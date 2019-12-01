package com.yarinov.lma.Notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R
import java.util.*
import kotlin.Comparator


class NotificationInGroupAdapter(
    private val context: Context,
    private var notificationsList: List<Notification>
) :
    RecyclerView.Adapter<NotificationInGroupAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.meeting_in_group_item, parent, false)


        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Get the meeting posting user name
        var fromId = notificationsList[position].fromId

        var formedMeetingUserDatabase =   FirebaseDatabase.getInstance().getReference().child("Users")
            .child(fromId)

        var getNameListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                holder.tvname!!.text = p0.child("Name").value.toString()
            }

        }

        formedMeetingUserDatabase.addValueEventListener(getNameListener)

        holder.tvname!!.isSelected = true



        holder.tvdate!!.text = notificationsList[position].date
        holder.tvtime!!.text = notificationsList[position].time
        holder.tvwhere!!.text = notificationsList[position].place


    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun sortByAsc(){
        val comparator: Comparator<Notification> =
            Comparator { object1: Notification, object2: Notification ->
                object1.datePosted.compareTo(object2.datePosted, true)
            }
        Collections.sort(notificationsList, comparator)
        notifyDataSetChanged()
    }

    fun sortByDesc(){
        val comparator: Comparator<Notification> =
            Comparator { object1: Notification, object2: Notification ->
                object2.datePosted.compareTo(object1.datePosted, true)
            }
        Collections.sort(notificationsList, comparator)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvname: TextView? = null
        var tvdate: TextView? = null
        var tvtime: TextView? = null
        var tvwhere: TextView? = null

        var meetingStatusButton: TextView? = null

        init {

            tvname = mView.findViewById(R.id.userNameText) as TextView
            tvdate = mView.findViewById(R.id.dateNotificationData) as TextView
            tvtime = mView.findViewById(R.id.timeNotificationData) as TextView
            tvwhere = mView.findViewById(R.id.locationNotificationData) as TextView

            meetingStatusButton = mView.findViewById(R.id.meetingStatusButton) as TextView
        }
    }

}
