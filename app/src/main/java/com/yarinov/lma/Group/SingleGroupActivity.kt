package com.yarinov.lma.Group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.Meeting.ContactListAdapter
import com.yarinov.lma.Notification.Notification
import com.yarinov.lma.Notification.NotificationInGroupAdapter
import com.yarinov.lma.R
import com.yarinov.lma.User.User

class SingleGroupActivity : AppCompatActivity() {

    var groupId: String? = null
    var groupName: String? = null
    var groupDesc: String? = null

    var groupNameLabel: TextView? = null
    var groupDescLabel: TextView? = null

    var membersList: RecyclerView? = null

    var membersIdArrayList: ArrayList<String>? = null
    var groupMembersObjectArrayList: ArrayList<User>? = null
    private var contactListAdapter: ContactListAdapter? = null

    var meetingsList: RecyclerView? = null

    var meetingsIdArrayList: ArrayList<String>? = null
    var groupMeetingsObjectArrayList: ArrayList<Notification>? = null
    private var meetingListAdapter: NotificationInGroupAdapter? = null

    var loadingLayout: LinearLayout? = null
    var mainSingleGroupLayout: LinearLayout? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_group)

        groupNameLabel = findViewById(R.id.groupNameLabel)
        groupDescLabel = findViewById(R.id.groupDescText)

        membersList = findViewById(R.id.membersList)
        meetingsList = findViewById(R.id.meetingsList)

        loadingLayout = findViewById(R.id.loadingLayout)
        mainSingleGroupLayout = findViewById(R.id.mainSingleGroupLayout)


        //Get and set group data
        val extras = intent.extras

        groupId = extras!!.getString("groupId")
        groupName = extras.getString("groupName")
        groupDesc = extras.getString("groupDesc")

        groupNameLabel!!.text = groupName
        groupDescLabel!!.text = groupDesc

        groupMembersObjectArrayList = ArrayList()
        membersIdArrayList = ArrayList()

        meetingsIdArrayList = ArrayList()
        groupMeetingsObjectArrayList = ArrayList()

        loadGroupMembers()
        loadGroupMeetings()

        contactListAdapter =
            ContactListAdapter(this, groupMembersObjectArrayList!!)

        membersList!!.setHasFixedSize(true)
        membersList!!.layoutManager = LinearLayoutManager(this)
        membersList!!.adapter = contactListAdapter


        meetingListAdapter =
            NotificationInGroupAdapter(this, groupMeetingsObjectArrayList!!, groupId!!)

        meetingsList!!.setHasFixedSize(true)
        meetingsList!!.layoutManager = LinearLayoutManager(this)
        meetingsList!!.adapter = meetingListAdapter

    }

    private fun loadGroupMeetings() {
        meetingsIdArrayList!!.clear()

        val currentGroupRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId!!).child("Meetings")

        var getAllGroupMeetingsIdListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                meetingsIdArrayList!!.clear()

                for (meetingId in p0.children){
                    meetingsIdArrayList!!.add(meetingId.key.toString())
                }

                loadMeetingsToAdapter()
            }

        }

        currentGroupRootDatabase.addValueEventListener(getAllGroupMeetingsIdListener)


    }

    private fun loadMeetingsToAdapter() {
        groupMeetingsObjectArrayList!!.clear()

        for (meetingId in meetingsIdArrayList!!){
            var currentGroupMeetingRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Groups")
                    .child(groupId!!).child("Meetings").child(meetingId)

            var getMeetingDataListener = object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var fromId = p0.child("from").value
                    var date = p0.child("date").value
                    var place = p0.child("place").value
                    var time = p0.child("time").value
                    var datePosted = p0.child("datePosted").value

                    var tempNotification = Notification(meetingId,
                        fromId.toString(), date.toString(), time.toString(),
                        place.toString(), datePosted.toString()
                    )

                    println(tempNotification)

                    groupMeetingsObjectArrayList!!.add(tempNotification)

                    meetingListAdapter!!.notifyDataSetChanged()

                }

            }

            currentGroupMeetingRootDatabase.addValueEventListener(getMeetingDataListener)
        }

    }

    private fun loadGroupMembers() {
        membersIdArrayList = ArrayList()

        val currentGroupRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId!!).child("groupMembers")

        var getMembersListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                membersIdArrayList!!.clear()

                for (memberId in p0.children){
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

        for (memberId in membersIdArrayList!!){

            var currentGroupMemberRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(memberId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    var userTemp:User = if (memberId == currentUserId){
                        User("You", memberId)
                    }else{
                        User(dataSnapshot.child("Name").value as String, memberId)

                    }
                    groupMembersObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data
                    contactListAdapter!!.notifyDataSetChanged()
                    loadingLayout!!.visibility = View.GONE
                    mainSingleGroupLayout!!.visibility = View.VISIBLE


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

        var intent = Intent(this, MyGroupsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
