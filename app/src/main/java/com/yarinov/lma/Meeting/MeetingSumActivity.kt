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
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import java.util.*
import kotlin.collections.HashMap


class MeetingSumActivity : AppCompatActivity() {

    private var personTextView: TextView? = null
    private var placeTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var timeTextView: TextView? = null

    private var friendId: String? = null
    private var fromUser: String? = null
    private var date: String? = null
    private var time: String? = null
    private var place: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_sum)

        personTextView = findViewById(R.id.sumPerson)
        dateTextView = findViewById(R.id.sumDate)
        timeTextView = findViewById(R.id.sumTIme)
        placeTextView = findViewById(R.id.sumPlace)

        //Get the category from the last activity
        val extras = intent.extras
        fromUser = extras!!.getString("theFriendName")
        date = extras!!.getString("theDate")
        place = extras!!.getString("thePlace")
        time = extras.getString("theTime")
        friendId = extras!!.getString("friendId")

        personTextView!!.setText(fromUser)
        dateTextView!!.setText(date)
        timeTextView!!.setText(time)
        placeTextView!!.setText(place)

    }

    fun sendMeeting(view: View) {

        var notificationId = UUID.randomUUID()
        var myId = FirebaseAuth.getInstance().currentUser!!.uid

        var receivedMeetingData = HashMap<String, String>()
        receivedMeetingData.put("from", myId)
        receivedMeetingData.put("date", date!!)
        receivedMeetingData.put("time", time!!)
        receivedMeetingData.put("place", place!!)
        receivedMeetingData.put("status", "pending")
        receivedMeetingData.put("type", "received")

        val currentFriendNotificationsDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(friendId!!).child("Notifications").child(notificationId.toString())


        var sentMeetingData = HashMap<String, String>()
        sentMeetingData.put("to", friendId!!)
        sentMeetingData.put("date", date!!)
        sentMeetingData.put("time", time!!)
        sentMeetingData.put("place", place!!)
        sentMeetingData.put("status", "pending")
        sentMeetingData.put("type", "sent")

        val currentUserNotificationsDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(myId!!).child("Notifications").child(notificationId.toString())

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
