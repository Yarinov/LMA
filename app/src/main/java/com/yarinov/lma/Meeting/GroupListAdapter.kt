package com.yarinov.lma.Meeting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.Group.Group
import com.yarinov.lma.R
import de.hdodenhof.circleimageview.CircleImageView

class GroupListAdapter(private val context: Context, private var groupsList: List<Group>) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>(), Filterable{


    var groupsListBackup = groupsList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                groupsList = results.values as ArrayList<Group>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                //Reset the contact list every time to get the result from the total contact
                groupsList = groupsListBackup

                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<Group>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in 0 until groupsList.size) {
                    val dataNames = groupsList.get(i).groupName
                    if (dataNames.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(groupsList.get(i))
                    }
                }

                results.count = FilteredArrayNames.size
                results.values = FilteredArrayNames

                return results
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.group_in_list_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val groupName = groupsList[position].groupName
        val groupDesc = groupsList[position].groupDesc

        holder.tvgroupname!!.isSelected = true
        holder.tvgroupname!!.setText(groupName)

        holder.tvgroupdesc!!.isSelected = true
        holder.tvgroupdesc!!.setText(groupDesc)





    }

    override fun getItemCount(): Int {
        return groupsList.size
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvgroupname: TextView? = null
        var tvgroupdesc: TextView? = null

        init {

            tvgroupname = mView.findViewById(R.id.groupNameInTag) as TextView
            tvgroupdesc = mView.findViewById(R.id.groupDescInTag) as TextView

        }
    }

}