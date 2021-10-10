package com.example.flyapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.flyapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class FlightInfoWindowAdapter @SuppressLint("InflateParams") constructor(cont: Context?) : GoogleMap.InfoWindowAdapter {

    private val window: View
    private val windowAirport: View

    private val context: Context? = cont

    init {
        window = LayoutInflater.from(context).inflate(R.layout.flight_info_window, null)
        windowAirport = LayoutInflater.from(context).inflate(R.layout.airport_info_window, null)
    }

    private fun renderFlight(marker: Marker, view: View) {
        val snippet = marker.snippet
        val title = marker.title
        val textViewS = view.findViewById<TextView>(R.id.snippet)
        val textViewT = view.findViewById<TextView>(R.id.title)
        textViewS.text = snippet
        textViewT.text = title

    }

    private fun renderAirport(marker: Marker, view: View) {
        val lines = marker.snippet.lines()
        val textViewName = view.findViewById<TextView>(R.id.airport_name)
        val picView = view.findViewById<ImageView>(R.id.ap_image)
        val airTempView = view.findViewById<TextView>(R.id.temperature_info)
        val windInfoView = view.findViewById<TextView>(R.id.wind_info)
        val weatherIcon: ImageView = view.findViewById(R.id.weather_icon)
        val windDir: ImageView = view.findViewById(R.id.wind_dir)
        val precipitation = view.findViewById<TextView>(R.id.precipitationRate)

        val iconAp = lines[3]
        val mid = weatherIcon.context
        val resID: Int
        resID = mid.resources.getIdentifier(iconAp , "drawable", mid.packageName)

        weatherIcon.setImageResource(resID)

        textViewName.text = lines[0]
        Glide.with(picView.context).load(lines[1]).into(picView)

        airTempView.text = airTempView.context.resources.getString(
                R.string.degrees_celsius, lines[2])
        windInfoView.text = windInfoView.context.resources.getString(
                R.string.meters_per_second, lines[4])
        var direction = lines[5].toFloat()
        direction += if (direction>0) -180 else +180
        windDir.rotation = direction

        precipitation.text = precipitation.context.resources.getString(
                R.string.precipitation_amount, lines[6])


    }


    override fun getInfoContents(marker: Marker?): View? {
        if(marker?.tag == "Flight") {
            renderFlight(marker, window)
            return window
        }
        return if(marker?.tag == "Airport") {
            renderAirport(marker, windowAirport)
            windowAirport
        } else null

    }

    // This returns 'null', so that the InfoWindow has the "speech-bubble tail"
    override fun getInfoWindow(param: Marker?): View? = null

}

