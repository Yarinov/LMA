package com.yarinov.lma

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.internal.NavigationMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.yarinov.lma.Authentication.LoginActivity
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.Info.AboutActivity
import com.yarinov.lma.Meeting.CreateGroupActivity
import com.yarinov.lma.Meeting.SetupMeetingActivity
import de.hdodenhof.circleimageview.CircleImageView
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter


class HomeActivity : AppCompatActivity() {

    var loadingLayout: LinearLayout? = null
    var homeLayout: LinearLayout? = null
    var userNameTitle: TextView? = null
    var profilePic: CircleImageView? = null
    var noActivityText: TextView? = null

    var user: FirebaseUser? = null
    var post: String? = null

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

        //Disable home layout till data load
        homeLayout?.visibility = View.GONE

        //Get current user from auth and from database
        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            var userId = user!!.uid

            val currentUserRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(userId)

            userNameTitle = findViewById(R.id.userNameTitle)

            //Set home activity according to the user details
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    userNameTitle!!.setText("Hello " + dataSnapshot.child("Name").getValue() + "!")

                    //If the user have a profile pic
                    if (dataSnapshot.child("imgUri").getValue()!!.equals("true")) {

                        val storage = FirebaseStorage.getInstance()

                        val gsReference =
                            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/" + userId + ".jpg")


                        GlideApp.with(applicationContext)
                            .load(gsReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(profilePic!!)


                    }

                    noActivityText!!.visibility = View.VISIBLE

                    //Disable loading animation and display the home layout
                    if (loadingLayout?.visibility == View.VISIBLE) {
                        homeLayout?.visibility = View.VISIBLE
                        loadingLayout?.visibility = View.GONE
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    // ...
                }
            }
            currentUserRootDatabase.addValueEventListener(postListener)


        }


        var popupMenu = findViewById<FabSpeedDial>(R.id.menuPopup)

        popupMenu.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {

                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.title) {

                    "Logout" -> {
                        showPopup()
                    }

                    "About" -> {
                        aboutOpen()
                    }

                }
                return false
            }


        })


    }


    fun createGroupSectionOpen(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    fun aboutOpen() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    fun setupMeetingSectionOpen(view: View) {
        val intent = Intent(this, SetupMeetingActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        minimizeApp()
    }

    fun minimizeApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun showPopup() {
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
        var user_id = user!!.getUid()
        val currentUserTokenIdDb =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user_id).child("tokenId")

        currentUserTokenIdDb.setValue("").addOnSuccessListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


}

