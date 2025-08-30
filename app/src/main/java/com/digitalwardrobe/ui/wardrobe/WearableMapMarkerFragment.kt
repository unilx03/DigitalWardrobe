package com.digitalwardrobe.ui.wardrobe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digitalwardrobe.R
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class WearableMapMarkerFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: WearableViewModel
    private lateinit var googleMap: GoogleMap
    private var currentWearableId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        currentWearableId = arguments?.getLong("wearableId") ?: -1L

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // Use your Fragment as the callback
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            googleMap.isMyLocationEnabled = true
        }

        lifecycleScope.launch {
            val selectedWearable = viewModel.getWearableById(currentWearableId)
            if (selectedWearable != null) {

                val boundsBuilder = LatLngBounds.Builder()
                var hasValidBounds = false

                // show existing marker
                if (selectedWearable.locationLat != null && selectedWearable.locationLng != null) {
                    val location = LatLng(selectedWearable.locationLat, selectedWearable.locationLng)
                    googleMap.addMarker(MarkerOptions().position(location))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                    boundsBuilder.include(location)
                    hasValidBounds = true
                }
                else {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val currentLatLng = LatLng(it.latitude, it.longitude)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        }
                    }
                }

                if (hasValidBounds) {
                    val bounds = boundsBuilder.build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }

        googleMap.setOnMapClickListener { newLatLng ->
            Log.d("Map_Tag", "CLICK")
            Log.v("coordinates", newLatLng.toString())

            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(newLatLng))

            updateCoordinates(currentWearableId, newLatLng.latitude, newLatLng.longitude)
        }
    }

    private fun updateCoordinates(wearableId: Long, lat: Double, lng: Double) {
        lifecycleScope.launch {
            val selectedWearable = viewModel.getWearableById(wearableId)
            selectedWearable?.let {
                val updatedWearable = it.copy(
                    locationLat = lat,
                    locationLng = lng
                )
                viewModel.update(updatedWearable)
            }
        }
    }
}
