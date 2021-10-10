package com.example.flyapp.models

import com.example.flyapp.repositories.AirportRepository
import com.example.flyapp.repositories.AirportWeatherRepository
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AirportWeatherTest{

    @Throws(IOException::class)
    fun readData(fn: String): String {
        var steam: InputStream? = null
        try {
            steam =
                    javaClass.classLoader?.getResourceAsStream(fn)
            val sb = StringBuilder()
            val bReader = BufferedReader(InputStreamReader(steam))

            var str: String? = bReader.readLine()
            while (str != null) {
                sb.append(str)
                str = bReader.readLine()
            }
            return sb.toString()
        } finally {
            steam?.close()
        }
    }

    @Test
    fun `Test fail if AirportWeatherObject is not made using data without null values`(){
        val data = readData("weatherData.txt")
        val response = AirportWeatherRepository().fromJson(data)
        val airportWeatherObject = response?.getWeatherData()
        print(airportWeatherObject)

        val resultOfTest = (airportWeatherObject is AirportWeatherObject)
        assertEquals(resultOfTest, true)
    }

    @Test
    fun `Test fail if AirportWeatherObject is not made using data with null values`(){
        val data = readData("weatherDataNullValues.txt")
        val response = AirportWeatherRepository().fromJson(data)
        val airportWeatherObject = response?.getWeatherData()
        print(airportWeatherObject)

        val resultOfTest = (airportWeatherObject is AirportWeatherObject)
        assertEquals(resultOfTest, true)
    }

    @Test
    fun `Test fail if AirportWeatherObject is not made using data with no precipitation_rate`(){
        val data = readData("weatherDataNoValue.txt")
        val response = AirportWeatherRepository().fromJson(data)
        val airportWeatherObject = response?.getWeatherData()
        print(airportWeatherObject)

        val resultOfTest = (airportWeatherObject is AirportWeatherObject)
        assertEquals(resultOfTest, true)
    }

}