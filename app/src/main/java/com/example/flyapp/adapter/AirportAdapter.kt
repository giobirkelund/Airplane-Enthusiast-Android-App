package com.example.flyapp.adapter

import android.transition.AutoTransition
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flyapp.R
import com.example.flyapp.models.Airport

class AirportAdapter(private val airports: MutableList<Airport>, private val gpsEnabled: Boolean) : RecyclerView.Adapter<AirportAdapter.ViewHolder>() {
    var expandedViews = mutableListOf<Airport>()

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val airportName: TextView = view.findViewById(R.id.airport_name)
        var airportTemperature: TextView = view.findViewById(R.id.temperature_info)
        var airportWind: TextView = view.findViewById(R.id.wind_info)
        val precipitationRate: TextView = view.findViewById(R.id.precipitationRate_info)
        val windIcon: ImageView = view.findViewById(R.id.wind_dir)

        val airportMetarText: TextView = view.findViewById(R.id.metar_text)
        val imageOfAirport: ImageView = view.findViewById(R.id.ap_image)
        val icon: ImageView = view.findViewById(R.id.weather_icon)

        val expandIcon: ImageView = view.findViewById(R.id.expand_icon_view)
        val expandView: LinearLayout = view.findViewById(R.id.expandable_info)
        val cardView: CardView = view.findViewById(R.id.card_view)

        val distanceFromUser: TextView = view.findViewById(R.id.distance_view)
        val distanceFromUserText: TextView = view.findViewById(R.id.distance_view_set_text)
        val icaoView: TextView = view.findViewById(R.id.icao_view)
        val yearlyTravelers: TextView = view.findViewById(R.id.yearly_travelers)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.airport_element, parent, false)
        val holder = ViewHolder(view)
        /*
        holder.cardView.setOnClickListener {
            Log.d("Expand", "Click")
            Log.d("Who:", holder)
            if(holder.expandView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
                holder.expandView.visibility = View.VISIBLE
                holder.expandIcon.setImageResource(R.drawable.expand_icon_up)
            }
            else {
                TransitionManager.beginDelayedTransition(holder.cardView, Fade())
                holder.expandView.visibility = View.GONE
                holder.expandIcon.setImageResource(R.drawable.expand_icon)
            }
        }

         */
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAirport = airports[position]
        holder.airportName.text = currentAirport.name
        val temp = currentAirport.weatherObject.airTemp.toString()
        val wind = currentAirport.weatherObject.windSpeed.toString()
        val precipitation = "%.1f".format(currentAirport.weatherObject.precipitationRate)


        val iconAp = currentAirport.weatherObject.weatherIcon
        holder.airportTemperature.text = holder.airportTemperature.context.resources.getString(
                R.string.degrees_celsius, temp)
        holder.airportWind.text = holder.airportTemperature.context.resources.getString(
                R.string.meters_per_second, wind)
        holder.precipitationRate.text = holder.precipitationRate.context.resources.getString(
                R.string.precipitation_amount, precipitation)

        var direction = currentAirport.weatherObject.windFromDirection!!.toFloat()
        direction += if (direction>0) -180 else +180
        holder.windIcon.rotation = direction

        Glide.with(holder.imageOfAirport.context)
            .load(currentAirport.imgURL).into(holder.imageOfAirport)

        val mid = holder.icon.context
        val resID: Int
        resID = if(iconAp != null) {
            mid.resources.getIdentifier(iconAp , "drawable", mid.packageName)
        }
        else {
            mid.resources.getIdentifier("unknown_weather", "drawable", mid.packageName)
        }

        holder.icon.setImageResource(resID)
        fillExpandableView(holder, currentAirport)

        // This code might look slightly confusing or over-complicated, but there is a 'glitch'
        // with recyclerview and onClickListeners where every 8th item would be affected
        // by the code inside the onClickListeners, or rather the UI-changes it would make,
        // meaning that if you expanded a view, the one 7 places in front would also expand.
        // It is not really a glitch, and is expected behaviour by the caching of the recyclerview
        if(currentAirport.isExpanded) {
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.expandView.visibility = View.VISIBLE
            holder.expandIcon.setImageResource(R.drawable.expand_icon_up)
        }
        else {
            TransitionManager.beginDelayedTransition(holder.cardView, Fade())
            holder.expandView.visibility = View.GONE
            holder.expandIcon.setImageResource(R.drawable.expand_icon)
        }

        // In this case, all the onClickListener will do, is change whether a certain Airport is
        // going to be expanded or not, and notify the adapter
        holder.cardView.setOnClickListener {
            Log.d("Expand", "Click")
            Log.d("Who", holder.adapterPosition.toString())
            currentAirport.isExpanded = !currentAirport.isExpanded
            notifyDataSetChanged()
        }

    }

    private fun fillExpandableView(holder: ViewHolder, currentAirport: Airport) {
        val distanceInKm = "%.2f".format(currentAirport.distanceFromUser/1000)
        if(gpsEnabled) holder.distanceFromUser.visibility = View.VISIBLE
        if(gpsEnabled) holder.distanceFromUserText.visibility = View.VISIBLE
        holder.distanceFromUser.text = holder.distanceFromUser.context.resources.getString(
                R.string.distance_in_km, distanceInKm)
        holder.icaoView.text = currentAirport.ICAO
        holder.airportMetarText.text = currentAirport.metarObject.metarText
        holder.yearlyTravelers.text = currentAirport.yearlyPassengers.toString()
    }


    override fun getItemCount() = airports.size

}
