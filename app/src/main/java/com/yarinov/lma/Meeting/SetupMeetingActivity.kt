package com.yarinov.lma.Meeting

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import com.yarinov.lma.User.User


class SetupMeetingActivity : AppCompatActivity() {

    private var contactList: ListView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var userFriendsObjectArrayList: ArrayList<User> = ArrayList()
    private var userFriendIdArrayList: ArrayList<String>? = null

    private var screenTitle: TextView? = null
    private var dateInput: EditText? = null
    private var contactSearchInput: EditText? = null
    private var locationInput: EditText? = null

    private var firstStepLayout: LinearLayout? = null
    private var secondStepLayout: LinearLayout? = null
    private var thirdStepLayout: LinearLayout? = null
    private var loadingLayout: LinearLayout? = null
    private var meetingLayout: LinearLayout? = null


    private var theFriendName: String? = null
    private var theFriendId: String? = null
    private var theDate: String? = null
    private var thePlace: String? = null

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_meeting)

        user = FirebaseAuth.getInstance().currentUser

        dateInput = findViewById(R.id.dateInput)
        contactSearchInput = findViewById(R.id.contactSearchInput)
        locationInput = findViewById(R.id.locationInput)

        loadingLayout = findViewById(R.id.loadingLayout)
        meetingLayout = findViewById(R.id.meetingLayout)

        firstStepLayout = findViewById(R.id.firstStepLayout)
        secondStepLayout = findViewById(R.id.secondStepLayout)
        thirdStepLayout = findViewById(R.id.thirdStepLayout)

        userFriendIdArrayList = ArrayList()
        contactList = findViewById<ListView>(R.id.contactList)


        loadUserFriendsData()

        //Filter contact list
        contactSearchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@SetupMeetingActivity.contactListAdapter!!.getFilter().filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })


        contactList!!.setOnItemClickListener { parent, view, position, id ->

            Toast.makeText(this, "Clicked item :" + " " + position, Toast.LENGTH_SHORT).show()

            theFriendName = userFriendsObjectArrayList!![position].getNames()
            theFriendId = userFriendsObjectArrayList!![position].getId()

            firstStepLayout!!.visibility = View.GONE
            secondStepLayout!!.visibility = View.VISIBLE
        }

        //Setup the date
        var calendar = findViewById<CalendarView>(R.id.calendarInput)

        calendar.setOnDateChangeListener(OnDateChangeListener { arg0, year, month, date ->

            theDate = "$date/${month.plus(1)}/$year"

            dateInput!!.setText("$date/${month.plus(1)}/$year")
        })

        thePlace = "Somewhere over the rainbow"

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
                        ContactListAdapter(this@SetupMeetingActivity, userFriendsObjectArrayList)
                    contactList!!.adapter = contactListAdapter

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
        secondStepLayout!!.visibility = View.GONE
        thirdStepLayout!!.visibility = View.VISIBLE
    }

    fun openMeetingSummry(view: View) {

        val location = locationInput?.text.toString()
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MeetingSumActivity::class.java)
        intent.putExtra("theFriendName", theFriendName)
        intent.putExtra("theDate", theDate)
        intent.putExtra("thePlace", location)
        intent.putExtra("friendId", theFriendId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
