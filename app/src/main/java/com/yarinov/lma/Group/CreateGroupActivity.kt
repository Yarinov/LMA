package com.yarinov.lma.Group

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.lma.Meeting.SetupMeetingActivity
import com.yarinov.lma.R


class CreateGroupActivity : AppCompatActivity() {

    var usersInGroupTagRecyclerList: RecyclerView? = null
    private var usersNameInTagArrayList: ArrayList<MultiSelectUser>? = null
    private var usersTagsInGroupAdapter: UsersTagsInGroupAdapter? = null

    private var tagsCardView: CardView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        usersInGroupTagRecyclerList = findViewById(R.id.usersInGroupTag)
        tagsCardView = findViewById(R.id.tagsCardView)

        usersNameInTagArrayList = try {
            val extras = intent.extras
            extras!!.getSerializable("selectedFriends") as ArrayList<MultiSelectUser>?

        }catch(e: KotlinNullPointerException){
            ArrayList()
        }

        if (!usersNameInTagArrayList!!.isEmpty()){
            tagsCardView!!.visibility = View.VISIBLE
        }

        usersTagsInGroupAdapter =
            UsersTagsInGroupAdapter(this, usersNameInTagArrayList!!)

        usersInGroupTagRecyclerList!!.setHasFixedSize(true)
        usersInGroupTagRecyclerList!!.layoutManager = GridLayoutManager(this, 3)
        usersInGroupTagRecyclerList!!.adapter = usersTagsInGroupAdapter


        usersInGroupTagRecyclerList!!.addOnItemClickListener(object:
            CreateGroupActivity.OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                usersNameInTagArrayList!!.removeAt(position)
                usersTagsInGroupAdapter!!.notifyDataSetChanged()

                if (usersNameInTagArrayList!!.isEmpty()){
                    tagsCardView!!.visibility = View.GONE

                }
            }
        })

    }

    //RecyclerView interface for item click
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {

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

    fun addFriendsToGroup(view: View){
        val intent = Intent(this, SelectFriendsToGroup::class.java)
        startActivity(intent)
    }
}
