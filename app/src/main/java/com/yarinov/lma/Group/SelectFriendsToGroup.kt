package com.yarinov.lma.Group

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R


class SelectFriendsToGroup : AppCompatActivity() {

    var user: FirebaseUser? = null

    private var userSearchInput: EditText? = null

    private var loadingLayout: LinearLayout? = null
    private var selectFriendLayout: LinearLayout? = null


    private var friendsList: RecyclerView? = null
    private var userFriendIdArrayList: ArrayList<String> = ArrayList()
    private var userFriendsObjectArrayList: ArrayList<MultiSelectUser> = ArrayList()
    private var selectedFriendsArrayList: ArrayList<MultiSelectUser> = ArrayList()
    private var friendsListAdapter: MultiSelectRecyclerAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_friends_to_group)

        friendsList = findViewById(R.id.myFriendsList)
        userSearchInput = findViewById(R.id.userSearchInput)

        loadingLayout = findViewById(R.id.loadingLayout)
        selectFriendLayout = findViewById(R.id.selectFriendLayout)


        val extras = intent.extras

        userFriendsObjectArrayList = try {
            (extras!!.getSerializable("selectedFriends") as ArrayList<MultiSelectUser>?)!!

        } catch (e: KotlinNullPointerException) {
            ArrayList()
        }

        friendsListAdapter =
            MultiSelectRecyclerAdapter(this, userFriendsObjectArrayList)

        friendsList!!.setHasFixedSize(true)
        friendsList!!.layoutManager = LinearLayoutManager(this)
        friendsList!!.adapter = friendsListAdapter


        friendsList!!.addOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {


                if (!selectedFriendsArrayList.contains(friendsListAdapter!!.getItem(position))) {
                    selectedFriendsArrayList.add(friendsListAdapter!!.getItem(position))
                    friendsListAdapter!!.getItem(position).isSelected = true

                    friendsListAdapter!!.notifyItemChanged(position)
                } else {
                    selectedFriendsArrayList.remove(friendsListAdapter!!.getItem(position))
                    friendsListAdapter!!.getItem(position).isSelected = false

                    friendsListAdapter!!.notifyItemChanged(position)
                }


            }
        })


        //Filter contact list
        userSearchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@SelectFriendsToGroup.friendsListAdapter!!.getFilter().filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

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
                    var userTemp = MultiSelectUser(
                        dataSnapshot.child("Name").value as String,
                        userFriendId,
                        false
                    )
                    if (!userFriendsObjectArrayList.contains(userTemp))
                        userFriendsObjectArrayList.add(userTemp)

                    loadingLayout!!.visibility = View.GONE
                    selectFriendLayout!!.visibility = View.VISIBLE

                    friendsListAdapter!!.sortByAsc()
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

    fun passSelectedUsers(view: View) {

        val intent = Intent(this, CreateGroupActivity::class.java)
        intent.putExtra("selectedFriends", selectedFriendsArrayList)
        startActivity(intent)
        finish()
    }

    //RecyclerView interface for item click
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                view?.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view?.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }


        })
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this@SelectFriendsToGroup)
        alert.setMessage("Going back will discard any selection done, Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->

                finish()
            }.setNegativeButton("Cancel", null)

        val alert1 = alert.create()
        alert1.show()
    }
}
