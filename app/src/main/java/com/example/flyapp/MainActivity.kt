package com.example.flyapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flyapp.view.AirportFragment
import com.example.flyapp.view.MapsFragment
import com.example.flyapp.viewmodel.SharedViewModel
import com.example.flyapp.viewmodel.SharedViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var airport: AirportFragment
    private lateinit var maps: MapsFragment
    private lateinit var floatingRefreshButton: FloatingActionButton
    private lateinit var navigation: BottomNavigationView
    private lateinit var frameLayout: FrameLayout
    private lateinit var splashScreen: LinearLayout
    private lateinit var loadingText: TextView
    private lateinit var floatingRefreshBar: ProgressBar
    private lateinit var sharedViewModel: SharedViewModel
    private var hasStarted = false
    private var reconnected = false

    // This is basically an observer which is notified when there is a change in connectivity.
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }
    // Set the default screen without loading anything that requires internet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        setContentView(R.layout.activity_main)
        floatingRefreshButton = findViewById(R.id.fab_refresh)
        floatingRefreshBar = findViewById(R.id.fab_refresh_refresh)
        navigation = findViewById(R.id.navigation)
        splashScreen = findViewById(R.id.splash_screen)
        frameLayout = findViewById(R.id.fragmentContainer)
        loadingText = findViewById(R.id.loading_text)
        splashScreen.visibility = View.VISIBLE

        // Only start the app if there is connection, otherwise we wait until connection is reestablished.
        // we handle reconnection in connected() function.
        if (isNetworkAvailable()) {
            requestPermissions()
        } else {
            Log.d("Connection", "onCreate: Not connected to internet")
            loadingText.text = getString(R.string.not_connected_message)
        }
    }

    // Here is where we put code that relies on internet. This is called when we have connection on start, and also when we resume connection.
    private fun startApp() {
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(this.applicationContext)
        ).get(SharedViewModel::class.java)
        maps = MapsFragment(sharedViewModel)
        airport = AirportFragment(sharedViewModel)
        sharedViewModel.getAirportLiveData().observe(this, Observer {
            // And when a change occurs, we simply update the adapter with the new data
            splashScreen.visibility = View.GONE
            navigation.visibility = View.VISIBLE
            frameLayout.visibility = View.VISIBLE

            Log.d("Action:", "Change noticed by observer in MainActivity (Loading screen).")
        })
        loadFragment(airport)
        setOnClickListener("Airports")
        navigation.setOnNavigationItemSelectedListener(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
    // This swaps out the current fragment with a new fragment
    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment).commit()

            return true
        }
        return false
    }

    // Switch to the fragment which is for the button pressed
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                setOnClickListener("Airports")
                return loadFragment(airport)
            }
            R.id.nav_map -> {
                setOnClickListener("Map")
                return loadFragment(maps)
            }
        }
        return loadFragment(maps)
    }

    private fun setOnClickListener(place: String) {
        Log.d("Navigation:", "To $place")
        floatingRefreshButton.setOnClickListener {
            if(InternetStatus.getConnection()==true) {
                Log.d("Refresh called:", "By $place")
                floatingRefreshButton.visibility = View.GONE
                floatingRefreshBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    floatingRefreshButton.isClickable = false
                    when (place) {
                        "Map" -> maps.updateLiveData()
                        "Airports" -> airport.updateLiveData()
                    }
                    runOnUiThread {
                        floatingRefreshButton.isClickable = true
                        floatingRefreshButton.visibility = View.VISIBLE
                        floatingRefreshBar.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(applicationContext,"Not connected to internet",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun requestPermissions() {
        if(ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 99)
            Log.d("Request", "Location access")
        }
        else {
            Log.d("Already granted", "Access to location")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()) {
            Log.d("Result", "RequestPermission")
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Result Location", "Granted")
            } else Log.d("No permission", "ACCESS_FINE_LOCATION")
            startApp()
        }
    }
    // Internet functions below
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
    // display the disconnected image when we lose connection
    private fun disconnected() {
        Toast.makeText(this, "Internet connection lost", Toast.LENGTH_SHORT).show()
        InternetStatus.setConnection(false)
        reconnected = false
        Log.d("Internet status", InternetStatus.getConnection().toString())
    }
    // run code when we regain connection
    private fun connected() {
        // loadingText.text = ""
        InternetStatus.setConnection(true)
        // if first time starting app, and we just reconnected, then start the app.
        if(!hasStarted) {
            startApp()
            hasStarted = true
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        }
        else {
            if(!reconnected) {
                Toast.makeText(this, "Reconnected to internet", Toast.LENGTH_SHORT).show()
                reconnected = true
            }
        }
        Log.d("Internet status", InternetStatus.getConnection().toString())
    }
}

class InternetStatus {
    companion object {
        private var isConnected: Boolean? = false
        fun setConnection(value: Boolean) {
            isConnected = value
        }
        fun getConnection(): Boolean? {
            return isConnected
        }
    }
}


