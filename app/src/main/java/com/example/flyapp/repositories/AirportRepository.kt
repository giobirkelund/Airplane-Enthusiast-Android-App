package com.example.flyapp.repositories

import android.util.Log
import com.example.flyapp.models.Airport
import com.example.flyapp.models.AirportMetar
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.*

class AirportRepository(private val airports: MutableList<Airport>) {

    // The path for the metar met-API, the token ICAO code is found in each airport-object
    private val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/tafmetar/1.0/metar.xml?" +
            "offset=+01:00&icao="
    private val currentWeatherRepo = AirportWeatherRepository()


    suspend fun init(): MutableList<Airport> {

        getWeatherDataForAllAirports() // fills all our airports with info
        return airports // And returns all airports with updated info


    }

    private suspend fun getWeatherDataForAllAirports() {
        for (ap in airports) { // Goes through all of our airports, and
            getMetarData(ap) // Sets the latest metar-observation, and
            currentWeatherRepo.getAirportWeatherData(ap) // Sets the current weather
        }
    }


    // The next two functions are for getting the metar-data from our API
    private suspend fun getMetarData(ap: Airport) {
        try{
            val mid = Fuel.get(path + ap.ICAO).awaitString()
            parseMetar(mid, ap)
        }catch(e:Exception){
            Log.e("CONNECTION", "getMetarData: internet not connected")
        }

    }

    private suspend fun parseMetar(text: String, ap: Airport) = withContext(Dispatchers.IO) {
        // We will get a list of all metar-data from that airport in the last 24 hour period
        val metarObservations = MetarParser().parse(text.byteInputStream())

        // But we only want the latest observation
        val lastIndex = metarObservations.size - 1
        val lastObservation = metarObservations[lastIndex]
        try{
            ap.metarObject = lastObservation
        }catch(e:InterruptedException){
            Log.e("CONNECTION","parseMetar: internet not connected")
            ap.metarObject = AirportMetar("","N/A" )
        }

    }

}