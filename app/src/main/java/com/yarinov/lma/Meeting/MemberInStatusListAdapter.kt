package com.yarinov.lma.Meeting

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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

class MemberInStatusListAdapter(
    private val context: Context,
    private var usersList: List<User>,
    var meetingId: String
) :
    RecyclerView.Adapter<MemberInStatusListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.member_in_status_item, parent, false)

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
                    Log.i("FriendsListAdapter", "Not found " + userName)
                }
            }

        var currentMemberRootDatabase =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(usersList[position].getId()).child("Notifications").child(meetingId)
                .child("status")

        var getMemberStatusListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var memberStatus = p0.value.toString()

                println("AAAAAAAAAAAAAAAAAAAAAAAA  $memberStatus")

                if (memberStatus == "accept"){
                    holder.statusImg!!.setImageResource(R.drawable.meeting_yes_ic)
                } else if (memberStatus == "deny"){
                    holder.statusImg!!.setImageResource(R.drawable.meeting_no_ic)
                }


            }

        }

        currentMemberRootDatabase.addValueEventListener(getMemberStatusListener)

    }

    override fun getItemCount(): Int {
        return usersList.size
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null
        var statusImg: ImageView? = null

        var divider: View? = null

        init {

            tvname = mView.findViewById(R.id.name) as TextView
            tvroundpic = mView.findViewById(R.id.profileImageList) as CircleImageView
            statusImg = mView.findViewById(R.id.statusImg)

            divider = mView.findViewById(R.id.divider4)
        }
    }

}