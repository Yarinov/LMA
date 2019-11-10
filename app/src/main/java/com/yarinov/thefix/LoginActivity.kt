package com.yarinov.thefix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_login2.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class LoginActivity : AppCompatActivity() {

    var loginLogo:ImageView? = null

    var loginFlag:Boolean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        var loginButton = findViewById<Button>(R.id.loginButton)
        var signupButton = findViewById<Button>(R.id.signupButton)

        var emailInput = findViewById<EditText>(R.id.input_email)
        var passwordInput = findViewById<EditText>(R.id.input_password)

        loginLogo = findViewById<ImageView>(R.id.logoLogin)

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
}
