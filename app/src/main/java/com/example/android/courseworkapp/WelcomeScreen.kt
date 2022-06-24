package com.example.android.courseworkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.example.android.courseworkapp.databinding.ActivitySignUpBinding
import com.example.android.courseworkapp.databinding.ActivityWelcomeScreenBinding

class WelcomeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome_screen)
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnWelcomeLogin.setOnClickListener {

            Intent(this, SignInActivity::class.java).also { startActivity(it) }
            finish()
        }

    }



}