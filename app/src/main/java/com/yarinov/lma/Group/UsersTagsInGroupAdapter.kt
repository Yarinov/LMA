package com.yarinov.lma.Group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.lma.R


class UsersTagsInGroupAdapter(
    private val context: Context,
    private val usersTagArrayList: List<MultiSelectUser>
) :
    RecyclerView.Adapter<UsersTagsInGroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.group_name_tag, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Get and set name
        var nameString = usersTagArrayList[position].userName
        holder.tvname!!.setText(nameString)
        holder.tvname!!.isSelected = true



    }

    override fun getItemCount(): Int {
        return usersTagArrayList.size
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvname: TextView? = null

        init {

            tvname = mView.findViewById(R.id.userNameInTag) as TextView
        }
    }


}