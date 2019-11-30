package com.yarinov.lma.Meeting

import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
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
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog
import com.yarinov.lma.Group.Group
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SetupMeetingActivity : AppCompatActivity(), RangeTimePickerDialog.ISelectedTime {

    private var contactList: RecyclerView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var userFriendsObjectArrayList: ArrayList<User> = ArrayList()
    private var userFriendIdArrayList: ArrayList<String>? = null

    private var groupListAdapter: GroupListAdapter? = null
    private var userGroupsObjectArrayList: ArrayList<Group> = ArrayList()
    private var userGroupArrayList: ArrayList<String>? = null

    private var dateInput: EditText? = null
    private var timeInput: EditText? = null
    private var contactSearchInput: EditText? = null
    private var locationInput: EditText? = null

    private var firstStepLayout: LinearLayout? = null
    private var secondStepLayout: LinearLayout? = null
    private var thirdStepLayout: LinearLayout? = null
    private var loadingLayout: LinearLayout? = null
    private var meetingLayout: LinearLayout? = null


    private var theFriendName: String? = null
    private var theFriendId: String? = null

    private var theGroupName: String? = null
    private var theGroupId: String? = null

    private var theDate: String? = null
    private var thePlace: String? = null
    private var theTime: String? = null

    private var meetingMoodIcon: ImageView? = null
    private var meetingMood: Boolean? = null //True = Single, False = Group
    private var passTheFirstSection: Boolean? = null
    private var listTitle: TextView? = null

    var user: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_meeting)

        user = FirebaseAuth.getInstance().currentUser

        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)
        contactSearchInput = findViewById(R.id.contactSearchInput)
        locationInput = findViewById(R.id.locationInput)

        loadingLayout = findViewById(R.id.loadingLayout)
        meetingLayout = findViewById(R.id.meetingLayout)

        firstStepLayout = findViewById(R.id.firstStepLayout)
        secondStepLayout = findViewById(R.id.secondStepLayout)
        thirdStepLayout = findViewById(R.id.thirdStepLayout)

        meetingMoodIcon = findViewById(R.id.meetingMoodIcon)
        listTitle = findViewById(R.id.listTitle)
        meetingMood = true
        passTheFirstSection = false

        userFriendIdArrayList = ArrayList()
        userGroupArrayList = ArrayList()

        contactList = findViewById(R.id.contactList)


        userFriendsObjectArrayList = ArrayList()
        userGroupsObjectArrayList = ArrayList()

        contactListAdapter =
            ContactListAdapter(this, userFriendsObjectArrayList)

        groupListAdapter =
            GroupListAdapter(this, userGroupsObjectArrayList)

        contactList!!.setHasFixedSize(true)
        contactList!!.layoutManager = LinearLayoutManager(this)
        contactList!!.adapter = contactListAdapter

        loadUserFriendsData()
        loadUserGroupsData()

        //Filter contact list
        contactSearchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@SetupMeetingActivity.contactListAdapter!!.getFilter().filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })



        contactList!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {

                if (meetingMood!!){
                    theFriendName = userFriendsObjectArrayList[position].getNames()
                    theFriendId = userFriendsObjectArrayList[position].getId()
                }else{
                    theGroupName = userGroupsObjectArrayList[position].groupName
                    theGroupId = userGroupsObjectArrayList[position].groupId
                }

                firstStepLayout!!.visibility = View.GONE
                secondStepLayout!!.visibility = View.VISIBLE
                passTheFirstSection = true
            }
        })

        //Setup the date
        var calendar = findViewById<CalendarView>(R.id.calendarInput)

        calendar.setOnDateChangeListener(OnDateChangeListener { arg0, year, month, date ->

            theDate = "$date/${month.plus(1)}/$year"

            dateInput!!.setText("$date/${month.plus(1)}/$year")
        })

        thePlace = "Somewhere over the rainbow"

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

    private fun loadUserFriendsData() {

        var userId = user!!.uid

        userFriendIdArrayList!!.clear()

        val currentUserFriendRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(userId)

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                userFriendIdArrayList!!.clear()

                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {
                    val userFriendData = childDataSnapshot.key

                    if (childDataSnapshot.value as Boolean)
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

        userFriendsObjectArrayList.clear()

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
                    contactListAdapter!!.notifyDataSetChanged()
                    loadingLayout!!.visibility = View.GONE
                    meetingLayout!!.visibility = View.VISIBLE

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            currentUserFriendRootDatabase.addValueEventListener(postListener)


        }

    }

    private fun loadUserGroupsData() {

        var userId = user!!.uid

        userGroupArrayList!!.clear()

        val currentUserGroupRootDatabase =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(userId).child("Groups")

        //Set home activity according to the user details
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                userGroupArrayList!!.clear()

                //Get all user groups
                for (childDataSnapshot in dataSnapshot.children) {
                    val userGroupData = childDataSnapshot.key

                    if (childDataSnapshot.value as Boolean)
                        userGroupArrayList!!.add(userGroupData.toString())
                }


                loadGroupsToAdapter()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...

            }
        }
        currentUserGroupRootDatabase.addValueEventListener(postListener)

    }

    private fun loadGroupsToAdapter() {
        userGroupsObjectArrayList.clear()

        for (userGroupId in userGroupArrayList!!) {

            var currentUserFriendRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Groups")
                    .child(userGroupId)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var userTemp = Group(
                        dataSnapshot.key as String,
                        dataSnapshot.child("groupName").value as String,
                        dataSnapshot.child("groupDesc").value as String
                    )
                    userGroupsObjectArrayList!!.add(userTemp)

                    //Set the contact list adapter with all the data
                     groupListAdapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...

                }
            }
            currentUserFriendRootDatabase.addValueEventListener(postListener)


        }
    }


    fun toThirdStep(view: View) {

        var date = dateInput!!.text

        val inputDateFormatter = SimpleDateFormat("dd/MM/yyyy").parse(date.toString())

        val currentDate: Date = Calendar.getInstance().getTime()



        if (date.isNullOrBlank() || theTime.isNullOrBlank()) {
            Toast.makeText(this, "Invalid Date/Time", Toast.LENGTH_SHORT).show()
            return
        } else if (inputDateFormatter.compareTo(currentDate) < 0) {
            Toast.makeText(this, "You can't go back in time.", Toast.LENGTH_SHORT).show()
        } else {
            secondStepLayout!!.visibility = View.GONE
            thirdStepLayout!!.visibility = View.VISIBLE
        }
    }

    fun openMeetingSummry(view: View) {

        val location = locationInput?.text.toString()
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MeetingSumActivity::class.java)

        if (meetingMood!!){
            intent.putExtra("friendName", theFriendName)
            intent.putExtra("friendId", theFriendId)

        }else{
            intent.putExtra("groupName", theGroupName)
            intent.putExtra("groupId", theGroupId)
        }

        intent.putExtra("theDate", theDate)
        intent.putExtra("thePlace", location)
        intent.putExtra("theTime", theTime)
        intent.putExtra("meetingMood", meetingMood!!)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        val alert = AlertDialog.Builder(this@SetupMeetingActivity)
        alert.setMessage("Going back will delete any progress done, Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }.setNegativeButton("Cancel", null)

        val alert1 = alert.create()
        alert1.show()

    }

    fun selectTime(view: View) {
        val selectTimeRangeDialog = RangeTimePickerDialog()
        selectTimeRangeDialog.newInstance()
        selectTimeRangeDialog.setRadiusDialog(20) // Set radius of dialog (default is 50)

        selectTimeRangeDialog.setIs24HourView(true) // Indicates if the format should be 24 hours
        selectTimeRangeDialog.setColorTextButton(R.color.colorPrimary)
        selectTimeRangeDialog.setColorTabSelected(R.color.colorPrimaryDark)
        selectTimeRangeDialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary)
        selectTimeRangeDialog.setColorBackgroundHeader(R.color.colorPrimary) // Set Color of Background header dialog

        val fragmentManager: FragmentManager = fragmentManager
        selectTimeRangeDialog.show(fragmentManager, "")
    }

    override fun onSelectedTime(hourStart: Int, minuteStart: Int, hourEnd: Int, minuteEnd: Int) {
        theTime = "$hourStart:$minuteStart - $hourEnd:$minuteEnd"

        timeInput!!.setText(theTime)


    }

    fun changeMeetingMood(view: View) {

        if (passTheFirstSection!!) {

            val alert = AlertDialog.Builder(this@SetupMeetingActivity)
            alert.setMessage("Switching mood will delete any progress done, Are you sure?")
                .setPositiveButton("Yes") { dialog, which ->
                    startActivity(intent)
                    finish()
                }.setNegativeButton("Cancel", null)

            val alert1 = alert.create()
            alert1.show()

        } else {
            //Go from single mood to group mood
            if (meetingMood!!) {
                meetingMoodIcon!!.setImageResource(R.drawable.meeting_group_mood_ic)
                listTitle!!.text = "My Groups"

                contactList!!.adapter = groupListAdapter
                meetingMood = false
            }
            //Go from group mood to single mood
            else {
                meetingMoodIcon!!.setImageResource(R.drawable.meeting_single_mood_ic)
                listTitle!!.text = "My Friends"

                contactList!!.adapter = contactListAdapter
                meetingMood = true
            }
        }

    }


}
