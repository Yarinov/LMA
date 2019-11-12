package com.yarinov.lma

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.widget.ListView
import java.util.HashSet





class ChooseFriendActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private var contactList: ListView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var contactModelArrayList: ArrayList<ContactModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friend)

        loadContacts()

        contactList = findViewById<ListView>(R.id.contactList)


        contactListAdapter = ContactListAdapter(this, contactModelArrayList!!)
        contactList!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        contactList!!.adapter = contactListAdapter




    }

    private fun loadContactsFunction(context: Context) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        )//plus any other properties you wish to query

        contactModelArrayList = ArrayList()

        var cursor: Cursor? = null
        try {
            cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
        } catch (e: SecurityException) {
            //SecurityException can be thrown if we don't have the right permissions
        }

        if (cursor != null) {
            try {
                val normalizedNumbersAlreadyFound = HashSet<String>()
                val indexOfNormalizedNumber =
                    cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                val indexOfDisplayName =
                    cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val indexOfDisplayNumber =
                    cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor!!.moveToNext()) {
                    val normalizedNumber = cursor!!.getString(indexOfNormalizedNumber)
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        val displayName = cursor!!.getString(indexOfDisplayName)
                        val displayNumber = cursor!!.getString(indexOfDisplayNumber)
                        //haven't seen this number yet: do something with this contact!

                        val contactModel = ContactModel()
                        contactModel.setNames(displayName)
                        contactModel.setNumbers(displayNumber)
                        contactModelArrayList!!.add(contactModel)
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
            } finally {
                cursor!!.close()
            }
        }
    }

    private fun loadContacts() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //callback onRequestPermissionsResult
        } else {

            loadContactsFunction(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }




}
