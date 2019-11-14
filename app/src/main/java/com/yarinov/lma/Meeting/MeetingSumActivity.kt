package com.yarinov.lma.Meeting

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yarinov.lma.R


class MeetingSumActivity : AppCompatActivity() {

    private var person: TextView? = null
    private var place: TextView? = null
    private var date: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_sum)

        person = findViewById<TextView>(R.id.sumPerson)
        date = findViewById<TextView>(R.id.sumDate)
        place = findViewById<TextView>(R.id.sumPlace)

        //Get the category from the last activity
        val extras = intent.extras
        person!!.setText(extras!!.getString("thePerson"))
        date!!.setText(extras!!.getString("theDate"))
        place!!.setText(extras!!.getString("thePlace"))
    }
}