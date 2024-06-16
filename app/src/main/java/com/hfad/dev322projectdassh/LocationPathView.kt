package com.hfad.dev322projectdassh

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.osmdroid.util.GeoPoint

class LocationPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Functionality developed by Shalom Tsegay
    // Choose the color and stroke width of the lines to create the route
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 8f
    }

    // List of all logged locations from the API
    var locations: List<GeoPoint> = emptyList()
        set(value) {
            field = value
            invalidate() // Redraw the view when the locations are updated
        }

    // Function to control the drawing of the route.
    // Start with an empty page, draw red dots for each logged location, then connect
    // them using a red line
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (locations.isNotEmpty()) {
            val minLatitude = locations.minOf { it.latitude }
            val maxLatitude = locations.maxOf { it.latitude }
            val minLongitude = locations.minOf { it.longitude }
            val maxLongitude = locations.maxOf { it.longitude }

            val latitudeRange = maxLatitude - minLatitude
            val longitudeRange = maxLongitude - minLongitude

            // Draw the red dots
            locations.forEach { location ->
                val x = ((location.longitude - minLongitude) / longitudeRange * width).toFloat()
                val y = ((maxLatitude - location.latitude) / latitudeRange * height).toFloat()
                canvas.drawCircle(x, y, 10f, paint)
                Log.d("LocationPathView", "Drawing circle at x: $x, y: $y")
            }

            // Draw the red line connecting the dots
            for (i in 0 until locations.size - 1) {
                val startX = ((locations[i].longitude - minLongitude) / longitudeRange * width).toFloat()
                val startY = ((maxLatitude - locations[i].latitude) / latitudeRange * height).toFloat()
                val stopX = ((locations[i + 1].longitude - minLongitude) / longitudeRange * width).toFloat()
                val stopY = ((maxLatitude - locations[i + 1].latitude) / latitudeRange * height).toFloat()
                canvas.drawLine(startX, startY, stopX, stopY, paint)
                Log.d("LocationPathView", "Drawing line from x: $startX, y: $startY to x: $stopX, y: $stopY")
            }
        }
    }
}