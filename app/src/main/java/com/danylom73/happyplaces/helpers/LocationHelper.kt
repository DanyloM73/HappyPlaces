package com.danylom73.happyplaces.helpers

import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.danylom73.happyplaces.AddHappyPlaceActivity
import com.danylom73.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationHelper(
    private val activity: AddHappyPlaceActivity,
    private val binding: ActivityAddHappyPlaceBinding
) {
    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                updateLocation(location.latitude, location.longitude)
            } else {
                Toast.makeText(
                    activity,
                    "Looks like you forgot enable location provider",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(activity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val locationText = address.getAddressLine(0)
                binding.locationEt.setText(locationText)
                activity.latitude = latitude
                activity.longitude = longitude
            } else {
                Toast.makeText(activity, "Unable to get address", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Updating location error", Toast.LENGTH_SHORT).show()
        }
    }
}