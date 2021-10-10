package com.example.flyapp.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng


class UserLocationRepository(context: Context): LiveData<LatLng>() {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback

    override fun onActive() {
        startLocationUpdates()
        getUserLocation()
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if(it == null) {
                Log.d("Null-value", "in location")
            }
            else {
                Log.d("Location", "Fetched in getUserLocation()")
                value = LatLng(it.latitude, it.longitude)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    value = LatLng(location.latitude, location.longitude)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create(),
                locationCallback,
                Looper.getMainLooper())
    }
}