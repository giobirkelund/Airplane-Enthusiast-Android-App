package com.example.flyapp.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.flyapp.InternetStatus
import com.example.flyapp.R
import com.example.flyapp.adapter.FlightInfoWindowAdapter
import com.example.flyapp.models.Airport
import com.example.flyapp.models.Flight
import com.example.flyapp.viewmodel.FlightViewModel
import com.example.flyapp.viewmodel.SharedViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapsFragment(mSharedViewModel: SharedViewModel) : Fragment(), OnMapReadyCallback {

    private val myViewModel = FlightViewModel()
    private val sharedViewModel = mSharedViewModel
    private var markers = mutableListOf<Marker>()
    private lateinit var mMap: GoogleMap
    private var firstTime = true
    private var airportMarkers = mutableListOf<Marker>()

    private var clickedMarkerId: String? = null

    private var prevUserZoom = 5.0f
    private var prevUserTarget: LatLng = LatLng(64.855, 12.496)

    private lateinit var userMarker: MarkerOptions



    override fun onPause() {
        super.onPause(); Log.d("onPause", "call")

        // We save the camera-position, so it stays the same when you reenter the MapFragment
        prevUserZoom = mMap.cameraPosition.zoom
        prevUserTarget = mMap.cameraPosition.target

        for(marker in markers) { // We want to remove the markers
            // Not doing so may result in duplication of Airplane-markers
            marker.remove()
        }
        markers = mutableListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        Log.d("Call", "OnCreate, MapsFragment")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Below code needs to be in onViewCreated and not onCreateView
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("Call", "OnMapReady")

        mMap = googleMap

        // Here we place the marker for the users location and for all the airports
        mMap.uiSettings.isMyLocationButtonEnabled = false // We do not want or need this button
        mMap.uiSettings.isMapToolbarEnabled = false // Same goes for this

        mMap.setInfoWindowAdapter(FlightInfoWindowAdapter(context))
        mMap.setOnInfoWindowClickListener {
            it.hideInfoWindow()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prevUserTarget, prevUserZoom))
        firstTime = false

        mMap.setOnMarkerClickListener {
            clickedMarkerId = it.id
            false
        }

        // We set the observers when the onMapReady is called
        setUpObservers()
    }


    private fun placeFlights(flights: MutableList<Flight>) {
        Log.d("Call", "Placing Flights")
        if(!isConnected()) return
        if(flights.isEmpty()) {
            Log.d("No flights", "Returned from API")
            for(marker in markers) {
                marker.remove()
            }
            return
        }

        // Update and/or remove old markers
        for(marker in markers) { // Go through each marker
            var remove = true
            for(flight in flights) { // And each piece of new plane-information
                if(flight.callsign == marker.title) { // And if they have the same ID,
                    updateMarker(flight, marker) // We update the position of that marker,
                                                    // instead of creating a new one
                    flights.remove(flight) // We then remove the flight from the flight-list
                    // So we can run through the remaining flight-list after this nested for-loop
                    remove = false; break
                }
            }
            if(remove) { // We remove a marker if a plane is no longer recorded
                marker.remove()
                Log.d("Remove", "No longer in-air")
            }
        }

        // Add new flights
        for(flight in flights) {
            val latitude: Double = flight.latitude!!.toDouble()
            val longitude: Double = flight.longitude!!.toDouble()
            val angle: Float = flight.true_track!!
            val pos = LatLng(latitude, longitude)

            // Set marker as flight icon on map
            val planeMark = mMap.addMarker(
                    MarkerOptions().position(pos).snippet(flightInfo(flight)).flat(true).icon(activity?.let {
                        bitmapDescriptorFromVector(it, R.drawable.flight_icon)
                    }).title(flight.callsign)
            )
            planeMark.tag = "Flight"
            markers.add(planeMark) // Adds to our MutableList of markers

            planeMark.setInfoWindowAnchor(0.5f, 0.5f) // Middle-ish of the airplane-vector
            // Sets the correct rotation of the plane
            planeMark.rotation = angle
        }
    }

    private fun updateMarker(flight: Flight, marker: Marker) {
        // This function updates the position of a moved Airplane
        if(marker.tag != null) {
            marker.position = LatLng(flight.latitude!!.toDouble(), flight.longitude!!.toDouble())
            marker.snippet = flightInfo(flight)
            marker.setIcon(activity?.let {
                bitmapDescriptorFromVector(it, R.drawable.flight_icon)
            })
        }
        if(marker.isInfoWindowShown) {
            // We hide and show the InfowWindow if it is already opened
            // So that the information is updated
            marker.hideInfoWindow()
            marker.showInfoWindow()
        }
    }


    private fun setUpObservers() {
        // Observes user-location
        sharedViewModel.getUserLocation().observe(this, Observer {
            if(!this::userMarker.isInitialized) {
                userMarker = MarkerOptions().position(it!!).title("Your location")
                mMap.addMarker(userMarker)
            }
            mMap.addMarker(userMarker.position(it))
            Log.d("UserLocation", "gotten from VM")
        })


        // Observes LiveData for Airplanes
        try {
            myViewModel.getFlightLiveData().observe(this, Observer {
                if(it == null) {
                    Log.d("Error", "OpenSkyAPI")
                    // This happens quite frequently, so we will simply do nothing
                }
                else placeFlights(it.toMutableList())
                Log.d("Observer", "Changes noticed, MapsFragment")
                CoroutineScope(Dispatchers.IO).launch {
                    updateLiveData()
                }
            })
        }
        catch(e: InterruptedException) {
            Log.d("Interrupted LivedData","Error")
        }


        // Observes LiveData for Airports
        sharedViewModel.getAirportLiveData().observe(this, Observer {
            placeAirports(it.toMutableList())
            Log.d("Observer", "Changes noticed, MapsFragment: sharedViewModel")
        })
    }

    private fun placeAirports(airports: MutableList<Airport>) {
        for (airport in airports) {
            val latitude: Double = airport.lat
            val longitude: Double = airport.lon
            val pos = LatLng(latitude, longitude)

            // Markers for airports
            val airplaneMark = mMap.addMarker(MarkerOptions().position(pos).flat(true).icon(
                    activity?.let {
                        bitmapDescriptorFromVector(it, R.drawable.alternative_airport_icon)
                    }).title(airport.name).snippet(airportInfo(airport))
            )
            airplaneMark.tag = "Airport"
            airportMarkers.add(airplaneMark)
            // Loading the image window
            airplaneMark.showInfoWindow()
            airplaneMark.hideInfoWindow()
        }
    }



    private fun flightInfo(flight: Flight) : String {
        var str = ""

        if (flight.geo_altitude != null) str += "Geometric Height: ${flight.geo_altitude} m"
        str += if (flight.velocity != "null") "\nVelocity: ${String.format("%.1f", (flight.velocity?.toDouble()?.times(3.6)))} km/t" else "\n"
        str += if (flight.vertical_rate != "null") "\nVertical velocity: ${String.format("%.1f", (flight.vertical_rate?.toDouble()?.times(3.6)))} km/t" else "\n"
        str += if (flight.origin_country != null) "\nReg in country: ${flight.origin_country}" else "\n"
        str += if (flight.latitude != null && flight.longitude != null) "\nLatitude, Longitude: ${flight.latitude}, ${flight.longitude}" else "\n"

        return str
    }

    private fun airportInfo(airport: Airport) : String {
        var str = ""
        val ob = airport.weatherObject
        str += airport.name
        str +=  "\n${airport.imgURL}"
        str += if (ob.airTemp != null) "\n${ob.airTemp}" else "\n"
        str += if (ob.weatherIcon != null) "\n${ob.weatherIcon}" else "\n"
        str += if (ob.windSpeed != null) "\n${ob.windSpeed}" else "\n"
        str += if (ob.windFromDirection != null) "\n${ob.windFromDirection}" else "\n"
        if (ob.precipitationRate != null) str += "\n${ob.precipitationRate}"
        return str
    }


    private fun isConnected(): Boolean {
        // Gives us connectivity-status
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =  connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities.also {
            if (it != null){
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return true
                else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true
                }
            }
        }
        return false
    }

    // Gives every plane a plane icon
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }



    suspend fun updateLiveData() {
        if(InternetStatus.getConnection() == true) {
            myViewModel.setFlightLiveData()
        }
    }


}