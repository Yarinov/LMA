package com.yarinov.lma.Notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.lma.R


class NotificationAdapter(
    private val context: Context,
    private val notificationArrayList: List<customNotification>
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.meeting_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//Get and set name
        var nameString = notificationArrayList[position].toName
        holder.tvname!!.setText(nameString)

        //Get notification type and set icon accordingly
        var notificationType = notificationArrayList[position].type

        if (notificationType.equals("sent")) {
            holder.tvnotificationic!!.setImageResource(R.drawable.sent_ic)
        } else {
            holder.tvnotificationic!!.setImageResource(R.drawable.received_ic)
        }

        //Get and set date and place
        var whenLabel = notificationArrayList[position].date
        var whereLabel = notificationArrayList[position].place

        holder.tvwhere!!.isSelected = true

        holder.tvwhen!!.setText(whenLabel)
        holder.tvwhere!!.setText(whereLabel)

    }

    override fun getItemCount(): Int {
        return notificationArrayList.size
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvname: TextView? = null
        var tvwhen: TextView? = null
        var tvwhere: TextView? = null
        var tvnotificationic: ImageView? = null

        init {

            tvname = mView.findViewById(R.id.userNameText) as TextView
            tvwhen = mView.findViewById(R.id.dateNotificationData) as TextView
            tvwhere = mView.findViewById(R.id.locationNotificationData) as TextView
            tvnotificationic =
                mView.findViewById(R.id.notificationTypeIcon) as ImageView

        }
    }

}