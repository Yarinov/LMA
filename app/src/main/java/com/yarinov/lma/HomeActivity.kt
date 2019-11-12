package com.yarinov.lma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)




    }


    fun createGroupSectionOpen(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    fun setupMeetingSectionOpen(view: View) {
        val intent = Intent(this, SetupMeetingActivity::class.java)
        startActivity(intent)
    }

    fun tecSectionOpen(view: View) {
        val intent = Intent(this, TechActivity::class.java)
        startActivity(intent)

    }

    override fun onBackPressed() {
            super.onBackPressed()
    }
}
