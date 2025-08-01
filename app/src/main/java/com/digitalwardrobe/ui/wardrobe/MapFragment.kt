package com.digitalwardrobe.ui.wardrobe

import com.digitalwardrobe.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        val BOLOGNA_POINT = LatLng(44.496781,11.356387)
        val position = CameraPosition.Builder()
            .target(BOLOGNA_POINT) // The central point
            .zoom(17f) // The zoom level
            .bearing(90f) // The clockwise angle from the north point
            .tilt(30f) // The viewing angle from the nadir
            .build()
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(position)
        )

        map.addMarker(
            MarkerOptions().position(BOLOGNA_POINT).title("Bologna")
        )
        map.isTrafficEnabled = true
    }

}