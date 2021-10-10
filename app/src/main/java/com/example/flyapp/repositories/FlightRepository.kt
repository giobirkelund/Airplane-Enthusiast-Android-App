package com.example.flyapp.repositories


import android.util.Log
import com.example.flyapp.InternetStatus
import com.example.flyapp.models.Flight
import com.example.flyapp.models.OpenSky
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson

lateinit var response : OpenSky

class FlightRepository() {

    // Box covering Norway
    val path : String = "https://opensky-network.org/api/states/all?" +
            "lamin=57.7590052&lomin=4.0875274&lamax=71.3848787&lomax=31.7614911"

    val gson = Gson()

    var time: Number? = null
    var flightData: MutableList<Flight> = ArrayList()

    fun makeFlightObjects(states: MutableList<MutableList<Any?>>?): MutableList<Flight> {
        val flightInfo = mutableListOf<Flight>()
        if(states == null) return flightInfo // If there are no flights, we return an empty list
        for(flightData in states) {
            val icao24: String? = flightData[0].toString()
            val callsign: String? = flightData[1].toString()
            val originCountry: String? = flightData[2].toString()
            val longitude: Float? = flightData[5].toString().toFloat()
            val latitude: Float? = flightData[6].toString().toFloat()
            val trueTrack: Float? = flightData[10].toString().toFloat()
            val velocity: String = flightData[9].toString()
            val vertical_rate: String = flightData[11].toString()
            val geo_altitude: String = flightData[13].toString()

            flightInfo.add(Flight(icao24, callsign, originCountry, longitude, latitude, trueTrack, velocity, vertical_rate, geo_altitude))
        }
        return flightInfo
    }

    // Get response from request and return list with Flight objects
    suspend fun getFlightData(): MutableList<Flight>? {
        try {
            response = gson.fromJson(
                Fuel.get(path)
                    .timeoutRead(60000) // To avoid crashing after the default 15 000 ms
                    // OpenSky can be slow, which causes a timeout-error
                    .awaitString(), OpenSky::class.java
            )
        }
        catch(e: Exception) {
            if(InternetStatus.getConnection()==false) {
                Log.e("InternetError", "Returned an empty list")
            }
            return null
            this.flightData = makeFlightObjects(null)
            return this.flightData
        }
        time = response.time
        // Flight data from API
        val states: MutableList<MutableList<Any?>>? = response.states
        // Flight data from API parsed threw method to make flight objects

        this.flightData = makeFlightObjects(states)
        return this.flightData
    }
}

