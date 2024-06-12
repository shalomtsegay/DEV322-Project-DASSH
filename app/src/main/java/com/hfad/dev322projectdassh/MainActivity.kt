package com.hfad.dev322projectdassh
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get a reference to the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavView.setupWithNavController(navController)

        // Handle click on the bottom navigation item corresponding to the WelcomeFragment
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.welcomeFragment -> {
                    // Navigate to WelcomeFragment
                    if (navController.currentDestination?.id != R.id.welcomeFragment) {
                        navController.navigate(R.id.welcomeFragment)
                    }
                    true // Indicate that the item click has been handled
                }
                R.id.timerFragment -> {
                    // Navigate to timer fragment
                    if (navController.currentDestination?.id != R.id.timerFragment) {
                        navController.navigate(R.id.timerFragment)
                    }
                    true // Indicate that the item click has been handled
                }
                R.id.resultsFragment -> {
                    // Navigate to Results fragment
                    if (navController.currentDestination?.id != R.id.resultsFragment) {
                        navController.navigate(R.id.resultsFragment)
                    }
                    true // Indicate that the item click has been handled
                }
                else -> false // Return false for other items
            }
        }
    }
}