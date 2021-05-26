package com.example.android.courseworkapp

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.courseworkapp.databinding.ActivityDashboardBinding
import com.example.android.courseworkapp.model.HostedGame
import com.example.android.courseworkapp.model.Place
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime

private const val TAG = "DashboardActivity"
private const val PERMISSION_REQUEST = 10


class DashboardActivity : AppCompatActivity() {
    //check location permissions
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding

    private val preferences: SharedPreferences by lazy {
        getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        )
    }
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewbinding instead of kotlin synthetics
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize sharedpreferences
        initPrefs()

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        setSupportActionBar(findViewById(R.id.toolbarDashboard))
        //home navigation
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Events"
        }


        //viewbinding
//        binding.include.tvId.text = currentUser?.uid
//        binding.include.tvName.text = currentUser?.displayName
//        binding.include.tvEmail.text = currentUser?.email

        //load profile picture
//        Glide.with(this).load(currentUser?.photoUrl).into(binding.include.ivProfilePic)

        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            Intent(this, SignInActivity::class.java).also { startActivity(it) }
            finish()
        }

        //games data
        val hostedGames = generateSampleData()

        //set layout manager for rv
        binding.rvHostedGames.layoutManager = LinearLayoutManager(this)
        binding.rvHostedGames.adapter =
            GamesAdapter(this, hostedGames, object : GamesAdapter.OnClickListener {
                override fun onItemClick(position: Int) {
                    showGameinfoDialog()
                }

            })
        //show game information activity


        //check location permissions
        if (checkPermission(permissions)) {
            Toast.makeText(this, "Location permission previously granted", Toast.LENGTH_LONG).show()
        } else {
            requestPermissions(permissions, PERMISSION_REQUEST)
        }

        val mapsFragment = MapsFragment()

        supportFragmentManager.beginTransaction().add(
            R.id.fcGoogleMap,
            mapsFragment
        ).commit()


    }


    //check location permissions
    fun checkPermission(permissionArray: Array<String>): Boolean {
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }


    //permission check
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Location permission granted")
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required to locate you!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //initialize sharedpreferences
    private fun initPrefs() {
        editor = preferences.edit()
    }

    private fun showGameinfoDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_gameinfo)

        //set default
        dialog.findViewById<CheckBox>(R.id.cbGameParticipation).isChecked =
            preferences.contains("cbGameParticipation") && preferences.getBoolean(
                "cbGameParticipation",
                false
            )
        //get new prefs
        dialog.findViewById<Button>(R.id.btnApplyCheckBox).setOnClickListener {
            editor.apply {
                putBoolean(
                    "cbGameParticipation",
                    dialog.findViewById<CheckBox>(R.id.cbGameParticipation).isChecked
                )
                apply()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    //populate games rv
    private fun generateSampleData(): List<HostedGame> {
        return listOf(
            HostedGame(
                "Dung n drags",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173)
            ),
            HostedGame(
                "dnd 2",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163)
            ),
            HostedGame(
                "another dnd",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864)

            ),
            HostedGame(
                "3",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)

            ),
            HostedGame(
                "piss n d",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),


                )
        )
    }
}