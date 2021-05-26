package com.example.android.courseworkapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.courseworkapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth instance
        auth = FirebaseAuth.getInstance()


        //button bindings
        binding.btnSignUp.setOnClickListener {
            Intent(this, SignUpActivity::class.java).also { startActivity(it) }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn()
        }
        binding.btnEmailSignIn.setOnClickListener {
            emailSignIn()
        }
    }

    //google sign in
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SIGN_IN_ACTIVITY", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SIGN_IN_ACTIVITY", "Google sign in failed", e)
                }
            } else {
                Log.w("SIGN_IN_ACTIVITY", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SIGN_IN_ACTIVITY", "signInWithCredential:success")
                    Intent(this, DashboardActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SIGN_IN_ACTIVITY", "signInWithCredential:failure", task.exception)
                }
            }
    }

    //log into an existing account using email
    private fun emailSignIn() {
        val email = binding.includeContainerSignup.tilEmail.editText?.text.toString()
        val password = binding.includeContainerSignup.tilPassword.editText?.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        //if the user is verified redirects to dashboard
                        Intent(this@SignInActivity, DashboardActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    //used to be dispatchers.io but didnt work
                    //idk if it will cause problems later on
                    withContext(Dispatchers.Main) {
                        Log.d("SIGNUPACTIVITY", e.message)
                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
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