package com.example.flyapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.flyapp.InternetStatus
import com.example.flyapp.R
import com.example.flyapp.adapter.AirportAdapter
import com.example.flyapp.models.calculateDistance
import com.example.flyapp.viewmodel.SharedViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AirportFragment(sharedViewModel: SharedViewModel) : Fragment() {

    private val myViewModel = sharedViewModel
    private lateinit var spinner: Spinner
    private var userLocation: LatLng? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var gpsEnabled = false
    private lateinit var airportRecyclerView: RecyclerView
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_airport, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)

        // Starts the progressbar, and shows it during API-call
        // progress = view.findViewById(R.id.progress_bar)
        spinner = view.findViewById(R.id.sorting_spinner)
        populateSpinner(spinner); setSpinnerListener(spinner)

        // We find the recyclerview
        airportRecyclerView = view.findViewById(R.id.airport_RecyclerView)
        // Attach a LayoutManager
        airportRecyclerView.layoutManager = LinearLayoutManager(context)
        // Set the refresh-action for our SwipeRefreshLayout
        setSwipeRefreshAction()

        setObservers()

        return view
    }

    private fun setSwipeRefreshAction() {
        Log.i("swipe internet:", InternetStatus.getConnection().toString())
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("Action", "onRefresh called from SwipeRefreshLayout")

            if (InternetStatus.getConnection() == true) {
                // Updates the RecyclerView
                CoroutineScope(Dispatchers.IO).launch {
                    updateLiveData()
                }
            } else {
                Log.i("swipe internet:", "in else statement")
                Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setObservers() {
        // We observe changes to our LiveData of Airport-objects, located in AirportViewModel
        myViewModel.getAirportLiveData().observe(viewLifecycleOwner, Observer {
            swipeRefreshLayout.isRefreshing = false
            if(userLocation != null){
                calculateDistance(it.toMutableList(), userLocation!!.latitude, userLocation!!.longitude)
            }
            // And when a change occurs, we simply update the adapter with the new data
            airportRecyclerView.adapter = AirportAdapter(it.toMutableList(), gpsEnabled)
            Log.d("Action:", "Change noticed by observer, and recyclerview updated")
        })

        myViewModel.getUserLocation().observe(viewLifecycleOwner, Observer {
            Log.d("UserLocation", "noticed by observer in AirportFragment")
            if(userLocation == null) {
                userLocation = LatLng(it.latitude, it.longitude)
                myViewModel.updateWithGpsInfo()
                gpsEnabled = true
                if(this::spinnerAdapter.isInitialized) {
                    spinnerAdapter.insert("Nearest//Farthest", 3)
                    spinnerAdapter.notifyDataSetChanged()
                }
            }
        })
    }


    private fun populateSpinner(spinner: Spinner) {
        val list = ArrayList<String>()
        list.add("Most visited"); list.add("Hottest"); list.add("Coldest")
        if(gpsEnabled) list.add("Nearest//Farthest")
        spinnerAdapter = ArrayAdapter(this.requireContext(), R.layout.spinner_list_layout, list)
        spinnerAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Setting the background!
        val mCon = spinner.context
        val resID: Int = mCon.resources.getIdentifier(
            "spinner_style_corner" , "drawable", mCon.packageName)

        spinner.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(
            requireContext().resources, resID, requireContext().theme))
    }

    private fun setSpinnerListener(spinner: Spinner) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                myViewModel.sortAirports(spinner.selectedItem.toString(), userLocation)
            }
        }
    }

    suspend fun updateLiveData() {
        try {
            myViewModel.setAirportLiveData(spinner.selectedItem.toString())
        } catch(e: Exception) {
            Handler(Looper.getMainLooper()).post { // This is where your UI code goes.
                Toast.makeText(context,"Not connected to internet",Toast.LENGTH_SHORT).show()
            }
        }
    }


}