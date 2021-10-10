package com.example.flyapp.models

data class Airport(val ICAO: String, val name: String, val lat: Double,
                   val lon: Double, val yearlyPassengers: Int, val imgURL: String) {
    lateinit var weatherObject: AirportWeatherObject
    lateinit var metarObject: AirportMetar
    var distanceFromUser: Double = 0.0
    var isExpanded = false // This variable is used by AirportAdapter, go there for an exPLANEation

    fun newDistance(d: Double) {
        distanceFromUser = d
    }
}

