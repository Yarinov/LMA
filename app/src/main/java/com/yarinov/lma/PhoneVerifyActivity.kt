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
import com.google.firebase.auth.*
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

    private var mVerificationInProgress = false
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    // private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var verificationid: String? = null

    var phoneNumber: String? = null


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


    }

    override fun onStart() {
        super.onStart()
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

        if (!validatePhoneNumberAndCode()) return

        startPhoneNumberVerification(phoneNumber!!)


        firstVerifyLayout!!.visibility = View.GONE
        secondVerifyLayout!!.visibility = View.VISIBLE

        verifyCodeText?.setText("Please type the verification code sent to " + phoneNumber)

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
        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = task.getResult()?.getUser()

                        startActivity(Intent(applicationContext, HomeActivity::class.java))

                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }

                    }
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
