package com.yarinov.lma.User

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
import com.yarinov.lma.R

class AddFriendActivity : AppCompatActivity() {

    private var usersList: ListView? = null
    private var usersListAdapter: usersListAdapter? = null
    private var usersIdArrayList: ArrayList<String> = ArrayList()
    private var userFriendsIdArrayList: ArrayList<String> = ArrayList()
    private var usersObjectArrayList: ArrayList<User> = ArrayList()

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        usersList = findViewById(R.id.usersListView)

        user = FirebaseAuth.getInstance().currentUser

        loadUsers()

    }

    private fun loadUsers() {

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
                    userFriendsIdArrayList!!.add(userFriendData!!)
                    //contactListAdapter!!.notifyDataSetChanged()
                }

                removeFriendsFromUsers()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...

            }
        }
        currentUserFriendRootDatabase.addValueEventListener(postListener)
    }

    private fun removeFriendsFromUsers() {

        var currentUserId = user!!.uid

        val currentUsersRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Users")

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {
                    val userId = childDataSnapshot.key
                    if (!userId.equals(currentUserId) && !userFriendsIdArrayList.contains(userId))
                        usersIdArrayList!!.add(userId!!)
                    //contactListAdapter!!.notifyDataSetChanged()
                }

                 loadUsersToAdapter()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...

            }
        }
        currentUsersRootDatabase.addValueEventListener(postListener)
    }

    private fun loadUsersToAdapter() {

        for (userId in usersIdArrayList!!) {

            var currentUserFriendRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(userId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var userTemp = User(dataSnapshot.child("Name").value as String, userId)
                    usersObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data
                    usersListAdapter =
                        usersListAdapter(this@AddFriendActivity, usersObjectArrayList)
                    usersList!!.adapter = usersListAdapter

                    usersListAdapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            currentUserFriendRootDatabase.addValueEventListener(postListener)


        }
    }


}
