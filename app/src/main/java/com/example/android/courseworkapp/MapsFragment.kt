package com.example.android.courseworkapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task
import java.io.IOException


private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener  {
    private lateinit var btnCurrentLocation: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var googleMapObject: GoogleMap
    private lateinit var geocoder: Geocoder
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //maps callback
    private val callback = OnMapReadyCallback { googleMap ->
        googleMapObject = googleMap

        //default location is odesa
        val location = LatLng(46.0, 30.0)

        googleMap.apply {
            setOnMapLongClickListener(this@MapsFragment)
            setOnMarkerDragListener(this@MapsFragment)
            addMarker(MarkerOptions().position(location).title("Default marker"))
            animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            uiSettings.isZoomControlsEnabled = true
        }
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
        geocoder = Geocoder(view.context)

        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        btnCurrentLocation.setOnClickListener {
            Log.d(TAG, "button clicked")
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
                zoomToUserLocation(
                    googleMapObject,
                    LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            } else {
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
                        zoomToUserLocation(
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

    override fun onMapLongClick(latLng: LatLng) {
        Log.d(TAG, "onMapLongClick: $latLng")
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val streetAddress: String = address.getAddressLine(0)
                googleMapObject.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onMarkerDragStart(marker: Marker?) {
        Log.d(TAG, "onMarkerDragStart: ")
    }

    override fun onMarkerDrag(marker: Marker?) {
        Log.d(TAG, "onMarkerDrag: ")
    }

    override fun onMarkerDragEnd(marker: Marker) {
        Log.d(TAG, "onMarkerDragEnd: ")
        val latLng = marker.position
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                marker.title = streetAddress
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun zoomToUserLocation(googleMap: GoogleMap, latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.addMarker(MarkerOptions().position(latLng))
    }

    private val reqSetting = LocationRequest.create().apply {
        fastestInterval = 10000
        interval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }
}