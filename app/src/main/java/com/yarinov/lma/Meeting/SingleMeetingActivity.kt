package com.yarinov.lma.Meeting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.processbutton.iml.ActionProcessButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.Group.SingleGroupActivity
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import com.yarinov.lma.User.User

class SingleMeetingActivity : AppCompatActivity() {

    var groupId: String? = null
    var meetingId: String? = null

    var fromActivity:String? = null

    var acceptButton: ActionProcessButton? = null
    var denyButton: ActionProcessButton? = null

    var membersStatusList: RecyclerView? = null

    var membersIdArrayList: ArrayList<String>? = null
    var groupMembersObjectArrayList: ArrayList<User>? = null
    var memberInStatusListAdapter: MemberInStatusListAdapter? = null

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_meeting)

        user = FirebaseAuth.getInstance().currentUser

        //Get and set group data
        val extras = intent.extras

        groupId = extras!!.getString("groupId")
        meetingId = extras.getString("meetingId")
        fromActivity = extras.getString("fromActivity")

        membersStatusList = findViewById(R.id.membersStatusList)
        acceptButton = findViewById(R.id.acceptButton)
        denyButton = findViewById(R.id.denyButton)


        membersIdArrayList = ArrayList()
        groupMembersObjectArrayList = ArrayList()

        loadMembersStatus()

        memberInStatusListAdapter =
            MemberInStatusListAdapter(this, groupMembersObjectArrayList!!, meetingId!!)

        membersStatusList!!.setHasFixedSize(true)
        membersStatusList!!.layoutManager = LinearLayoutManager(this)
        membersStatusList!!.adapter = memberInStatusListAdapter

        acceptButton!!.setOnClickListener {

            var newStatus = "accept"

            //Write in the group db status
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId!!).child("Meetings").child(meetingId!!).child("membersStatus")
                .child(user!!.uid).setValue(newStatus)

            //Write in the user db status
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user!!.uid).child("Notifications").child(meetingId!!).child("status")
                .setValue(newStatus)

            membersIdArrayList = ArrayList()
            groupMembersObjectArrayList = ArrayList()


        }

        denyButton!!.setOnClickListener {

            var newStatus = "deny"

            //Write in the group db status
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId!!).child("Meetings").child(meetingId!!).child("membersStatus")
                .child(user!!.uid).setValue(newStatus)

            //Write in the user db status
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user!!.uid).child("Notifications").child(meetingId!!).child("status")
                .setValue(newStatus)

            membersIdArrayList = ArrayList()
            groupMembersObjectArrayList = ArrayList()


        }

    }

    private fun loadMembersStatus() {

        membersIdArrayList!!.clear()
        val currentGroupRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId!!).child("groupMembers")

        var getMembersListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                membersIdArrayList!!.clear()

                for (memberId in p0.children) {
                    membersIdArrayList!!.add(memberId.key.toString())
                }

                loadEachMemberData()
            }

        }

        currentGroupRootDatabase.addValueEventListener(getMembersListener)
    }

    private fun loadEachMemberData() {

        groupMembersObjectArrayList!!.clear()

        var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        for (memberId in membersIdArrayList!!) {

            var currentGroupMemberRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(memberId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    var userTemp: User = if (memberId == currentUserId) {
                        User("You", memberId)
                    } else {
                        User(dataSnapshot.child("Name").value as String, memberId)

                    }
                    groupMembersObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data
                    memberInStatusListAdapter!!.notifyDataSetChanged()


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            currentGroupMemberRootDatabase.addValueEventListener(postListener)


        }
    }

    override fun onBackPressed() {

        if (fromActivity == "singleGroupActivity"){
             var intent = Intent(this, SingleGroupActivity::class.java)

        }else{
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
