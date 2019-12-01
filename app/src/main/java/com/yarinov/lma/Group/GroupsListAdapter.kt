package com.yarinov.lma.Group

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.lma.R
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class GroupsListAdapter(
    private val context: Context,
    private var groupsList: List<Group>
) :
    RecyclerView.Adapter<GroupsListAdapter.ViewHolder>(), Filterable{

    var filteredGroups = ArrayList<Group>()
    var groupsListBackup = groupsList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                groupsList = results.values as ArrayList<Group>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                groupsList = groupsListBackup

                var constraint = constraint
                constraint = constraint.toString().toLowerCase()


                //Reset the contact list every time to get the result from the total contact
                if (groupsList.isEmpty()) {
                    Toast.makeText(context, "404 Group", Toast.LENGTH_SHORT).show()

                } else {
                    filteredGroups.clear()

                    if (constraint.toString().isEmpty()) {
                        filteredGroups.addAll(groupsList)
                    } else {
                        filteredGroups = ArrayList()


                        for (i in groupsList.indices) {
                            val dataNames = groupsList[i].groupName
                            if (dataNames!!.toLowerCase().contains(constraint.toString())) {
                                filteredGroups.add(groupsList[i])
                            }
                        }
                    }
                }


                val results = FilterResults()

                results.count = filteredGroups.size
                results.values = filteredGroups

                return results
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.group_card_item, parent, false)


        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var groupId = groupsList[position].groupId
        var groupNameText = groupsList[position].groupName

        holder.groupName!!.text = groupNameText
        holder.groupName!!.isSelected = true

        holder.groupDescLabel!!.text = groupsList[position].groupDesc

        holder.groupDetailsLabel!!.setOnClickListener {
            var intent = Intent(context, SingleGroupActivity::class.java)
            intent.putExtra("groupName", groupNameText)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupDesc", groupsList[position].groupDesc)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return groupsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int) : Group {
        return groupsList[position]
    }

    fun sortByAsc(){
        val comparator: Comparator<Group> =
            Comparator { object1: Group, object2: Group ->
                object1.groupName.compareTo(object2.groupName, true)
            }
        Collections.sort(groupsList, comparator)
        notifyDataSetChanged()
    }

    fun sortByDesc(){
        val comparator: Comparator<Group> =
            Comparator { object1: Group, object2: Group ->
                object2.groupName.compareTo(object1.groupName, true)
            }
        Collections.sort(groupsList, comparator)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var groupName: TextView? = null
        var groupDescLabel: TextView? = null
        var groupDetailsLabel: TextView? = null

        init {

            groupName = mView.findViewById(R.id.groupNameLabel) as TextView
            groupDescLabel = mView.findViewById(R.id.groupDescLabel) as TextView
            groupDetailsLabel = mView.findViewById(R.id.groupDetailsLabel) as TextView


        }
    }

}
