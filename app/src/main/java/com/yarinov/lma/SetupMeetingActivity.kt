package com.yarinov.lma

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.*
import com.yarinov.lma.ChooseFriendActivity.Companion.PERMISSIONS_REQUEST_READ_CONTACTS
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.text.Editable
import android.text.TextWatcher
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class SetupMeetingActivity : AppCompatActivity() {

    private var contactList: ListView? = null
    private var contactListAdapter: ContactListAdapter? = null
    private var contactModelArrayList: ArrayList<ContactModel>? = null

    private var screenTitle: TextView? = null
    private var dateInput: EditText? = null
    private var contactSearchInput: EditText? = null

    private var firstStepLayout: LinearLayout? = null
    private var secondStepLayout: LinearLayout? = null
    private var thirdStepLayout: LinearLayout? = null


    private var thePerson: String? = null
    private var theDate: String? = null
    private var thePlace: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_meeting)

        //screenTitle = findViewById(R.id.screenTitle)

        dateInput = findViewById(R.id.dateInput)
        contactSearchInput = findViewById(R.id.contactSearchInput)

        firstStepLayout = findViewById(R.id.firstStepLayout)
        secondStepLayout = findViewById(R.id.secondStepLayout)
        thirdStepLayout = findViewById(R.id.thirdStepLayout)

        loadContacts()

        contactList = findViewById<ListView>(R.id.contactList)


        //Set the contact list adapter with all the data
        contactListAdapter = ContactListAdapter(this, contactModelArrayList!!)
        contactList!!.adapter = contactListAdapter

        //Filter contact list
        contactSearchInput!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                this@SetupMeetingActivity.contactListAdapter!!.getFilter().filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })


        contactList!!.setOnItemClickListener { parent, view, position, id ->

            Toast.makeText(this, "Clicked item :"+" "+position, Toast.LENGTH_SHORT).show()

            thePerson = contactModelArrayList!![position].getNames()

            firstStepLayout!!.visibility = View.GONE
            secondStepLayout!!.visibility = View.VISIBLE
        }

        //Setup the data
        var calendar = findViewById<CalendarView>(R.id.calendarInput)

        calendar.setOnDateChangeListener(OnDateChangeListener { arg0, year, month, date ->

            theDate = "$date/${month.plus(1)}/$year"

            dateInput!!.setText("$date/${month.plus(1)}/$year")
        })

        thePlace = "Somewhere over the rainbow"

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


    fun toThirdStep(view: View){
        secondStepLayout!!.visibility = View.GONE
        thirdStepLayout!!.visibility = View.VISIBLE
    }

    fun openMeetingSummry(view: View){
        val intent = Intent(this, MeetingSumActivity::class.java)
        intent.putExtra("thePerson", thePerson)
        intent.putExtra("theDate", theDate)
        intent.putExtra("thePlace", thePlace)
        startActivity(intent)
    }
}
