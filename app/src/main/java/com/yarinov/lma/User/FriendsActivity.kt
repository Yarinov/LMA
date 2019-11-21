package com.yarinov.lma.User

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.Meeting.ContactListAdapter
import com.yarinov.lma.R

class FriendsActivity : AppCompatActivity() {

    private var myFriendsLabel: TextView? = null
    private var friendRequestsLabel: TextView? = null
    private var addFriendLabel: TextView? = null

    private var friendsViewPager: ViewPager? = null
    private var fViewPagerAdapter: FriendsPagerViewAdapter? = null

    private var friendRequestsList: ListView? = null

    var user: FirebaseUser? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        myFriendsLabel = findViewById(R.id.myFriendsLabel)
        friendRequestsLabel = findViewById(R.id.friendRequestsLabel)
        addFriendLabel = findViewById(R.id.addFriendLabel)

        friendsViewPager = findViewById(R.id.friendsViewPager)
        friendsViewPager!!.offscreenPageLimit = 2

        fViewPagerAdapter = FriendsPagerViewAdapter(supportFragmentManager)
        friendsViewPager!!.adapter = fViewPagerAdapter

        friendsViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                changeTabs(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        myFriendsLabel!!.setOnClickListener { friendsViewPager!!.currentItem = 0 }
        friendRequestsLabel!!.setOnClickListener { friendsViewPager!!.currentItem = 1 }
        addFriendLabel!!.setOnClickListener { friendsViewPager!!.currentItem = 2 }

        val inflatedView = layoutInflater.inflate(R.layout.friend_request_layout, null)
        friendRequestsList = inflatedView.findViewById(R.id.friendRequestListView)

        user = FirebaseAuth.getInstance().currentUser



    }

    private fun changeTabs(position: Int) {

        if(position == 0){

            myFriendsLabel!!.setTextColor(getColor(R.color.White))
            myFriendsLabel!!.textSize = 22F

            friendRequestsLabel!!.setTextColor(getColor(R.color.Gray))
            friendRequestsLabel!!.textSize = 16F

            addFriendLabel!!.setTextColor(getColor(R.color.Gray))
            addFriendLabel!!.textSize = 16F

        }

        if(position == 1){

            myFriendsLabel!!.setTextColor(getColor(R.color.Gray))
            myFriendsLabel!!.textSize = 16F

            friendRequestsLabel!!.setTextColor(getColor(R.color.White))
            friendRequestsLabel!!.textSize = 22F

            addFriendLabel!!.setTextColor(getColor(R.color.Gray))
            addFriendLabel!!.textSize = 16F

        }

        if(position == 2){

            myFriendsLabel!!.setTextColor(getColor(R.color.Gray))
            myFriendsLabel!!.textSize = 16F

            friendRequestsLabel!!.setTextColor(getColor(R.color.Gray))
            friendRequestsLabel!!.textSize = 16F

            addFriendLabel!!.setTextColor(getColor(R.color.White))
            addFriendLabel!!.textSize = 22F

        }


    }


}
