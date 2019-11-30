package com.yarinov.lma.Meeting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MeetingSumActivity : AppCompatActivity() {

    private var personTextView: TextView? = null
    private var placeTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var timeTextView: TextView? = null

    private var friendId: String? = null
    private var fromUser: String? = null

    private var groupId: String? = null
    private var groupName: String? = null

    private var date: String? = null
    private var time: String? = null
    private var place: String? = null

    private var meetingMood: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_sum)

        personTextView = findViewById(R.id.sumPerson)
        dateTextView = findViewById(R.id.sumDate)
        timeTextView = findViewById(R.id.sumTIme)
        placeTextView = findViewById(R.id.sumPlace)

        //Get the category from the last activity
        val extras = intent.extras
        meetingMood = extras!!.getBoolean("meetingMood")

        if (meetingMood!!) {
            fromUser = extras.getString("friendName")
            friendId = extras.getString("friendId")
        } else {
            groupName = extras.getString("groupName")
            groupId = extras.getString("groupId")
        }
        date = extras.getString("theDate")
        place = extras.getString("thePlace")
        time = extras.getString("theTime")


        if (meetingMood!!)
            personTextView!!.setText(fromUser)
        else
            personTextView!!.setText(groupName)
        dateTextView!!.setText(date)
        timeTextView!!.setText(time)
        placeTextView!!.setText(place)

    }

    fun sendMeeting(view: View) {

        var currentDate = Calendar.getInstance().getTime()

        var notificationId = UUID.randomUUID()
        var myId = FirebaseAuth.getInstance().currentUser!!.uid


        var receivedMeetingData = HashMap<String, String>()
        receivedMeetingData.put("datePosted", currentDate.toString())
        receivedMeetingData.put("from", myId)
        receivedMeetingData.put("date", date!!)
        receivedMeetingData.put("time", time!!)
        receivedMeetingData.put("place", place!!)
        receivedMeetingData.put("status", "pending")
        receivedMeetingData.put("type", "received")

        if (meetingMood!!) {
            receivedMeetingData.put("groupAffiliation", "none")
        } else {
            receivedMeetingData.put("groupName", groupName!!)
            receivedMeetingData.put("groupAffiliation", groupId!!)
        }

        var sentMeetingData = HashMap<String, String>()
        sentMeetingData.put("datePosted", currentDate.toString())
        sentMeetingData.put("date", date!!)
        sentMeetingData.put("time", time!!)
        sentMeetingData.put("place", place!!)
        sentMeetingData.put("status", "pending")
        sentMeetingData.put("type", "sent")
        if (meetingMood!!) {
            sentMeetingData.put("to", friendId!!)
            sentMeetingData.put("groupAffiliation", "none")
        } else {
            sentMeetingData.put("to", groupName!!)
            sentMeetingData.put("groupAffiliation", groupId!!)
        }

        val currentUserNotificationsDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(myId!!).child("Notifications").child(notificationId.toString())

        if (meetingMood!!) {

            val currentFriendNotificationsDb =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(friendId!!).child("Notifications").child(notificationId.toString())


            currentFriendNotificationsDb.setValue(receivedMeetingData).addOnSuccessListener {
                // Write was successful!
                // ...

                currentUserNotificationsDb.setValue(sentMeetingData).addOnSuccessListener {
                    Toast.makeText(this, "MSG SENT!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
                .addOnFailureListener {
                    // Write failed
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT)
                }
        } else {

            //Get all the group members
            val groupsDb =
                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId!!)
                    .child("groupMembers")


            var membersMeetingStatusMap = HashMap<String, String>()
            var groupMembers = ArrayList<String>()

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    groupMembers.clear()

                    //Get all user groups
                    for (childDataSnapshot in dataSnapshot.children) {
                        val groupMemberData = childDataSnapshot.key

                        if (groupMemberData != myId)
                            membersMeetingStatusMap[groupMemberData.toString()] = "pending"

                        else
                            membersMeetingStatusMap[groupMemberData.toString()] = "accept"

                        if (childDataSnapshot.value as Boolean && !groupMemberData!!.equals(myId)) {
                            groupMembers.add(groupMemberData.toString())
                        }
                    }


                    FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId!!)
                        .child("Meetings").child(notificationId.toString()).setValue(membersMeetingStatusMap)

                    sendMeetingToGroupMembers(groupMembers, notificationId, receivedMeetingData)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            groupsDb.addValueEventListener(postListener)



            //Write to current user notification
            currentUserNotificationsDb.setValue(sentMeetingData).addOnSuccessListener {
                Toast.makeText(this, "MSG SENT!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun sendMeetingToGroupMembers(
        groupMembers: ArrayList<String>,
        notificationId: UUID,
        receivedMeetingData: HashMap<String, String>
    ) {

        for (memberId in groupMembers) {
            val currentFriendNotificationsDb =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(memberId).child("Notifications").child(notificationId.toString())


            currentFriendNotificationsDb.setValue(receivedMeetingData)
        }

    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this@MeetingSumActivity)
        alert.setMessage("Going back will discard any progress done until now, Are you sure?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }).setNegativeButton("Cancel", null)

        val alert1 = alert.create()
        alert1.show()
    }
}
