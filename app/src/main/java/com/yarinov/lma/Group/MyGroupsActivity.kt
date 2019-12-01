package com.yarinov.lma.Group

import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R


class MyGroupsActivity : AppCompatActivity() {

    var myGroupsRecyclerView: RecyclerView? = null
    var groupsArrayList: ArrayList<Group>? = null
    var groupListAdapter: GroupsListAdapter? = null


    var searchBar: CardView? = null
    var searchBarOpenButton: ImageView ? = null
    var mainToolBarLayout: LinearLayout? = null
    var backSearchBarButton: Button? = null
    private var searchBarFlag:Boolean?= null //True - Search Bar Open | False - Search Bar Close

    var searchInput: EditText? = null


    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_groups)

        myGroupsRecyclerView = findViewById(R.id.myGroupsRecyclerView)

        mainToolBarLayout = findViewById(R.id.mainMyGroupsToolbar)
        searchBarOpenButton = findViewById(R.id.searchButton)
        searchBar = findViewById(R.id.searchBar)
        backSearchBarButton = findViewById(R.id.backSearchBarButton)
        searchInput = findViewById(R.id.searchInputInMyGroups)
        searchBarFlag = false

        currentUser = FirebaseAuth.getInstance().currentUser

        groupsArrayList = ArrayList()

        groupListAdapter =
            GroupsListAdapter(this, groupsArrayList!!)

        myGroupsRecyclerView!!.setHasFixedSize(true)
        myGroupsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        myGroupsRecyclerView!!.adapter = groupListAdapter



        getUserGroups()

        searchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@MyGroupsActivity.groupListAdapter!!.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })


        searchBarOpenButton!!.setOnClickListener {
            openSearchBar()
        }

        backSearchBarButton!!.setOnClickListener {
            closeSearchBar()
        }
    }

    private fun openSearchBar(){
        searchBarFlag = true
        mainToolBarLayout!!.visibility = View.GONE
        searchBar!!.visibility = View.VISIBLE
    }

    private fun closeSearchBar(){
        searchBarFlag = false
        mainToolBarLayout!!.visibility = View.VISIBLE
        searchBar!!.visibility = View.GONE
    }


    private fun getUserGroups() {

        val currentUserGroupsDatabase =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser!!.uid).child("Groups")

        var getUserGroupsListener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                groupsArrayList!!.clear()

                for (group in p0.children){
                    var  groupId = group.key.toString()
                    var groupName = ""
                    var groupDes = ""

                    //Get Group Name
                    val currentGroupDatabase =
                        FirebaseDatabase.getInstance().getReference().child("Groups")
                            .child(group.key.toString())

                    var groupNameListener = object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            groupName = p0.child("groupName").value.toString()
                            groupDes = p0.child("groupDesc").value.toString()

                            var tempGroupObject = Group(groupId, groupName, groupDes)
                            groupsArrayList!!.add(tempGroupObject)

                            groupListAdapter!!.notifyDataSetChanged()

                        }

                    }

                    currentGroupDatabase.addValueEventListener(groupNameListener)

                    //Add this group object to group array list
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

        currentUserGroupsDatabase.addValueEventListener(getUserGroupsListener)
    }

    override fun onBackPressed() {
        if (searchBarFlag!!){
            closeSearchBar()
        }else
            super.onBackPressed()
    }
}
