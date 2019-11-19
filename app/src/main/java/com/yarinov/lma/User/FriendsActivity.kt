package com.yarinov.lma.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.Meeting.ContactListAdapter
import com.yarinov.lma.R

class FriendsActivity : AppCompatActivity() {

    private var friendsList: ListView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var userFriendIdArrayList: ArrayList<String> = ArrayList()
    private var userFriendsObjectArrayList: ArrayList<User> = ArrayList()

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendsList = findViewById(R.id.myFriendsList)

        user = FirebaseAuth.getInstance().currentUser

        loadUserFriendsData()


    }

    private fun loadUserFriendsData() {

        var userId = user!!.uid


        val currentUserFriendRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(userId)

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {
                    val userFriendData = childDataSnapshot.key
                    userFriendIdArrayList!!.add(userFriendData!!)
                    //contactListAdapter!!.notifyDataSetChanged()
                }

                loadContactsToAdapter()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...

            }
        }
        currentUserFriendRootDatabase.addValueEventListener(postListener)

    }

    private fun loadContactsToAdapter() {

        for (userFriendId in userFriendIdArrayList!!) {

            var currentUserFriendRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(userFriendId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var userTemp = User(dataSnapshot.child("Name").value as String, userFriendId)
                    userFriendsObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data
                    contactListAdapter =
                        ContactListAdapter(this@FriendsActivity, userFriendsObjectArrayList)
                    friendsList!!.adapter = contactListAdapter

                    contactListAdapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            currentUserFriendRootDatabase.addValueEventListener(postListener)


        }

    }

    fun addFriendSection(view: View) {
        var toAddFriendActivity = Intent(this, AddFriendActivity::class.java)
        startActivity(toAddFriendActivity)
    }
}
