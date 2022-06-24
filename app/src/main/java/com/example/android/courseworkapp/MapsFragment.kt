package com.example.android.courseworkapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android.courseworkapp.model.CustomInfoWindowForGoogleMap
import com.example.android.courseworkapp.model.DialogEventTitle
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    private lateinit var btnCurrentLocation: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var googleMapObject: GoogleMap
    private lateinit var geocoder: Geocoder
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        // These are both view groups containing an ImageView with id "badge" and two
        // TextViews with id "title" and "snippet".
        private val window: View = layoutInflater.inflate(R.layout.dialog_gameinfo, null)

        override fun getInfoWindow(marker: Marker): View? {
//            window.background = R.drawable.vereinigungsmenge_7
            window.findViewById<TextView>(R.id.tvEventTitle).text = marker.title
            window.findViewById<TextView>(R.id.tvEventInfo).text = marker.snippet

            return window



        }

        override fun getInfoContents(marker: Marker): View? {

            return null


        }

    }


    //maps callback
    @SuppressLint("PotentialBehaviorOverride")
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.apply {
            setOnMapLongClickListener(this@MapsFragment)
            setOnMarkerDragListener(this@MapsFragment)
            setOnMarkerClickListener(this@MapsFragment)
            setInfoWindowAdapter(CustomInfoWindowAdapter())

            uiSettings.isZoomControlsEnabled = true

            val success: Boolean = googleMap.setMapStyle(
                MapStyleOptions(
                    resources
                        .getString(R.string.style_json)
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }

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
//        val dialogEventTitle = DialogEventTitle(activity as DashboardActivity)
        Log.d(TAG, "onMapLongClick: $latLng")
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.isNotEmpty()) {
//                dialogEventTitle.show()
                val address: Address = addresses[0]
//                val streetAddress: String = address.getAddressLine(0)
                Log.d(TAG, "inside try block")
                showGameinfoDialog(latLng)
//                googleMapObject.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title(title)
//                        .draggable(true)
//                )
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


    private val reqSetting = LocationRequest.create().apply {
        fastestInterval = 10000
        interval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }

    private fun showGameinfoDialog(latLng: LatLng) {
        val dialog = Dialog(activity as DashboardActivity)
//        var gameTitle: String = "Default"

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_gametitle)

        //get new prefs
        dialog.findViewById<Button>(R.id.btnAccept).setOnClickListener {
            Log.d(TAG, dialog.findViewById<EditText>(R.id.etGameTitle).text.toString())
            googleMapObject.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(dialog.findViewById<EditText>(R.id.etGameTitle).text.toString())
                    .snippet(getGameSnippet(dialog))
                    .draggable(true)
            )
            Log.e(TAG, getGameSnippet(dialog))
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getGameSnippet(dialog: Dialog): String {
        return "Game: " + dialog.findViewById<EditText>(R.id.dialog_spinner_game).text.toString() +
                "\nAt: " + dialog.findViewById<EditText>(R.id.dialog_spinner_time).text.toString() +
                "\nPlayers: "+ dialog.findViewById<EditText>(R.id.dialog_spinner_players).text.toString()
    }


    override fun onMarkerClick(marker: Marker): Boolean {

        marker.showInfoWindow()
        return true
    }
}