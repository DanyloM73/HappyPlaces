package com.danylom73.happyplaces.helpers

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class PermissionHelper(
    private val activity: AppCompatActivity,
    private val imageHelper: ImageHelper,
    private val locationHelper: LocationHelper
) {
    private val requestPermission = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            if (isGranted) {
                when (permission) {
                    Manifest.permission.READ_MEDIA_IMAGES -> imageHelper.openGallery()
                    Manifest.permission.CAMERA -> imageHelper.openCamera()
                    Manifest.permission.ACCESS_FINE_LOCATION -> locationHelper.getCurrentLocation()
                }
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestStoragePermission() {
        when {
            activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED -> {
                imageHelper.openGallery()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.READ_MEDIA_IMAGES) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                requestPermission.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            }
        }
    }

    fun requestCameraPermission() {
        when {
            activity.checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> {
                imageHelper.openCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.CAMERA) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                requestPermission.launch(arrayOf(Manifest.permission.CAMERA))
            }
        }
    }

    fun requestLocationPermission() {
        when {
            activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
            activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED -> {
                locationHelper.getCurrentLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                requestPermission.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage("You have denied the permission. " +
                    "Please enable it in settings to use this feature.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", activity.packageName, null))
                activity.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
