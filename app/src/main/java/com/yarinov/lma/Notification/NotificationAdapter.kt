package com.yarinov.lma.Notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.lma.R


class NotificationAdapter(
    private val context: Context,
    private val notificationArrayList: List<CustomNotification>
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.meeting_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        //Get notification type and status for placing the icons accordingly
        var notificationType = notificationArrayList[position].type
        var notificationStatus = notificationArrayList[position].status
        var notificationMood = notificationArrayList[position].mood




        //If current user sent meeting request
        if (notificationType.equals("sent")) {
            holder.tvnotificationic!!.setImageResource(R.drawable.sent_ic)

            //Set the meeting status icon
            if (notificationMood == "Single"){
                if (notificationStatus.equals("pending")) {
                    holder.pendingMeetingButton!!.visibility = View.VISIBLE

                    holder.denyMeetingButton!!.visibility = View.GONE
                    holder.acceptMeetingButton!!.visibility = View.GONE

                } else if (notificationStatus.equals("deny")) {
                    holder.pendingMeetingButton!!.visibility = View.GONE
                    holder.acceptMeetingButton!!.visibility = View.GONE

                    holder.denyMeetingButton!!.visibility = View.VISIBLE
                } else {
                    holder.pendingMeetingButton!!.visibility = View.GONE
                    holder.denyMeetingButton!!.visibility = View.GONE

                    holder.acceptMeetingButton!!.visibility = View.VISIBLE
                }
            }else{//This is a group meeting
                holder.groupMeetingMoreButton!!.visibility = View.VISIBLE
            }

            //If current user received meeting request
        } else {
            holder.tvnotificationic!!.setImageResource(R.drawable.received_ic)

            if (notificationMood == "Single") {
                //Set the meeting status icon
                if (notificationStatus.equals("pending")) {
                    holder.acceptMeetingButton!!.visibility = View.VISIBLE
                    holder.denyMeetingButton!!.visibility = View.VISIBLE
                } else if (notificationStatus.equals("deny")) {
                    holder.denyMeetingButton!!.visibility = View.VISIBLE
                    holder.acceptMeetingButton!!.visibility = View.GONE
                } else {
                    holder.acceptMeetingButton!!.visibility = View.VISIBLE
                    holder.denyMeetingButton!!.visibility = View.GONE
                }
            }else{//This is a group meeting
                holder.groupMeetingMoreButton!!.visibility = View.VISIBLE
            }
        }


        //Get and set date and place
        var dateLabel = notificationArrayList[position].date
        var timeLabel = notificationArrayList[position].time
        var whereLabel = notificationArrayList[position].place

        holder.tvwhere!!.isSelected = true
        holder.tvname!!.isSelected = true
        holder.fromUserText!!.isSelected = true

        //Get and set name
        var nameString = notificationArrayList[position].toName
        holder.tvname!!.setText(nameString)

        if (notificationMood == "Group") {
            holder.fromText!!.visibility = View.VISIBLE
            holder.fromUserText!!.visibility = View.VISIBLE

            if (notificationType.equals("sent")){
                holder.fromUserText!!.text = notificationArrayList[position].fromName
            }else{
                holder.fromUserText!!.text = notificationArrayList[position].toId

            }

        }

        holder.tvdate!!.text = dateLabel
        holder.tvwhere!!.text = whereLabel
        holder.tvtime!!.text = timeLabel


        //TODO Meeting accept/reject for groups
        //User accept meeting
        holder.acceptMeetingButton!!.setOnClickListener {

            if (notificationType.equals("received") && notificationStatus.equals("pending")) {
                holder.denyMeetingButton!!.visibility = View.GONE

                //Change the meeting status

                var currentUserId = notificationArrayList[position].toId
                var requestedUserId = notificationArrayList[position].fromId


                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(currentUserId).child("Notifications")
                    .child(notificationArrayList[position].meetingId).child("status")
                    .setValue("accept")

                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(requestedUserId).child("Notifications")
                    .child(notificationArrayList[position].meetingId).child("status")
                    .setValue("accept")

            }

        }

        //User deny meeting
        holder.denyMeetingButton!!.setOnClickListener {

            if (notificationType.equals("received") && notificationStatus.equals("pending")) {
                holder.acceptMeetingButton!!.visibility = View.GONE

                //Change the meeting status

                var currentUserId = notificationArrayList[position].toId
                var requestedUserId = notificationArrayList[position].fromId


                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(currentUserId).child("Notifications")
                    .child(notificationArrayList[position].meetingId).child("status")
                    .setValue("deny")

                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(requestedUserId).child("Notifications")
                    .child(notificationArrayList[position].meetingId).child("status")
                    .setValue("deny")

            }

        }

    }

    override fun getItemCount(): Int {
        return notificationArrayList.size
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvname: TextView? = null
        var tvdate: TextView? = null
        var tvtime: TextView? = null
        var tvwhere: TextView? = null

        var fromText: TextView? = null
        var fromUserText: TextView? = null

        var tvnotificationic: ImageView? = null

        var acceptMeetingButton: Button? = null
        var denyMeetingButton: Button? = null
        var pendingMeetingButton: Button? = null
        var groupMeetingMoreButton: Button? = null

        init {

            tvname = mView.findViewById(R.id.userNameText) as TextView
            tvdate = mView.findViewById(R.id.dateNotificationData) as TextView
            tvtime = mView.findViewById(R.id.timeNotificationData) as TextView
            tvwhere = mView.findViewById(R.id.locationNotificationData) as TextView

            fromText = mView.findViewById(R.id.fromGroupNotification)
            fromUserText = mView.findViewById(R.id.fromUserGroupNotification)

            tvnotificationic =
                mView.findViewById(R.id.notificationTypeIcon) as ImageView

            acceptMeetingButton = mView.findViewById(R.id.acceptMeetingButton)
            denyMeetingButton = mView.findViewById(R.id.denyMeetingButton)
            pendingMeetingButton = mView.findViewById(R.id.pendingMeetingButton)
            groupMeetingMoreButton = mView.findViewById(R.id.groupMeetingMore)
        }
    }


}