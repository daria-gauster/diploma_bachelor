package com.example.android.courseworkapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.courseworkapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.btnEmailSignUp.setOnClickListener {
            registerUser()
        }
        binding.btnToSignIn.setOnClickListener {
            Intent(this, SignInActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }


    //register a new user using email and password
    private fun registerUser() {
        val email = binding.includeContainerSignup.tilEmail.editText?.text.toString()
        val password = binding.includeContainerSignup.tilPassword.editText?.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    //used to be dispatchers.io but didnt work
                    //idk if it will cause problems later on
                    withContext(Dispatchers.Main) {
                        Log.d("SIGNUPACTIVITY", e.message)
                        Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    //check if user is logged in
    private fun checkLoggedInState() {
        Log.d("SIGNUPACTIVITY", if (auth.currentUser == null) "not signed in" else "signed in")
    }
}