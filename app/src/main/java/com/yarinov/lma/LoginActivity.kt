package com.yarinov.lma

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login2.*
import com.google.firebase.auth.FirebaseUser
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class LoginActivity : AppCompatActivity() {

    var loginLogo: ImageView? = null

    var loginFlag: Boolean? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val i = Intent(this@LoginActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        loginFlag = false

        var loginButton = findViewById<Button>(R.id.loginButton)
        var signupButton = findViewById<Button>(R.id.signupButton)

        var emailInput = findViewById<EditText>(R.id.input_email)
        var passwordInput = findViewById<EditText>(R.id.input_password)

        loginLogo = findViewById<ImageView>(R.id.logoLogin)

        mAuth = FirebaseAuth.getInstance()

    }


    fun login(view: View) {


        loginFlag = true
        loginButton.visibility = View.GONE
        signupButton.visibility = View.GONE
        loginLogo!!.visibility = View.GONE

        var loginPlat = findViewById<LinearLayout>(R.id.loginPlat)
        loginPlat.visibility = View.VISIBLE
        var loginAnimation = AnimationUtils.loadAnimation(this, R.anim.login_ani)
        loginAnimation.duration = 700
        loginPlat.animation = loginAnimation
        loginPlat.animate()
        loginAnimation.start()

    }

    fun signup(view: View) {
        loginFlag = true
        loginButton.visibility = View.GONE
        signupButton.visibility = View.GONE
        loginLogo!!.visibility = View.GONE

        var signupPlat = findViewById<LinearLayout>(R.id.signupPlat)
        signupPlat.visibility = View.VISIBLE
        var loginAnimation = AnimationUtils.loadAnimation(this, R.anim.login_ani)
        loginAnimation.duration = 700
        signupPlat.animation = loginAnimation
        signupPlat.animate()
        loginAnimation.start()

    }

    fun loginToApp(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        if (loginFlag!!) {
            super.recreate()

        } else
            finish()
    }

    fun signupToFirebase(view: View) {

        var userName = findViewById<TextInputEditText>(R.id.userNameInput)
        var userEmail = findViewById<TextInputEditText>(R.id.userEmailInput)
        var userPassword = findViewById<TextInputEditText>(R.id.userPasswordInput)
        var userRePassword = findViewById<TextInputEditText>(R.id.userRePasswordInput)

        if (userName.text.isNullOrBlank() || userEmail.text.isNullOrBlank() || userPassword.text.isNullOrBlank()
            || userRePassword.text.isNullOrBlank()
        ) {
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text).matches()) {
            Toast.makeText(this, "Email Not Valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (!userPassword.text.toString().equals(userRePassword.text.toString())) {
            Toast.makeText(this, "Incompatible passwords", Toast.LENGTH_SHORT).show()
            return
        }

        val newUserData = HashMap<String, String>()
        newUserData.put("Name", userName.text.toString())
        newUserData.put("Email", userEmail.text.toString())
        newUserData.put("Password", userPassword.text.toString())
        newUserData.put("Role", "normal")

        val toVerifyPhoneIntent = Intent(this, PhoneVerifyActivity::class.java)
        toVerifyPhoneIntent.putExtra("userData", newUserData)

        startActivity(toVerifyPhoneIntent)

        finish()


    }
}
