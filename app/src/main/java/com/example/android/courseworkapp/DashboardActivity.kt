package com.example.android.courseworkapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.android.courseworkapp.databinding.ActivityDashboardBinding
import com.example.android.courseworkapp.model.HostedGame
import com.example.android.courseworkapp.model.Place
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime

private const val PERMISSION_REQUEST = 10

class DashboardActivity : AppCompatActivity() {

    //check location permissions
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewbinding instead of kotlin synthetics
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser




        //viewbinding
//        binding.include.tvId.text = currentUser?.uid
//        binding.include.tvName.text = currentUser?.displayName
//        binding.include.tvEmail.text = currentUser?.email

        //load profile picture
//        Glide.with(this).load(currentUser?.photoUrl).into(binding.include.ivProfilePic)
//        binding.btnSignOut.setOnClickListener {
//            auth.signOut()
//            Intent(this, SignInActivity::class.java).also { startActivity(it) }
//            finish()
//        }

        //games data
        val hostedGames = generateSampleData()
        //set layout manager for rv
        binding.rvHostedGames.layoutManager = LinearLayoutManager(this)
        binding.rvHostedGames.adapter = GamesAdapter(this, hostedGames)


        //check location permissions
        if (checkPermission(permissions)) {
            Toast.makeText(this, "Location permission previously granted", Toast.LENGTH_LONG).show()
        } else {
            requestPermissions(permissions, PERMISSION_REQUEST)
        }

        //fragmentcontainer with google map
        supportFragmentManager.beginTransaction().add(
            R.id.fcGoogleMap,
            MapsFragment()
        ).commit()

    }

    //check location permissions
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateSampleData(): List<HostedGame> {
        return listOf(
            HostedGame(
                "Dung n drags",
                LocalDateTime.now(),
                "DnD",
                10,
                Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173)
            ),
            HostedGame("dnd 2",
                LocalDateTime.now(),
                "DnD",
                10,
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163)
                ),
            HostedGame("another dnd",
                LocalDateTime.now(),
                "DnD",
                10,
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864)

            ),
            HostedGame("3",
                LocalDateTime.now(),
                "DnD",
                10,
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)

            ),
            HostedGame("piss n d",
                LocalDateTime.now(),
                "DnD",
                10,
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),


            )
        )
    }
}