package com.example.android.courseworkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.android.courseworkapp.databinding.ActivityDashboardBinding
import com.example.android.courseworkapp.databinding.ActivitySettingsBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var tv_logout: TextView

    private lateinit var arrw_back: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_settings)

        //authorisation
        auth = FirebaseAuth.getInstance()

        //viewbinding instead of kotlin synthetics
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        arrw_back = findViewById(R.id.btn_arr_back_settings)
        tv_logout = findViewById(R.id.tv_logout)

        //go back to the previous screen
        arrw_back.setOnClickListener {
            Log.e("error", "so it works")
            finish()
        }
        tv_logout.setOnClickListener {
            auth.signOut()
            Intent(this, SignInActivity::class.java).also { startActivity(it) }
            finish()
        }

    }


}