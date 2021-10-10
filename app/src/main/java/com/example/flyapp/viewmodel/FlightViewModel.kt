package com.example.flyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flyapp.models.Flight
import com.example.flyapp.repositories.FlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlightViewModel() : ViewModel(){
    private val flightApi = FlightRepository()
    private var mapsLiveData = MutableLiveData<List<Flight>?>()
    private var firstTime = true

    fun getFlightLiveData() : LiveData<List<Flight>?> {
        if(firstTime) { // If the value has not been set before
            // We open a coroutine, because we are fetching information from our APIs
            firstTime = true
            CoroutineScope(Dispatchers.IO).launch {
                // And we use the setAirportLiveData()-function to initialize the LiveData
                setFlightLiveData()
                Log.d("Action:", "First time LogIn MapView")
            }
        }
        return mapsLiveData
    }


    suspend fun setFlightLiveData() {
        mapsLiveData.postValue(flightApi.getFlightData())
    }
}