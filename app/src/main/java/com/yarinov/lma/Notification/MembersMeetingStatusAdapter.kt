package com.yarinov.lma.Notification

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import com.yarinov.lma.User.User
import de.hdodenhof.circleimageview.CircleImageView


class MembersMeetingStatusAdapter(
    private val context: Context,
    private val groupId: String,
    private val meetingId: String
) :
    RecyclerView.Adapter<MembersMeetingStatusAdapter.ViewHolder>() {

    private var usersArrayList: List<User> = ArrayList()
    private var membersArrayList: HashMap<String,String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.in_status_card_item, parent, false)

        var meetingRootDatabase =  FirebaseDatabase.getInstance().getReference().child("Groups")
            .child(groupId).child("Meetings")
            .child(meetingId)

        //Get all the group members data
        var getGroupMembersListener =  object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                membersArrayList = HashMap()

                for (childDataSnapshot in dataSnapshot.children){

                    var memberId = childDataSnapshot.key.toString()
                    var memberMeetingStatus = childDataSnapshot.value.toString()

                    membersArrayList!!.put(memberId, memberMeetingStatus)



                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        }


        meetingRootDatabase.addValueEventListener(getGroupMembersListener)


        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        //Set member profile pic (if exits)
//        val storage = FirebaseStorage.getInstance()
//
//        val gsReference =
//            storage.getReferenceFromUrl("gs://lma-master.appspot.com/Images/$memberId.jpg")
//
//        gsReference.downloadUrl
//            .addOnSuccessListener {
//
//
//                GlideApp.with(context)
//                    .load(gsReference)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .into(holder.tvroundpic!!)
//            }
//            .addOnFailureListener { exception ->
//                val errorCode = (exception as StorageException).errorCode
//                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
//                    //Not Found
//                    Timber.i("FriendsListAdapter", "Not found $memberId")
//                }
//            }
//
//        val userName = usersArrayList[position].userName
//
//        holder.tvname!!.isSelected = true
//        holder.tvname!!.setText(userName)

    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null
        var tvstatusimg: ImageView? = null

        init {

            tvname = mView.findViewById(R.id.name) as TextView
            tvroundpic = mView.findViewById(R.id.profileImageList) as CircleImageView

            tvstatusimg =
                mView.findViewById(R.id.meetingStatusImg) as ImageView

        }



    }


}