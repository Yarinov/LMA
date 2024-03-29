package com.yarinov.lma

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.internal.NavigationMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yarinov.lma.Authentication.LoginActivity
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.Group.CreateGroupActivity
import com.yarinov.lma.Group.MyGroupsActivity
import com.yarinov.lma.Info.AboutActivity
import com.yarinov.lma.Meeting.SetupMeetingActivity
import com.yarinov.lma.Notification.HomeNotification
import com.yarinov.lma.Notification.NotificationAdapter
import com.yarinov.lma.User.FriendsActivity
import de.hdodenhof.circleimageview.CircleImageView
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter


class HomeActivity : AppCompatActivity() {

    var loadingLayout: LinearLayout? = null
    var homeLayout: LinearLayout? = null
    var userNameTitle: TextView? = null
    var profilePic: CircleImageView? = null
    var noActivityText: TextView? = null
    var userNotificationList: RecyclerView? = null

    var user: FirebaseUser? = null
    var post: String? = null

    var mStorage: StorageReference? = null


    val PICK_IMAGE = 1
    var imageUri: Uri? = null

    private var notificationListAdapter: NotificationAdapter? = null
    private var notificationArrayList: ArrayList<HomeNotification> = ArrayList()

    override fun onStart() {
        super.onStart()

        user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // User is not signed in
            val i = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loadingLayout = findViewById(R.id.loadingLayout)
        homeLayout = findViewById(R.id.homeLayout)
        profilePic = findViewById(R.id.profileImageHome)
        noActivityText = findViewById(R.id.noActivityText)
        userNotificationList = findViewById(R.id.userNotificationList)

        var popupMenu = findViewById<FabSpeedDial>(R.id.menuPopup)

        mStorage = FirebaseStorage.getInstance().reference.child("Images")

        //Disable home layout till data load
        homeLayout?.visibility = View.GONE

        //Get current user from auth and from database
        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            var userId = user!!.uid

            val currentUserRootDatabase =
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(userId)

            userNameTitle = findViewById(R.id.userNameTitle)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var myName = dataSnapshot.child("Name").value
                    userNameTitle!!.text = "Hello $myName!"
                    userNameTitle!!.isSelected = true

                    //If the user have a profile pic
                    if (dataSnapshot.child("imgUri").value!!.equals("true")) {

                        val storage = FirebaseStorage.getInstance()

                        val gsReference =
                            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/$userId.jpg")


                        GlideApp.with(applicationContext).asBitmap().load(gsReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate().into(object : SimpleTarget<Bitmap?>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap?>?
                                ) {
                                    profilePic!!.setImageBitmap(resource)
                                }

                            })


                    }


                    //Get all the user notifications
                    loadNotification(userId, myName)

                    //Disable loading animation and display the home layout
                    if (loadingLayout?.visibility == View.VISIBLE) {
                        homeLayout?.visibility = View.VISIBLE
                        loadingLayout?.visibility = View.GONE
                        noActivityText!!.visibility = View.VISIBLE
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...
                }
            }
            currentUserRootDatabase.addValueEventListener(postListener)


        }

        popupMenu.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {

                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.title) {

                    "Logout" -> {
                        showLogoutPopup()
                    }

                    "About" -> {
                        aboutOpen()
                    }

                    "My Groups" -> {
                        myGroupsOpen()
                    }

                }
                return false
            }


        })


        notificationArrayList = ArrayList()
        notificationListAdapter = NotificationAdapter(this, notificationArrayList)

        userNotificationList!!.setHasFixedSize(true)
        userNotificationList!!.layoutManager = LinearLayoutManager(this)
        userNotificationList!!.adapter = notificationListAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            imageUri = data!!.data
        }
    }

    fun changeProfilePic(view: View) {
        var picIntent = Intent()
        picIntent.type = "image/*"
        picIntent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(picIntent, "Choose Photo"), PICK_IMAGE)


        if (imageUri !=null){
            var userProfileImageStorageReference = mStorage!!.child(user!!.uid + ".jpg")

            userProfileImageStorageReference.putFile(imageUri!!).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    var downloadUri =
                        task.result!!.storage.downloadUrl.toString()

                    GlideApp.with(applicationContext).asBitmap().load(downloadUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .dontAnimate().into(object : SimpleTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                profilePic!!.setImageBitmap(resource)
                            }

                        })
                }

            }

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(user!!.uid).child("imgUri").setValue(true)
        }


    }


    fun createGroupSectionOpen(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    fun aboutOpen() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    fun myGroupsOpen() {
        val intent = Intent(this, MyGroupsActivity::class.java)
        startActivity(intent)
    }

    fun setupMeetingSectionOpen(view: View) {
        val intent = Intent(this, SetupMeetingActivity::class.java)
        startActivity(intent)
    }

    fun friendsSectionOpen(view: View) {
        val intent = Intent(this, FriendsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        minimizeApp()
    }

    private fun minimizeApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun showLogoutPopup() {
        val alert = AlertDialog.Builder(this@HomeActivity)
        alert.setMessage("Are you sure?")
            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                logout() // Last step. Logout function
            }).setNegativeButton("Cancel", null)

        val alert1 = alert.create()
        alert1.show()
    }

    private fun logout() {

        //Get a user uid and user database root to remove the token id from this user before logout
        var user_id = user!!.uid
        val currentUserTokenIdDb =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(user_id).child("tokenId")

        currentUserTokenIdDb.setValue("").addOnSuccessListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun loadNotification(userId: String, myName: Any?) {


        val currentUserNotificationDatabase =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(userId).child("Notifications")

        //Set home activity according to the user details
        val notificationsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                notificationArrayList.clear()
                //Get all user friend
                for (childDataSnapshot in dataSnapshot.children) {

                    var notificationId = childDataSnapshot.key
                    val notificationType = childDataSnapshot.child("type").value
                    var groupAffecting = childDataSnapshot.child("groupAffiliation").value

                    var friendId = ""
                    var groupId = ""
                    var groupName = ""

                    if (notificationType!!.equals("sent")) {

                        if (groupAffecting!!.equals("none")) {
                            friendId = childDataSnapshot.child("to").value as String
                        } else {
                            groupId = groupAffecting.toString()
                            groupName = childDataSnapshot.child("to").value as String

                        }
                    } else {//Received

                        friendId = childDataSnapshot.child("from").value as String

                        if (!groupAffecting!!.equals("none")) {
                            groupId = groupAffecting.toString()
                            groupName = childDataSnapshot.child("groupName").value as String
                        }
                    }
                    var date = childDataSnapshot.child("date").value
                    var place = childDataSnapshot.child("place").value
                    var time = childDataSnapshot.child("time").value
                    println(time)
                    var status = childDataSnapshot.child("status").value

                    var datePosted = childDataSnapshot.child("datePosted").value

                    //Get Friend Name
                    val currentUserFriendDatabase =
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(friendId).child("Name")

                    val getUserNameListener = object : ValueEventListener {

                        override fun onDataChange(p0: DataSnapshot) {
                            var toName = p0.value

                            var notificationObject: HomeNotification

                            //If - this is 1-to-1 meeting
                            if (groupAffecting.equals("none")) {
                                notificationObject = HomeNotification(
                                    notificationId.toString(),
                                    userId,
                                    myName.toString(),
                                    friendId,
                                    toName.toString(),
                                    date.toString(),
                                    time.toString(),
                                    place.toString(),
                                    notificationType.toString(),
                                    status.toString(),
                                    "Single",
                                    datePosted.toString()
                                )
                            } else { // Else - this is group meeting

                                if (notificationType == "sent") { // If - this is a group meeting I set up
                                    notificationObject = HomeNotification(
                                        notificationId.toString(),
                                        userId,
                                        myName.toString(),
                                        groupId,
                                        groupName,
                                        date.toString(),
                                        time.toString(),
                                        place.toString(),
                                        notificationType.toString(),
                                        status.toString(),
                                        "Group",
                                        datePosted.toString()
                                    )
                                } else { // Else - I didn't set this group meeting
                                    notificationObject = HomeNotification(
                                        notificationId.toString(),
                                        friendId,
                                        toName.toString(),
                                        groupId,
                                        groupName,
                                        date.toString(),
                                        time.toString(),
                                        place.toString(),
                                        notificationType.toString(),
                                        status.toString(),
                                        "Group",
                                        datePosted.toString()
                                    )
                                }
                            }

                            //Add this notification to the notification array
                            notificationArrayList.add(notificationObject)
                            notificationListAdapter!!.sortByAsc()

                            notificationListAdapter!!.notifyDataSetChanged()

                            userNotificationList!!.visibility = View.VISIBLE
                            noActivityText!!.visibility = View.GONE
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    }

                    currentUserFriendDatabase.addValueEventListener(getUserNameListener)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...

            }
        }

        currentUserNotificationDatabase.addValueEventListener(notificationsListener)
    }


}

