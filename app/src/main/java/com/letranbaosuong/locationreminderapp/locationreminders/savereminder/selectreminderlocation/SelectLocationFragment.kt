package com.letranbaosuong.locationreminderapp.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.letranbaosuong.locationreminderapp.R
import com.letranbaosuong.locationreminderapp.base.BaseFragment
import com.letranbaosuong.locationreminderapp.databinding.FragmentSelectLocationBinding
import com.letranbaosuong.locationreminderapp.locationreminders.savereminder.SaveReminderViewModel
import com.letranbaosuong.locationreminderapp.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

@Suppress("DEPRECATION")
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private val requestPermissionLocation = 1
    private lateinit var map: GoogleMap
    private val tag = SelectLocationFragment::class.java.simpleName
    private var currentPointOfInterest: PointOfInterest? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSelectLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // TODO: add the map setup implementation
        // TODO: zoom to the user location after taking his permission
        // TODO: add style to the map
        // TODO: put a marker to location that the user selected

        // TODO: call this function after the user confirms on the selected location
        binding.btnSave.setOnClickListener {
            onLocationSelected()
        }
        return binding.root
    }

    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
        currentPointOfInterest?.let {
            _viewModel.savePoint(it)
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "inflater.inflate(R.menu.map_options, menu)", "com.letranbaosuong.locationreminderapp.R"
        )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        configMyLocation()
        setMapStyle(map)
        setPointOfInterestClick(map)
    }

    private fun reloadMap() {
        map.clear()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSelectLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configMyLocation() {
        if (isPermissionLocationGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            getUserLocationAndMoveCamera()
        } else {
            getRequestPermissions()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == requestPermissionLocation) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                configMyLocation()
            }
        }
    }

    private fun isPermissionLocationGranted(): Boolean =
        isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && isPermissionGranted(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private fun isPermissionGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun setPointOfInterestClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions().position(poi.latLng).title(poi.name)
            )
            poiMarker?.showInfoWindow()
            currentPointOfInterest = poi
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, 15f))
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )

            if (!success) {
                Timber.e(tag, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(tag, "Can't find style. Error: $e")
        }
    }

    private fun getUserLocationAndMoveCamera() {
        if (isPermissionLocationGranted()) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val overlaySize = 100f
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val androidOverlay =
                        GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.android))
                            .position(currentLatLng, overlaySize)
                    map.addGroundOverlay(androidOverlay)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng, 15f
                        )
                    )
                }
            }
        } else {
            getRequestPermissions()
        }
    }

    private fun getRequestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), requestPermissionLocation
        )
        reloadMap()
    }
}