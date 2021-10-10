package com.example.flyapp.models


import android.util.Log
import com.beust.klaxon.*

val klaxon = Klaxon()

data class AirportWeather (
        val type: String,
        val geometry: Geometry,
        val properties: Properties
) {
    // Method below is not used
    // fun toJson() = klaxon.toJsonString(this)



        fun getWeatherData(): AirportWeatherObject? {
            val airTemp = properties.timeseries[0].data.instant.details.airTemperature
            val windFromDirection = properties.timeseries[0].data.instant.details.windFromDirection
            val windSpeed = properties.timeseries[0].data.instant.details.windSpeed
            val icon: String = try {
                properties.timeseries[0].data.next1_Hours?.summary!!.symbolCode
            } catch (e: NullPointerException) {
                Log.d("Exception:", "Null pointer icon")
                "unknown_weather"
            }
            val precipitationRate = properties.timeseries[0].data.instant.details.precipitationRate
            val precipitationAmount = properties.timeseries[0].data.next1_Hours!!.details.precipitationAmount
            return AirportWeatherObject(airTemp,windFromDirection,windSpeed,icon, precipitationAmount)
        }
}

data class Geometry (
        val type: String,
        val coordinates: List<Double>
)

data class Properties (
        val meta: Meta,
        val timeseries: List<Timesery>
)

data class Meta (
        @Json(name = "updated_at")
        val updatedAt: String,

//        val units: Units,

        @Json(name = "radar_coverage")
        val radarCoverage: String
)

data class Timesery (
        val time: String,
        val data: Data
)

data class Data (
        val instant: Instant,

        @Json(name = "next_1_hours")
        val next1_Hours: Next1_Hours? = null
)

data class Instant (
        val details: InstantDetails
)

data class InstantDetails (
        @Json(name = "air_temperature")
        val airTemperature: Double? = null,

        @Json(name = "precipitation_rate")
        val precipitationRate: Double? = null,

        @Json(name = "relative_humidity")
        val relativeHumidity: Double? = null,

        @Json(name = "wind_from_direction")
        val windFromDirection: Double? = null,

        @Json(name = "wind_speed")
        val windSpeed: Double? = null
)

data class Next1_Hours (
        val summary: Summary,
        val details: Next1_HoursDetails
)

data class Next1_HoursDetails (
        @Json(name = "precipitation_amount")
        val precipitationAmount: Double
)

data class Summary (
        @Json(name = "symbol_code")
        val symbolCode: String
)
