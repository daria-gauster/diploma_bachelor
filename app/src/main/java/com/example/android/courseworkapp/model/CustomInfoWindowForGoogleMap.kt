package com.example.android.courseworkapp.model

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.android.courseworkapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowForGoogleMap(context: Context): GoogleMap.InfoWindowAdapter {

    var mWindow: View = (context as Activity).layoutInflater.inflate(R.layout.dialog_gameinfo, null)

    private fun renderWindowText(marker: Marker, view: View){

        val tvEventTitle = view.findViewById<TextView>(R.id.tvEventTitle)
//        val tvSnippet = view.findViewById<TextView>(R.id.snippet)

        tvEventTitle.text = marker.title
//        tvSnippet.text = marker.snippet

    }

    override fun getInfoWindow(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }
}