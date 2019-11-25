package com.yarinov.lma.Group

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.CustomObject.ProgressGenerator.ProgressGenerator
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import de.hdodenhof.circleimageview.CircleImageView


class MultiSelectRecyclerAdapter(private val context: Context, private var usersList: List<MultiSelectUser>) :
    RecyclerView.Adapter<MultiSelectRecyclerAdapter.ViewHolder>(), Filterable,
    ProgressGenerator.OnCompleteListener {


    var usersListBackup = usersList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                usersList = results.values as ArrayList<MultiSelectUser>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                //Reset the contact list every time to get the result from the total contact
                usersList = usersListBackup

                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<MultiSelectUser>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in 0 until usersList.size) {
                    val dataNames = usersList.get(i).userName
                    if (dataNames!!.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(usersList.get(i))
                    }
                }

                results.count = FilteredArrayNames.size
                results.values = FilteredArrayNames

                return results
            }
        }
    }

    override fun onComplete() {
        Toast.makeText(context, "Request Sent!", Toast.LENGTH_LONG).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.multiselect_user_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userName = usersList[position].userName

        holder.tvname!!.isSelected = true
        holder.tvname!!.setText(userName)

        if (usersList[position].isSelected){
            holder.isSelectedButton!!.setImageResource(R.drawable.is_selected_true_ic)
        }else{
            holder.isSelectedButton!!.setImageResource(R.drawable.is_selected_false_ic)

        }


    }

    override fun getItemCount(): Int {
        return usersList.size
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
