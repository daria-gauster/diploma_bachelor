package com.example.android.courseworkapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.android.courseworkapp.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewbinding instead of kotlin synthetics
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.include.tvId.text = currentUser?.uid
        binding.include.tvName.text = currentUser?.displayName
        binding.include.tvEmail.text = currentUser?.email

        //load profile picture
        Glide.with(this).load(currentUser?.photoUrl).into(binding.include.ivProfilePic)
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            Intent(this, SignInActivity::class.java).also { startActivity(it) }
            finish()
        }

    }
}