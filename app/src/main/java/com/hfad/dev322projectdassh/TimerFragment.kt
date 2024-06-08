package com.hfad.dev322projectdassh

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import android.util.Log // API Stuff
import android.os.Handler // API Stuff
import android.os.Looper // API Stuff
import okhttp3.OkHttpClient // API Stuff
import okhttp3.Request // API Stuff
import java.io.IOException // API Stuff
import org.osmdroid.util.GeoPoint // osmdroid Stuff



class TimerFragment : Fragment(), SensorEventListener, LocationListener {
    private lateinit var stopwatch: Chronometer // The chronometer
    private var running = false // Is the chronometer running?
    private var offset: Long = 0 // The base offset for the chronometer

    // Add key Strings for use with the Bundle
    private val OFFSET_KEY = "offset"
    private val RUNNING_KEY = "running"
    private val BASE_KEY = "base"


    //Sensor Stuff
    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var resume = false;

    // GPS Stuff
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2

    // API Stuff
    private var handler: Handler? = null
    private val interval = 5000L // 5 seconds

    private val locationList: MutableList<Location> = mutableListOf() // osmdroid Stuff



    // needed for find view by id
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        Log.d("TimerFragment", "onCreateView called") // Simple log statement for testing


        rootView = view

        // initialize
        tvGpsLocation = view.findViewById(R.id.gpsTextView)

        // Initialize sensor manager and accelerometer
        mSensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager



        // Get a reference to the stopwatch
        stopwatch = view.findViewById(R.id.stopwatch)

        // Restore the previous state
        if (savedInstanceState != null) {
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            if (running) {
                stopwatch.base = savedInstanceState.getLong(BASE_KEY)
                stopwatch.start()
            } else {
                setBaseTime()
            }
        }

        // Start button
        val startButton = view.findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            if (!running) {
                setBaseTime()
                stopwatch.start()
                running = true
                startLocationUpdates()

            }
        }

        // Pause button
        val pauseButton = view.findViewById<Button>(R.id.pause_button)
        pauseButton.setOnClickListener {
            if (running) {
                saveOffset()
                stopwatch.stop()
                running = false
                stopLocationUpdates() // API Stuff

            }
        }

        // Reset button
        val resetButton = view.findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            offset = 0
            setBaseTime()
        }

        // Get location button
        val getLocationButton = view.findViewById<Button>(R.id.getLocation)
        getLocationButton.setOnClickListener {
            getLocation()
        }

        // Finish Run button
        val finishRunButton = view.findViewById<Button>(R.id.finishRunButton)
        finishRunButton.setOnClickListener {
            val bundle = Bundle()
            Log.d("TimerFragment", "Finish Run button clicked. Locations: ${locationList.size}") // Log statement for testing
            val geoPointList = ArrayList(locationList.map { GeoPoint(it.latitude, it.longitude) }) // osmdroid Stuff
            bundle.putParcelableArrayList("locations", geoPointList)

            view.findNavController().navigate(R.id.action_timerFragment_to_resultsFragment, bundle)
        }


        return view
    }

    override fun onPause() {
        super.onPause()
        Log.d("TimerFragment", "onPause called") // Log statement for testing


        mSensorManager.unregisterListener(this)

        if (running) {
            saveOffset()
            stopwatch.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("TimerFragment", "onResume called") // Log statement for testing


        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)

        if (running) {
            setBaseTime()
            stopwatch.start()
            offset = 0
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("TimerFragment", "onSaveInstanceState called") // Log statement for testing
        outState.putLong(OFFSET_KEY, offset)
        outState.putBoolean(RUNNING_KEY, running)
        outState.putLong(BASE_KEY, stopwatch.base)
        super.onSaveInstanceState(outState)
    }

    private fun setBaseTime() {
        stopwatch.base = SystemClock.elapsedRealtime() - offset
    }

    private fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - stopwatch.base
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                //event.values[0]
                //^^^^accelerometer data
                return
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    //GPS Stuff
    override fun onLocationChanged(location: Location) {
        tvGpsLocation.text =
            "Latitude: " + location.latitude + " , Longitude: " + location.longitude
        logLocationToAPI(location) // API Stuff
        locationList.add(location) // osmdroid Stuff


    }

    //GPS Stuff
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                startLocationUpdates() // API Stuff

            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //GPS Stuff
    private fun getLocation() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    private fun startLocationUpdates() { // API Stuff
        Log.d("API", "Starting location updates") // Log statement to verify
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            handler = Handler(Looper.getMainLooper())
            handler?.postDelayed(object : Runnable {
                override fun run() {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestSingleUpdate(
                            LocationManager.GPS_PROVIDER,
                            this@TimerFragment,
                            null
                        )
                    }
                    handler?.postDelayed(this, interval)
                }
            }, interval)
        }
    }

    private fun stopLocationUpdates() { // API Stuff
        Log.d("API", "Stopping location updates") // Log statement to verify
        handler?.removeCallbacksAndMessages(null)
    }

    private fun logLocationToAPI(location: Location) { // API Stuff
        val client = OkHttpClient()
        val url =
            "https://nominatim.openstreetmap.org/reverse?format=json&lat=${location.latitude}&lon=${location.longitude}"
        Log.d("API", "Requesting URL: $url") // Log the request URL

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.e("API", "Request failed: ${e.message}") // Log failure
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("API", "Unexpected code $response")
                } else {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        Log.d("API", "Response data: $responseData") // Log response data
                    }
                }
            }
        })
    }
}