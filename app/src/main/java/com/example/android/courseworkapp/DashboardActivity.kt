package com.example.android.courseworkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_user_info.*
import kotlin.properties.Delegates

class DashboardActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        tvId.text = currentUser?.uid
        tvName.text = currentUser?.displayName
        tvEmail.text = currentUser?.email

        Glide.with(this).load(currentUser?.photoUrl).into(ivProfilePic)
        btnSignOut.setOnClickListener {
            mAuth.signOut()
            Intent(this, SignInActivity::class.java).also { startActivity(it) }
            finish()
        }

    }
}