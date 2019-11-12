package com.yarinov.lma

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CreateGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
    }

    fun addFriendsSectionOpen(view: View) {
        val intent = Intent(this, ChooseFriendActivity::class.java)
        startActivity(intent)
    }
}
