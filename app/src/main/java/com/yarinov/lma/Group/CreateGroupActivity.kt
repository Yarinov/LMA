package com.yarinov.lma.Group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import java.util.*
import kotlin.collections.ArrayList


class CreateGroupActivity : AppCompatActivity() {

    var usersInGroupTagRecyclerList: RecyclerView? = null
    private var usersNameInTagArrayList: ArrayList<MultiSelectUser>? = null
    private var usersTagsInGroupAdapter: UsersTagsInGroupAdapter? = null

    private var tagsCardView: CardView? = null

    private var groupName: EditText? = null
    private var groupDesc: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        usersInGroupTagRecyclerList = findViewById(R.id.usersInGroupTag)
        tagsCardView = findViewById(R.id.tagsCardView)

        groupName = findViewById(R.id.groupNameInput)
        groupDesc = findViewById(R.id.groupDescInput)

        val extras = intent.extras

        //Get user inputs - prevent delete between activity
        usersNameInTagArrayList = try {
            extras!!.getSerializable("selectedFriends") as ArrayList<MultiSelectUser>?

        } catch (e: KotlinNullPointerException) {
            ArrayList()
        }



        if (!usersNameInTagArrayList!!.isEmpty()) {
            tagsCardView!!.visibility = View.VISIBLE

            groupName!!.visibility = View.VISIBLE
            groupDesc!!.visibility = View.VISIBLE
        }

        usersTagsInGroupAdapter =
            UsersTagsInGroupAdapter(this, usersNameInTagArrayList!!)

        usersInGroupTagRecyclerList!!.setHasFixedSize(true)
        usersInGroupTagRecyclerList!!.layoutManager = GridLayoutManager(this, 3)
        usersInGroupTagRecyclerList!!.adapter = usersTagsInGroupAdapter


        usersInGroupTagRecyclerList!!.addOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                usersNameInTagArrayList!!.removeAt(position)
                usersTagsInGroupAdapter!!.notifyDataSetChanged()

                if (usersNameInTagArrayList!!.isEmpty()) {
                    tagsCardView!!.visibility = View.GONE

                    groupName!!.visibility = View.GONE
                    groupDesc!!.visibility = View.GONE

                }
            }
        })

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

    fun addFriendsToGroup(view: View) {
        val intent = Intent(this, SelectFriendsToGroup::class.java)
        intent.putExtra("selectedFriends", usersNameInTagArrayList)
        startActivity(intent)
    }

    fun createGroup(view: View) {

        var groupId = UUID.randomUUID()
        var groupName = groupName!!.text.toString()
        var groupDesc = groupDesc!!.text.toString()

        if (groupName.isNullOrBlank() || groupDesc.isNullOrBlank() || usersNameInTagArrayList!!.isEmpty()) {
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (usersNameInTagArrayList!!.size <= 1) {
            Toast.makeText(this, "You must choose at least 2 members", Toast.LENGTH_SHORT).show()
            return
        }

        var myId = FirebaseAuth.getInstance().currentUser!!.uid

        var groupMap = HashMap<String, String>()
        groupMap.put("groupName", groupName)
        groupMap.put("groupDesc", groupDesc)

        var membersMap = HashMap<String, Boolean>()
        membersMap.put(myId, true)

        for (member in usersNameInTagArrayList!!) {
            membersMap.put(member.userId, true)
        }

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId.toString())
            .setValue(groupMap)

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId.toString())
            .child("groupMembers").setValue(membersMap)

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

        Toast.makeText(this, "Group Created", Toast.LENGTH_SHORT).show()

        finish()
    }

    override fun onBackPressed() {

        if (!usersNameInTagArrayList.isNullOrEmpty() || !groupName!!.text.isNullOrBlank() || !groupDesc!!.text.isNullOrBlank()) {
            val alert = AlertDialog.Builder(this@CreateGroupActivity)
            alert.setMessage("Going back will delete any progress done, Are you sure?")
                .setPositiveButton("Yes") { dialog, which ->
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }.setNegativeButton("Cancel", null)

            val alert1 = alert.create()
            alert1.show()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
