package com.yarinov.lma.Meeting

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import kotlin.collections.ArrayList


class ChooseFriendActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private var contactList: ListView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var userArrayList: ArrayList<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friend)

        loadContacts()

        contactList = findViewById<ListView>(R.id.contactList)



    }


    private fun loadContacts() {



    }



}
