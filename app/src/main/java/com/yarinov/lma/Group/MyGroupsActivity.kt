package com.yarinov.lma.Group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R
import kotlinx.android.synthetic.main.group_card_item.*

class MyGroupsActivity : AppCompatActivity() {

    var myGroupsRecyclerView: RecyclerView? = null
    var groupsArrayList: ArrayList<Group>? = null
    var groupListAdapter: GroupsListAdapter? = null

    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_groups)

        myGroupsRecyclerView = findViewById(R.id.myGroupsRecyclerView)

        currentUser = FirebaseAuth.getInstance().currentUser

        groupsArrayList = ArrayList()

        groupListAdapter =
            GroupsListAdapter(this, groupsArrayList!!)

        myGroupsRecyclerView!!.setHasFixedSize(true)
        myGroupsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        myGroupsRecyclerView!!.adapter = groupListAdapter


        getUserGroups()
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
}
