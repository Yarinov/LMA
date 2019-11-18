package com.yarinov.lma.Notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.yarinov.lma.R


class NotificationAdapter(
    private val context: Context,
    private var notificationArrayList: ArrayList<customNotification>
) : BaseAdapter() {

    private val TAG = "NotificationAdapter"


    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return notificationArrayList.size
    }

    override fun getItem(position: Int): Any {
        return notificationArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.meeting_item, null, true)

            holder.tvname = convertView!!.findViewById(R.id.userNameText) as TextView
            holder.tvwhenwhere = convertView!!.findViewById(R.id.whenWhereText) as TextView
            holder.tvnotificationic =
                convertView.findViewById(R.id.notificationTypeIcon) as ImageView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }


        //Get and set name
        var nameString = notificationArrayList[position].toName
        holder.tvname!!.setText(nameString)

        //Get notification type and set icon accordingly
        var notificationType = notificationArrayList[position].type

        if (notificationType.equals("sent")){
            holder.tvnotificationic!!.setImageResource(R.drawable.sent_ic)
        }else{
            holder.tvnotificationic!!.setImageResource(R.drawable.received_id)
        }

        //Get and set date and place
        var whenWhere = notificationArrayList[position].date + " " + notificationArrayList[position].place
        holder.tvwhenwhere!!.setText(whenWhere)

        return convertView
    }

    private inner class ViewHolder {

        var tvnotificationic: ImageView? = null
        var tvname: TextView? = null
        var tvwhenwhere: TextView? = null


    }
}