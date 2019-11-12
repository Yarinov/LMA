package com.yarinov.lma

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import kotlinx.android.synthetic.main.activity_login2.*
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import android.util.Patterns
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    var loginLogo:ImageView? = null

    var loginFlag:Boolean?=null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        var loginButton = findViewById<Button>(R.id.loginButton)
        var signupButton = findViewById<Button>(R.id.signupButton)

        var emailInput = findViewById<EditText>(R.id.input_email)
        var passwordInput = findViewById<EditText>(R.id.input_password)

        loginLogo = findViewById<ImageView>(R.id.logoLogin)

        mAuth = FirebaseAuth.getInstance()

    }

    fun login (view : View){
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

    fun signup (view : View){
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

    fun loginToApp(view: View){
         val intent = Intent(this, HomeActivity::class.java)
         startActivity(intent)
    }


    override fun onBackPressed() {
        if(loginFlag!!){
            super.recreate()

        }else
            super.onBackPressed()
    }

    fun signupToFirebase(view: View){

        var userName = findViewById<TextInputEditText>(R.id.userNameInput)
        var userEmail = findViewById<TextInputEditText>(R.id.userEmailInput)
        var userPassword = findViewById<TextInputEditText>(R.id.userPasswordInput)
        var userRePassword = findViewById<TextInputEditText>(R.id.userRePasswordInput)

        if (userName.text.isNullOrBlank() || userEmail.text.isNullOrBlank() || userPassword.text.isNullOrBlank()
            || userRePassword.text.isNullOrBlank()){
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()

            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail.text).matches()){
            Toast.makeText(this, "Email Not Valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (!userPassword.text.toString().equals(userRePassword.text.toString())){
            Toast.makeText(this, "Incompatible passwords", Toast.LENGTH_SHORT).show()
            return
        }

        //Progress dialog for the signup process
        val progressDialog = ProgressDialog(
            this@LoginActivity,
            R.style.AppTheme)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()



        mAuth!!.createUserWithEmailAndPassword(userEmail.text.toString(),
            userPassword.text.toString())
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        //Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this@LoginActivity, "Good", Toast.LENGTH_SHORT).show()
                        val user = mAuth!!.getCurrentUser()

                        //Get a new user uid and create a new user in the database
                        var user_id = user!!.getUid()
                        val currentUserDb =
                            FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(user_id)

                        val newUserData = HashMap<String, String>()
                        newUserData.put("Name", userName.text.toString())
                        newUserData.put("Email", userEmail.text.toString())
                        newUserData.put("Role", "normal")

                        currentUserDb.setValue(newUserData)

                        progressDialog.dismiss()
                        //Go to home page after login the new user
                        openHome()
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                       // updateUI(null)
                    }

                    // ...
                }
            })
    }

    private fun openHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
