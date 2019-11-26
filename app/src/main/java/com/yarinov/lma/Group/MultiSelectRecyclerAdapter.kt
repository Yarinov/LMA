package com.yarinov.lma.Group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.lma.CustomObject.ProgressGenerator.ProgressGenerator
import com.yarinov.lma.R
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class MultiSelectRecyclerAdapter(
    private val context: Context,
    private var usersList: List<MultiSelectUser>
) :
    RecyclerView.Adapter<MultiSelectRecyclerAdapter.ViewHolder>(), Filterable,
    ProgressGenerator.OnCompleteListener {

    var filteredArrayNames = ArrayList<MultiSelectUser>()
    var usersListBackup = usersList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                usersList = results.values as ArrayList<MultiSelectUser>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                usersList = usersListBackup

                var constraint = constraint
                constraint = constraint.toString().toLowerCase()


                //Reset the contact list every time to get the result from the total contact
                if (usersList.isEmpty()) {
                    Toast.makeText(context, "404 User", Toast.LENGTH_SHORT).show()

                } else {
                    filteredArrayNames.clear()

                    if (constraint.toString().isEmpty()) {
                        filteredArrayNames.addAll(usersList)
                    } else {
                        filteredArrayNames = ArrayList()


                        for (i in usersList.indices) {
                            val dataNames = usersList[i].userName
                            if (dataNames!!.toLowerCase().contains(constraint.toString())) {
                                filteredArrayNames.add(usersList[i])
                            }
                        }
                    }
                }


                val results = FilterResults()

                results.count = filteredArrayNames.size
                results.values = filteredArrayNames

                return results
            }
        }
    }

    override fun onComplete() {
        Toast.makeText(context, "Request Sent!", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.multiselect_user_item, parent, false)


        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userName = usersList[position].userName

        holder.tvname!!.isSelected = true
        holder.tvname!!.setText(userName)


        if (usersList[position].isSelected) {
            holder.isSelectedButton!!.setImageResource(R.drawable.is_selected_true_ic)
        } else {
            holder.isSelectedButton!!.setImageResource(R.drawable.is_selected_false_ic)

        }


    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int) : MultiSelectUser{
        return usersList[position]
    }

    fun sortByAsc(){
        val comparator: Comparator<MultiSelectUser> =
            Comparator { object1: MultiSelectUser, object2: MultiSelectUser ->
                object1.userName.compareTo(object2.userName, true)
            }
        Collections.sort(usersList, comparator)
        notifyDataSetChanged()
    }

    fun sortByDesc(){
        val comparator: Comparator<MultiSelectUser> =
            Comparator { object1: MultiSelectUser, object2: MultiSelectUser ->
                object2.userName.compareTo(object1.userName, true)
            }
        Collections.sort(usersList, comparator)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvname: TextView? = null
        var isSelectedButton: ImageView? = null

        init {

            tvname = mView.findViewById(R.id.userName) as TextView
            isSelectedButton =
                mView.findViewById(R.id.radioButtonAddUserToGroup) as ImageView

        }
    }

}
