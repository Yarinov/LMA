package com.yarinov.lma.Meeting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import de.hdodenhof.circleimageview.CircleImageView

class ContactListAdapter(private val context: Context, private var usersList: List<User>) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>(), Filterable{


    var usersListBackup = usersList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                usersList = results.values as ArrayList<User>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                //Reset the contact list every time to get the result from the total contact
                usersList = usersListBackup

                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<User>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in 0 until usersList.size) {
                    val dataNames = usersList.get(i).getNames()
                    if (dataNames.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(usersList.get(i))
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
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userName = usersList[position].userName

        holder.tvname!!.isSelected = true
        holder.tvname!!.text = userName

        //Get user pic (if exits)
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/" + usersList[position].getId() + ".jpg")

        gsReference.downloadUrl
            .addOnSuccessListener {


                GlideApp.with(context)
                    .load(gsReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.tvroundpic!!)
            }
            .addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    //Not Found
                    Log.i("FriendsListAdapter", "Not found " + userName)
                }
            }



    }

    override fun getItemCount(): Int {
        return usersList.size
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null

        var divider:View? = null

        init {

            tvname = mView.findViewById(R.id.name) as TextView
            tvroundpic = mView.findViewById(R.id.profileImageList) as CircleImageView

            divider = mView.findViewById(R.id.divider4)
        }
    }

}