package com.example.android.courseworkapp

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.courseworkapp.databinding.ActivityDashboardBinding
import com.example.android.courseworkapp.model.HostedGame
import com.example.android.courseworkapp.model.Place
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.clustering.ClusterManager
import java.time.LocalDateTime

private const val TAG = "DashboardActivity"
private const val PERMISSION_REQUEST = 10
private const val REQUEST_LOCATION = 1

class DashboardActivity : AppCompatActivity() {
//, ClusterManager.OnClusterClickListener<MyClusterItem>, ClusterManager.OnClusterItemClickListener<MyClusterItem>
    //check location permissions
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mGoogleMap: GoogleMap
    
    private val malaysiaCoordinate = LatLng(4.2105, 101.9758)
    private var mSpotMarkerList = ArrayList<Marker>()
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

        //fragmentcontainer with google map
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.apply{
//            getMapAsync(this@DashboardActivity)
//        }
        val mapsFragment = MapsFragment()

        supportFragmentManager.beginTransaction().add(
            R.id.fcGoogleMap,
            mapsFragment
        ).commit()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//        val btnCurrentLocation = findViewById<Button>(R.id.btnCurrentLocation)
//        btnCurrentLocation.setOnClickListener {
//            Log.e(TAG, "button")
//            Toast.makeText(this, "OJ", Toast.LENGTH_LONG).show()
//            fetchLocation()
//        }

    }


    //check location permissions
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }

    //fetch current location
    fun fetchLocation(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            val task: Task<Location> = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener {

                if (it != null) {
                    currentLocation = it
                    animateZoomInCamera(googleMap,
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                } else {

                    val REQUEST_CHECK_STATE = 12300 // any suitable ID
                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(reqSetting)

                    val client = LocationServices.getSettingsClient(this)
                    client.checkLocationSettings(builder.build()).addOnCompleteListener { task ->
                        try {
                            val state: LocationSettingsStates = task.result!!.locationSettingsStates
                            Log.d("salam", task.result!!.toString())
                            Log.e(
                                "LOG", "LocationSettings: \n" +
                                        " GPS present: ${state.isGpsPresent} \n" +
                                        " GPS usable: ${state.isGpsUsable} \n" +
                                        " Location present: " +
                                        "${state.isLocationPresent} \n" +
                                        " Location usable: " +
                                        "${state.isLocationUsable} \n" +
                                        " Network Location present: " +
                                        "${state.isNetworkLocationPresent} \n" +
                                        " Network Location usable: " +
                                        "${state.isNetworkLocationUsable} \n"
                            )
                        } catch (e: RuntimeExecutionException) {
                            Log.d("salam", "hei")
                            if (e.cause is ResolvableApiException)
                                (e.cause as ResolvableApiException).startResolutionForResult(
                                    this,
                                    REQUEST_CHECK_STATE
                                )
                        }
                    }

                    val locationUpdates = object : LocationCallback() {
                        override fun onLocationResult(lr: LocationResult) {
                            Log.e("salam", lr.toString())
                            Log.e("salam", "Newest Location: " + lr.locations.last())
                            // do something with the new location...
                            animateZoomInCamera(googleMap,
                                LatLng(
                                    lr.locations.last().latitude,
                                    lr.locations.last().longitude
                                )
                            )
                        }
                    }

                    fusedLocationProviderClient.requestLocationUpdates(
                        reqSetting,
                        locationUpdates,
                        null /* Looper */
                    )
                    fusedLocationProviderClient.removeLocationUpdates(locationUpdates)
                }
            }

        }
    }

    //permission check
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST) {
//            var allSuccess = true
//            for (i in permissions.indices) {
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    allSuccess = false
//                    val requestAgain = shouldShowRequestPermissionRationale(permissions[i])
//                    if (requestAgain) {
//                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Go to settings and enable the permission",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//            if (allSuccess)
//                Toast.makeText(this, "Location permission granted", Toast.LENGTH_LONG).show()
//        }
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                fetchLocation()
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

    private fun animateZoomInCamera(googleMap: GoogleMap, latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

    }

    private val reqSetting = LocationRequest.create().apply {
        fastestInterval = 10000
        interval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//
//    }


}