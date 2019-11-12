package com.yarinov.lma

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.NavigationMenu
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter

class HomeActivity : AppCompatActivity() {

    var titleLayout: LinearLayout? = null
    var homeLayout: FrameLayout? = null
    var yourActivityText: TextView? = null
    var divider: View? = null
    var userActivityList: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        titleLayout = findViewById(R.id.dim_layout)

        var popupMenu = findViewById<FabSpeedDial>(R.id.menuPopup)

        popupMenu.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {
                titleLayout!!.visibility = View.VISIBLE
                return true
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
        super.onBackPressed()
    }
}
