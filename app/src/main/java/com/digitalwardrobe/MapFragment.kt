package com.digitalwardrobe

import android.app.PendingIntent
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digitalwardrobe.data.DigitalWardrobeRoomDatabase
import com.digitalwardrobe.data.GeofenceVisit
import com.digitalwardrobe.data.GeofenceVisitRepository
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var wearableViewModel: WearableViewModel
    private lateinit var geofencingClient: GeofencingClient

    private val geofenceRadiusMeters = 10.0

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

        wearableViewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            map.isMyLocationEnabled = true
        }

        lifecycleScope.launch {
            val wearables = wearableViewModel.getAllWearablesWithLocations()
            val boundsBuilder = LatLngBounds.Builder()

            if (!wearables.isNullOrEmpty()) {
                for (wearable in wearables) {
                    if (wearable.locationLat != null && wearable.locationLng != null) {
                        val position = LatLng(wearable.locationLat, wearable.locationLng)
                        val marker = map.addMarker(
                            MarkerOptions()
                                .position(position)
                        )
                        marker?.tag = wearable
                        boundsBuilder.include(position)

                        addGeofence(wearable.id.toString(), position)

                        map.addCircle(
                            com.google.android.gms.maps.model.CircleOptions()
                                .center(position)
                                .radius(geofenceRadiusMeters) // radius in meters, same as geofence radius
                                .strokeColor(0x550000FF) // semi-transparent blue stroke
                                .fillColor(0x220000FF) // very transparent blue fill
                                .strokeWidth(2f)
                        )
                    }
                }

                val bounds = boundsBuilder.build()
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }

        map.setOnMarkerClickListener { marker ->
            val wearable = marker.tag as? Wearable
            wearable?.let {
                showWearableCard(it)
            }
            true
        }
    }

    private fun showWearableCard(wearable: Wearable) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.map_show_wearable, null)

        val imageView = dialogView.findViewById<ImageView>(R.id.wearableImageView)
        val bitmap = BitmapFactory.decodeStream(
            context?.contentResolver?.openInputStream(wearable.image.toUri())
        )
        imageView.setImageBitmap(bitmap)

        //show number of visits in last month from geofencevisit
        val repo = context?.let { DigitalWardrobeRoomDatabase.getDatabase(it).geofenceVisitDao() }?.let {
            GeofenceVisitRepository(
                it
            )
        }

        val oneMonthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000) // 30 days in ms

        CoroutineScope(Dispatchers.IO).launch {
            // Count visits in last month
            val visitCount = repo?.countVisitsSince(wearable.id.toString(), oneMonthAgo)
            dialogView.findViewById<MaterialTextView>(R.id.markerVisitCount).text = visitCount.toString()
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

    fun addGeofence(markerId: String, latLng: LatLng) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                1002
            )
        }

        val geofence = Geofence.Builder()
            .setRequestId(markerId)
            .setCircularRegion(latLng.latitude, latLng.longitude, geofenceRadiusMeters.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
    }
}