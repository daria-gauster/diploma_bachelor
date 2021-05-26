package com.example.android.courseworkapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task

private const val TAG = "MapsFragment"

class MapsFragment : Fragment() {
    lateinit var btnCurrentLocation: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    lateinit var googleMapObject: GoogleMap
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        googleMapObject = googleMap
        //default location is odesa
        val location = LatLng(46.0, 30.0)


        googleMap.addMarker(MarkerOptions().position(location).title("Marker in Odesa"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        googleMap.uiSettings.isZoomControlsEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.context)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        btnCurrentLocation.setOnClickListener {
            Log.e("fragment map", "button")
            mapFragment?.getMapAsync(callback)
            if ((activity as DashboardActivity).checkPermission(permissions)) {
                fetchLocation()
            }
        }
    }

    //fetch current location
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
            val task: Task<Location> = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    animateZoomInCamera(
                        googleMapObject,
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                } else {
//                    val REQUEST_CHECK_STATE = 12300 // any suitable ID
                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(reqSetting)

                    val client = LocationServices.getSettingsClient(view?.context)
                    client.checkLocationSettings(builder.build()).addOnCompleteListener { task ->
                        try {
                            val state: LocationSettingsStates = task.result!!.locationSettingsStates
                            Log.d(TAG, task.result!!.toString())
                            Log.e(
                                TAG, "LocationSettings: \n" +
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
                            Log.d(TAG, e.toString())
                        }
                    }

                    val locationUpdates = object : LocationCallback() {
                        override fun onLocationResult(lr: LocationResult) {
                            Log.e(TAG, lr.toString())
                            Log.e(TAG, "Newest Location: " + lr.locations.last())
                            animateZoomInCamera(
                                googleMapObject,
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
//        }
    }

    private fun animateZoomInCamera(googleMap: GoogleMap,latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

    }

    private val reqSetting = LocationRequest.create().apply {
        fastestInterval = 10000
        interval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }
}