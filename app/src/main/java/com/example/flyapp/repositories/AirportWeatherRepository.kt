package com.example.flyapp.repositories

import com.example.flyapp.models.Airport
import com.example.flyapp.models.AirportWeather
import com.example.flyapp.models.klaxon
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString

class AirportWeatherRepository {

    suspend fun getAirportWeatherData(airport: Airport) {
        val lat = airport.lat; val lon = airport.lon
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/nowcast/2.0/complete?lat=$lat&lon=$lon"


        val response = fromJson(Fuel.get(path).awaitString())

        val airportWeatherObject = response?.getWeatherData()

        if(airportWeatherObject != null) airport.weatherObject = airportWeatherObject

    }

    fun fromJson(json: String) = klaxon.parse<AirportWeather>(json)

}
