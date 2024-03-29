package com.yarinov.lma.User

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.yarinov.lma.Glide.GlideApp
import com.yarinov.lma.R
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

class FriendsListAdapter(private val context: Context, private val usersList: List<User>) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userName = usersList[position].userName

        holder.tvname!!.setText(userName)
        holder.tvname!!.isSelected = true

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
                    Timber.i("FriendsListAdapter", "Not found $userName")
                }
            }




    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var tvroundpic: CircleImageView? = null
        var tvname: TextView? = null

        init {

            tvroundpic = mView.findViewById(R.id.profileImageList) as CircleImageView
            tvname = mView.findViewById(R.id.name) as TextView

        }
    }

}