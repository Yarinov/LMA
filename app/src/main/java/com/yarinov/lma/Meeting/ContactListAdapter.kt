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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import de.hdodenhof.circleimageview.CircleImageView




class ContactListAdapter(
    private val context: Context,
    private var contactModelArrayList: ArrayList<User>
) : BaseAdapter(),
    Filterable {

    private val TAG = "ContactListAdapter"

    var contactListBackup = contactModelArrayList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                contactModelArrayList = results.values as ArrayList<User>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                //Reset the contact list every time to get the result from the total contact
                contactModelArrayList = contactListBackup

                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<User>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in 0 until contactModelArrayList.size) {
                    val dataNames = contactModelArrayList.get(i).getNames()
                    if (dataNames.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(contactModelArrayList.get(i))
                    }
                }

                results.count = FilteredArrayNames.size
                results.values = FilteredArrayNames

                return results
            }
        }
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return contactModelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return contactModelArrayList[position]
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
            convertView = inflater.inflate(R.layout.lv_item, null, true)

            holder.tvname = convertView!!.findViewById(R.id.name) as TextView
            holder.tvroundpic = convertView.findViewById(R.id.profileImageList) as CircleImageView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }


        var nameString = contactModelArrayList[position].getNames()

        holder.tvname!!.setText(nameString)

        //Get user pic (if exits)
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/" + contactModelArrayList[position].getId() + ".jpg")

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
                    Log.i(TAG, "Not found " + nameString)
                }
            }

        return convertView
    }

    private inner class ViewHolder {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null

    }
}