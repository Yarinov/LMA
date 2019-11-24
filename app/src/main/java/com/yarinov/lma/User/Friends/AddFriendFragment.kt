package com.yarinov.lma.User.Friends


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.yarinov.lma.User.AddFriendListAdapter
import com.yarinov.lma.User.FriendsListAdapter
import com.yarinov.lma.User.User

/**
 * A simple [Fragment] subclass.
 */
class AddFriendFragment : Fragment() {

    var user: FirebaseUser? = null

    private var userSearchInput: EditText? = null

    private var friendsList: RecyclerView? = null
    private var userFriendIdArrayList: ArrayList<String> = ArrayList()
    private var usersIdArrayList: ArrayList<String> = ArrayList()
    private var userNonFriendsObjectArrayList: ArrayList<User> = ArrayList()
    private var friendsListAdapter: AddFriendListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)

        friendsList = view.findViewById(R.id.myFriendsList)
        userSearchInput = view.findViewById(R.id.userSearchInput)

        userNonFriendsObjectArrayList = ArrayList()

        friendsListAdapter =
            AddFriendListAdapter(container!!.context, userNonFriendsObjectArrayList)

        friendsList!!.setHasFixedSize(true)
        friendsList!!.layoutManager = LinearLayoutManager(container.context)
        friendsList!!.adapter = friendsListAdapter

        //Filter contact list
        userSearchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@AddFriendFragment.friendsListAdapter!!.getFilter().filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

        // Inflate the layout for this fragment
        return view
    }

    override fun onStart() {
        super.onStart()

        userFriendIdArrayList.clear()

        loadUserFriends()
    }

    private fun loadUserFriends() {

        user = FirebaseAuth.getInstance().currentUser
        var userId = user!!.uid

        userFriendIdArrayList.clear()

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

        usersIdArrayList.clear()

        val currentUsersRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Users")

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {
                    val userId = childDataSnapshot.key
                    if (!userId.equals(currentUserId) && !userFriendIdArrayList.contains(userId))
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

        userNonFriendsObjectArrayList.clear()

        for (userId in usersIdArrayList!!) {

            var currentUserFriendRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(userId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var userTemp = User(dataSnapshot.child("Name").value as String, userId)
                    userNonFriendsObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data

                    System.out.println(userNonFriendsObjectArrayList)
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
