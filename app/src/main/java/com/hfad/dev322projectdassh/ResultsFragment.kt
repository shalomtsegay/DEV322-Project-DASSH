package com.hfad.dev322projectdassh

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration // osmdroid Stuff
import org.osmdroid.util.GeoPoint // osmdroid Stuff
import org.osmdroid.views.MapView // osmdroid Stuff
import org.osmdroid.views.overlay.Polyline // osmdroid Stuff

class ResultsFragment : Fragment() {

    private lateinit var map: MapView // osmdroid Stuff
    private var locationList: ArrayList<GeoPoint> = ArrayList() // osmdroid Stuff

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)

        // Get location data from arguments or savedInstanceState
        locationList = arguments?.getParcelableArrayList("locations") ?: ArrayList() // osmdroid Stuff

        map = view.findViewById(
            R.id.map) // osmdroid Stuff
        Configuration.getInstance().load(context, null) // osmdroid Stuff

        map.setMultiTouchControls(true) // osmdroid Stuff
        val mapController: IMapController = map.controller // osmdroid Stuff
        if (locationList.isNotEmpty()) {
            mapController.setZoom(15.0) // osmdroid Stuff
            mapController.setCenter(locationList[0]) // osmdroid Stuff
            val polyline = Polyline() // osmdroid Stuff
            polyline.setPoints(locationList) // osmdroid Stuff
            map.overlays.add(polyline) // osmdroid Stuff
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // Needed for compass, my location overlays, v6.0.0 and up // osmdroid Stuff
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // Needed for compass, my location overlays, v6.0.0 and up // osmdroid Stuff
    }
}