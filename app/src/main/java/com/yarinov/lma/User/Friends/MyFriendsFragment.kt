package com.yarinov.lma.User.Friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import com.yarinov.lma.User.FriendsListAdapter


class MyFriendsFragment : Fragment() {

    var user: FirebaseUser? = null

    private var friendsList: RecyclerView? = null
    private var userFriendIdArrayList: ArrayList<String> = ArrayList()
    private var userFriendsObjectArrayList: ArrayList<User> = ArrayList()
    private var friendsListAdapter: FriendsListAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_friends, container, false)

        friendsList = view.findViewById(R.id.myFriendsList)

        userFriendsObjectArrayList = ArrayList()

        friendsListAdapter =
            FriendsListAdapter(container!!.context, userFriendsObjectArrayList)

        friendsList!!.setHasFixedSize(true)
        friendsList!!.layoutManager = LinearLayoutManager(container.context)
        friendsList!!.adapter = friendsListAdapter

        // Inflate the layout for this fragment
        return view

    }

    override fun onStart() {
        super.onStart()


        userFriendIdArrayList.clear()

        loadUserFriendsData()
    }

    private fun loadUserFriendsData() {

        user = FirebaseAuth.getInstance().currentUser
        var userId = user!!.uid


        val currentUserFriendRootDatabase =
            FirebaseDatabase.getInstance().reference.child("Friends")
                .child(userId)

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                userFriendIdArrayList.clear()
                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {
                    val userFriendData = childDataSnapshot.key
                    if (childDataSnapshot.value as Boolean)
                        userFriendIdArrayList.add(userFriendData!!)
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

        userFriendsObjectArrayList.clear()

        for (userFriendId in userFriendIdArrayList) {

            var currentUserFriendRootDatabase =
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(userFriendId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var userTemp = User(dataSnapshot.child("Name").value as String, userFriendId)
                    if (!userFriendsObjectArrayList.contains(userTemp))
                        userFriendsObjectArrayList.add(userTemp)

                    friendsListAdapter!!.notifyDataSetChanged()
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
