package com.yarinov.lma.User

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dd.processbutton.iml.ActionProcessButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.CustomObject.ProgressGenerator.ProgressGenerator
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber


class AddFriendListAdapter(private val context: Context, private var usersList: List<User>) :
    RecyclerView.Adapter<AddFriendListAdapter.ViewHolder>(), Filterable,
    ProgressGenerator.OnCompleteListener {


    var usersListBackup = usersList

    var filteredArrayNames = ArrayList<User>()

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                usersList = results.values as ArrayList<User>
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
            LayoutInflater.from(parent.context).inflate(R.layout.add_friend_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userName = usersList[position].userName

        holder.tvname!!.isSelected = true
        holder.tvname!!.setText(userName)

        //Get user pic (if exits)
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/" + usersList[position].getId() + ".jpg")

        gsReference.downloadUrl
            .addOnSuccessListener {


                GlideApp.with(context).asBitmap().load(gsReference).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate().into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            holder.tvroundpic!!.setImageBitmap(resource)
                        }

                    })
            }
            .addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    //Not Found
                    Timber.i("FriendsListAdapter", "Not found " + userName)
                }
            }

        var user = FirebaseAuth.getInstance().currentUser

        holder.tvsendreqbutton!!.setOnClickListener {

            holder.tvsendreqbutton!!.progress = 100

            holder.tvsendreqbutton!!.isEnabled = false

            //Write to database
            val currentFriendsRootDatabase =
                FirebaseDatabase.getInstance().getReference().child("Friends")

            //Write in the friend section
            currentFriendsRootDatabase.child(usersList[position].getId()).child(user!!.uid)
                .setValue(false)

            notifyDataSetChanged()

        }


    }

    override fun getItemCount(): Int {
        return usersList.size
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null
        var tvsendreqbutton: ActionProcessButton? = null

        init {

            tvname = mView!!.findViewById(R.id.userName) as TextView
            tvroundpic = mView.findViewById(R.id.profileImageList) as CircleImageView
            tvsendreqbutton =
                mView.findViewById(R.id.sendRequestButton) as ActionProcessButton

        }
    }

}