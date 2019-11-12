package com.yarinov.lma

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.NavigationMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter


class HomeActivity : AppCompatActivity() {

    var titleLayout: LinearLayout? = null
    var homeLayout: FrameLayout? = null
    var userNameTitle: TextView? = null
    var divider: View? = null
    var userActivityList: ListView? = null

    var user: FirebaseUser? = null
    var post: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Get current user from auth and from database
        user = FirebaseAuth.getInstance().currentUser
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
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        }
        currentUserRootDatabase.addValueEventListener(postListener)

        titleLayout = findViewById(R.id.dim_layout)

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

                }
                return false
            }


        })


    }


    fun createGroupSectionOpen(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    fun setupMeetingSectionOpen(view: View) {
        val intent = Intent(this, SetupMeetingActivity::class.java)
        startActivity(intent)
    }

    fun tecSectionOpen(view: View) {
        val intent = Intent(this, TechActivity::class.java)
        startActivity(intent)

    }

    override fun onBackPressed() {
        if (user != null) {

            showPopup()
        }
        //super.onBackPressed()
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
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}
