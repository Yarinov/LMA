package com.yarinov.lma.Meeting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.lma.R
import java.util.*
import kotlin.collections.HashMap


class MeetingSumActivity : AppCompatActivity() {

    private var personTextView: TextView? = null
    private var placeTextView: TextView? = null
    private var dateTextView: TextView? = null

    private var friendId: String? = null
    private var fromUser: String? = null
    private var date: String? = null
    private var place: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_sum)

        personTextView = findViewById<TextView>(R.id.sumPerson)
        dateTextView = findViewById<TextView>(R.id.sumDate)
        placeTextView = findViewById<TextView>(R.id.sumPlace)

        //Get the category from the last activity
        val extras = intent.extras
        fromUser = extras!!.getString("theFriendName")
        date = extras!!.getString("theDate")
        place = extras!!.getString("thePlace")
        friendId = extras!!.getString("friendId")

        personTextView!!.setText(fromUser)
        dateTextView!!.setText(date)
        placeTextView!!.setText(place)

    }

    fun sendMeeting(view: View) {

        var receivedNotificationId = UUID.randomUUID()
        var sentNotificationId = UUID.randomUUID()
        var myId = FirebaseAuth.getInstance().currentUser!!.uid

        var receivedMeetingData = HashMap<String, String>()
        receivedMeetingData.put("from", myId)
        receivedMeetingData.put("date", date!!)
        receivedMeetingData.put("place", place!!)
        receivedMeetingData.put("type", "received")

        val currentFriendNotificationsDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(friendId!!).child("Notifications").child(receivedNotificationId.toString())


        var sentMeetingData = HashMap<String, String>()
        sentMeetingData.put("to", friendId!!)
        sentMeetingData.put("date", date!!)
        sentMeetingData.put("place", place!!)
        sentMeetingData.put("type", "sent")

        val currentUserNotificationsDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(myId!!).child("Notifications").child(sentNotificationId.toString())

        currentFriendNotificationsDb.setValue(receivedMeetingData).addOnSuccessListener {
            // Write was successful!
            // ...

            currentUserNotificationsDb.setValue(sentMeetingData).addOnSuccessListener {
                Toast.makeText(this, "MSG SENT!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
            .addOnFailureListener {
                // Write failed
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT)
            }


    }
}
