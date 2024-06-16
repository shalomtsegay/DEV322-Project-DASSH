package com.hfad.dev322projectdassh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.util.GeoPoint

class ResultsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Shalom Tsegays Contribution for Results Fragment - Setting up fragment, implementing API to get location data/updates
        // Using locations from API to draw final route on to the results fragment
        val view = inflater.inflate(R.layout.fragment_results, container, false)

        val locationPathView = view.findViewById<LocationPathView>(R.id.locationPathView)

        val geoPointList = arguments?.getParcelableArrayList<GeoPoint>("locations") ?: arrayListOf()
        locationPathView.locations = geoPointList

        return view
    }
}