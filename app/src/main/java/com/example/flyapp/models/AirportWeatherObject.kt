package com.example.flyapp.models

data class AirportWeatherObject (
    val airTemp : Double? = 0.0,
    val windFromDirection : Double? = 0.0,
    val windSpeed : Double? = 0.0,
    val weatherIcon : String? = "unknown_weather",
    val precipitationRate: Double? = 0.0
    )

