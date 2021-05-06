package com.example.android.courseworkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
            if(user != null){
//                val dashboardIntent =
                Intent(
                    this, DashboardActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(
                    this, SignInActivity::class.java).also {
                    startActivity(it)
                }
            }
        },2000)


    }
}