package com.example.wander

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.wander.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val latitude = 26.153970718931866
        val longitude = 32.7170134495738
        val overLaySize = 100f
        val zoomLevel = 15f

        val homeLatLng = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(homeLatLng))

        // Add a marker in MyHome and move the camera
        map.addMarker(MarkerOptions().position(homeLatLng).title("This is My Home"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

        val GoroundOverLay = GroundOverlayOptions().position(homeLatLng, overLaySize)
            .image(BitmapDescriptorFactory.fromResource(R.drawable.download))

        enableMyLocation()
        map.addGroundOverlay(GoroundOverLay)
        setPoiClick(map)
        setMapLongClick(map)
        setMapStyle(map)
    }

    //  add a marker when the user touches and holds a location on the map
    private fun setMapLongClick(map: GoogleMap) {

        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
            )
            map.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.dropped_pin))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .snippet(snippet)

            )
        }
    }

    //  add a marker when the user chooses a POI location on the map

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { PointOfInterest ->

            val poiMarker = map.addMarker(
                MarkerOptions().position(PointOfInterest.latLng).title(PointOfInterest.name)
                    //style Marker
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            )
            poiMarker?.showInfoWindow()
        }

    }

    // Customize the styling of the base map using a JSON object defined
    // in a raw resource file.
    private fun setMapStyle(map: GoogleMap) {
        try {

            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    // regionPermission

    private fun isPermissionGranted(): Boolean {
        Log.e(TAG, "isPermissionGranted")
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled
            Log.e(TAG, "enableMyLocation Granted")

        } else {
            Log.e(TAG, "enableMyLocation requestPermissions")

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
                Log.e(TAG, "onRequestPermissionsResult() enableMyLocation")
            }
        }
    }

    //endregion

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}