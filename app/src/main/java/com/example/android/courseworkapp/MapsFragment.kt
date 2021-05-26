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

        googleMap.apply {
            setOnMapLongClickListener(this@MapsFragment)
            setOnMarkerDragListener(this@MapsFragment)
            uiSettings.isZoomControlsEnabled = true
        }
        googleMapObject = googleMap

        @SuppressLint("MissingPermission")
        if ((activity as DashboardActivity).checkPermission(permissions)) {
            googleMapObject.isMyLocationEnabled = true
            zoomToUserLocation()
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


    }

    @SuppressLint("MissingPermission")
    private fun zoomToUserLocation() {
        val locationTask = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            googleMapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }
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