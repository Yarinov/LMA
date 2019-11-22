package com.yarinov.lma.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import android.net.Uri
import com.yarinov.lma.HomeActivity
import com.yarinov.lma.R
import de.hdodenhof.circleimageview.CircleImageView


class LoginActivity : AppCompatActivity() {

    var loginLogo: ImageView? = null

    var loginFlag: Boolean? = null

    private var mAuth: FirebaseAuth? = null

    var profilePic: CircleImageView? = null
    val PICK_IMAGE = 1
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loginFlag = false

        profilePic = findViewById(R.id.profile_image)

        mAuth = FirebaseAuth.getInstance()

        var loginButton = findViewById<Button>(R.id.loginButton)
        var signupButton = findViewById<Button>(R.id.signupButton)

        var emailInput = findViewById<EditText>(R.id.input_email)
        var passwordInput = findViewById<EditText>(R.id.input_password)

        loginLogo = findViewById<ImageView>(R.id.logoLogin)


    }

    fun chooseProfilePic(view: View) {
        var picIntent = Intent()
        picIntent.type = "image/*"
        picIntent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(picIntent, "Choose Photo"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            imageUri = data!!.data
            profilePic!!.setImageURI(imageUri)
        }
    }


    fun login(view: View) {

        val intent = Intent(this, PhoneVerifyActivity::class.java)
        intent.putExtra("VerifyType", "Login")
        startActivity(intent)

//        loginFlag = true
//        loginButton.visibility = View.GONE
//        signupButton.visibility = View.GONE
//        loginLogo!!.visibility = View.GONE
//
//        var loginPlat = findViewById<LinearLayout>(R.id.loginPlat)
//        loginPlat.visibility = View.VISIBLE
//        var loginAnimation = AnimationUtils.loadAnimation(this, R.anim.login_ani)
//        loginAnimation.duration = 700
//        loginPlat.animation = loginAnimation
//        loginPlat.animate()
//        loginAnimation.start()

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

        if (userName.text.isNullOrBlank() || userEmail.text.isNullOrBlank()
        ) {
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text).matches()) {
            Toast.makeText(this, "Email Not Valid", Toast.LENGTH_SHORT).show()
            return
        }


        val newUserData = HashMap<String, String>()
        newUserData.put("Name", userName.text.toString())
        newUserData.put("Email", userEmail.text.toString())
        newUserData.put("Role", "normal")

        val toVerifyPhoneIntent = Intent(this, PhoneVerifyActivity::class.java)
        toVerifyPhoneIntent.putExtra("userData", newUserData)
        toVerifyPhoneIntent.putExtra("VerifyType", "Registration")

        if (imageUri == null) {
            startActivity(toVerifyPhoneIntent)
        } else {
            toVerifyPhoneIntent.putExtra("imgSrc", imageUri)
            startActivity(toVerifyPhoneIntent)
        }

        finish()


    }

}
