package com.yarinov.lma

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView


class ContactListAdapter(
    private val context: Context,
    private var contactModelArrayList: ArrayList<ContactModel>
) : BaseAdapter(),
    Filterable {

    var contactListBackup = contactModelArrayList

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                contactModelArrayList = results.values as ArrayList<ContactModel>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {

                //Reset the contact list every time to get the result from the total contact
                contactModelArrayList = contactListBackup

                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<ContactModel>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in 0 until contactModelArrayList.size) {
                    val dataNames = contactModelArrayList.get(i).getNames()
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
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
            holder.tvroundname = convertView.findViewById(R.id.shortName) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }


        var nameString = contactModelArrayList[position].getNames()

        var parts = nameString.split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        holder.tvname!!.setText(nameString)

        if (!lastName.equals(""))
            holder.tvroundname!!.setText(firstName?.get(0).toString() + lastName[0].toString())
        else
            holder.tvroundname!!.setText(firstName?.get(0).toString())

        return convertView
    }

    private inner class ViewHolder {

        var tvroundname: TextView? = null
        var tvname: TextView? = null
        var tvnumber: TextView? = null

    }
}