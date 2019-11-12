package com.yarinov.lma

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.chaos.view.PinView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit


class PhoneVerifyActivity : AppCompatActivity() {

    var firstVerifyLayout: LinearLayout? = null
    var secondVerifyLayout: LinearLayout? = null

    var verifyCodeText: TextView? = null

    private var mAuth: FirebaseAuth? = null

    var phoneNumberInput: EditText? = null
    var countryCodePicker: CountryCodePicker? = null
    var verifyCodeInput: PinView? = null

    private var verificationid: String? = null

    var phoneNumber: String? = null

    var userData: HashMap<String, String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)

        firstVerifyLayout = findViewById(R.id.firstVerifyLayout)
        secondVerifyLayout = findViewById(R.id.secondVerifyLayout)

        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        countryCodePicker = findViewById(R.id.ccp)
        verifyCodeInput = findViewById(R.id.firstPinView)
        verifyCodeText = findViewById(R.id.verifyCodeText)

        mAuth = FirebaseAuth.getInstance()

        countryCodePicker?.setCountryPreference("IL")

        userData = intent.extras!!.get("userData") as HashMap<String, String>?
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        var mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(this@PhoneVerifyActivity, e.message, Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)

                verificationid = s
            }
        }

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks)
    }

    fun toSecondVerifyLayout(view: View) {

        phoneNumber =
            "+" + countryCodePicker!!.selectedCountryCode + phoneNumberInput!!.text.toString()

        var userRef = FirebaseDatabase.getInstance().getReference("Users")
        userRef.orderByChild("Phone Number").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.getValue() != null) {
                        Toast.makeText(
                            this@PhoneVerifyActivity,
                            "There is already a member register with this phone number",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (!validatePhoneNumberAndCode()) return

                        startPhoneNumberVerification(phoneNumber!!)


                        firstVerifyLayout!!.visibility = View.GONE
                        secondVerifyLayout!!.visibility = View.VISIBLE

                        verifyCodeText?.setText("Please type the verification code sent to " + phoneNumber)
                    }
                }

            })

    }

    fun verifyCode(view: View) {
        if (!validateSMSCode()) return

        verifyPhoneNumberWithCode(verificationid.toString(), verifyCodeInput?.text.toString())
    }

    override fun onBackPressed() {
        if (secondVerifyLayout!!.isVisible) {
            secondVerifyLayout!!.visibility = View.GONE
            firstVerifyLayout!!.visibility = View.VISIBLE
        } else
            super.onBackPressed()
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId, code)


        if (credential.smsCode.equals(verifyCodeInput?.text.toString())) {
            signInWithPhoneAuthCredential(credential)
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {


        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        //Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this@PhoneVerifyActivity, "Good", Toast.LENGTH_SHORT).show()
                        val user = mAuth!!.getCurrentUser()

                        //Get a new user uid and create a new user in the database
                        var user_id = user!!.getUid()
                        val currentUserDb =
                            FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(user_id)

                        userData!!.put("Phone Number", phoneNumber!!)

                        currentUserDb.setValue(userData)

                        //Go to home page after login the new user
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        finish()
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@PhoneVerifyActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // updateUI(null)
                    }

                    // ...
                }
            })
    }

    private fun validatePhoneNumberAndCode(): Boolean {

        if (phoneNumberInput!!.text.toString().equals("")) {
            phoneNumberInput?.setError("Invalid phone number.")
            return false
        }


        return true
    }

    private fun validateSMSCode(): Boolean {
        val code = verifyCodeInput?.text.toString()
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


}
