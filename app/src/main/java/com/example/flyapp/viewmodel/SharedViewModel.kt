package com.example.flyapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.flyapp.models.*
import com.example.flyapp.repositories.AirportRepository
import com.example.flyapp.repositories.UserLocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(context: Context) : ViewModel() {

    private val myRepo = AirportRepository(initMyAirports())
    private var airportLiveData = MutableLiveData<List<Airport>>()
    private val userLocationRepository = UserLocationRepository(context)
    private val baseCase = "Most visited"
    private var firstTime: Boolean = true

    fun getAirportLiveData() : LiveData<List<Airport>> {
        if(firstTime) { // If the value has not been set before
            firstTime = false
            // We open a coroutine, because we are fetching information from our APIs
            CoroutineScope(Dispatchers.IO).launch {
                // And we use the setAirportLiveData()-function to initialize the LiveData
                setAirportLiveData(baseCase)
                Log.d("Action:", "First time LogIn AirportView")
            }
        }
        return airportLiveData
    }
    @Synchronized
    suspend fun setAirportLiveData(condition: String) {
        // Changes in LiveData-value cannot occur on a background thread
        // Therefore we use the postValue-function,
        // meant for setting the value of LiveData on a background thread
        when(condition) {
            "Most visited" -> {
                val test = getUserLocation().value
                if(test != null) {
                    airportLiveData.postValue(popularSort(calculateDistance(myRepo.init(), test.latitude, test.longitude)))
                }
                else airportLiveData.postValue(popularSort(myRepo.init()))
            }
            "Hottest" -> airportLiveData.postValue(hotSort(myRepo.init()))
            "Coldest" -> airportLiveData.postValue(coldSort(myRepo.init()))
            "Nearest//Farthest" -> {

                val loc = getUserLocation().value
                if(loc == null) {
                    Log.d("Location Error", "None found")
                    return
                }
                else {
                    airportLiveData.postValue(
                        distanceSort(myRepo.init(),
                            loc.latitude, loc.longitude))
                }
            }
        }
    }

    fun sortAirports(condition: String, loc: LatLng?) {
        if(airportLiveData.value == null) {
            Log.d("Unexpected error", "LiveData Airports is null on sort call")
            return
        }
        // We sort the already existing data, instead of making a new call to the API
        // This makes the most sense
        when(condition) {
            "Most visited" -> airportLiveData.value = popularSort(getAirportLiveData().value!!.toMutableList())
            "Hottest" -> airportLiveData.value = hotSort(getAirportLiveData().value!!.toMutableList())
            "Coldest" -> airportLiveData.value = coldSort(getAirportLiveData().value!!.toMutableList())
            "Nearest//Farthest" -> {
                if(loc == null) {
                    Log.d("Location Error", "None found")
                    return
                }
                else {
                    airportLiveData.value =
                        distanceSort(getAirportLiveData().value!!.toMutableList(),
                            loc.latitude, loc.longitude)
                }
            }
        }
    }

    fun updateWithGpsInfo() {
        if(airportLiveData.value != null) {
            val loc = getUserLocation().value!!
            airportLiveData.value = calculateDistance(airportLiveData.value!!.toMutableList(),
                    loc.latitude, loc.longitude)
        }
    }

    fun getUserLocation(): LiveData<LatLng> = userLocationRepository

}