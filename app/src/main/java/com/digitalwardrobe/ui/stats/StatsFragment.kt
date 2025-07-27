package com.digitalwardrobe.ui.stats

import com.digitalwardrobe.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class StatsFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val mapFragment = view.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)*/
    }

    override fun onMapReady(map: GoogleMap) {
        val kyoto = LatLng(35.00116, 135.7681)
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.addMarker(
            MarkerOptions().position(kyoto).title("Kyoto")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(kyoto, 15f))
        map.isTrafficEnabled = true
    }

}