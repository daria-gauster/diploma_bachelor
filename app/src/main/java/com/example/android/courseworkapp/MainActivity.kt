package com.example.android.courseworkapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        /**if user is not authenticated, send them to SignInActivity to authenticate first
         *else send them to dashboard*/
        Handler().postDelayed({
            if (user != null) {
//                val dashboardIntent =
                Intent(
                    this, DashboardActivity::class.java
                ).also {
                    startActivity(it)
                    finish()
                }
            } else {
                Intent(
                    this, WelcomeScreen::class.java
                ).also {
                    startActivity(it)
                    finish()
                }
            }
        }, 2000)

    }
}