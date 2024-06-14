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


        // Stephen Orendains contributions of Bottom Navigation Bar
        // Get a reference to the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavView.setupWithNavController(navController)

        // Handle bottom navigation to be able to navigate to different fragments after navigating through buttons - Stephen Orendain
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.welcomeFragment -> {
                    // Navigate to WelcomeFragment - Stephen Orendain
                    if (navController.currentDestination?.id != R.id.welcomeFragment) {
                        navController.navigate(R.id.welcomeFragment)
                    }
                    true
                }
                R.id.timerFragment -> {
                    // Navigate to timer fragment - Stephen Orendain
                    if (navController.currentDestination?.id != R.id.timerFragment) {
                        navController.navigate(R.id.timerFragment)
                    }
                    true
                }
                R.id.resultsFragment -> {
                    // Navigate to Results fragment - Stephen Orendain
                    if (navController.currentDestination?.id != R.id.resultsFragment) {
                        navController.navigate(R.id.resultsFragment)
                    }
                    true // Indicate that the item click has been handled
                }
                else -> false // Return false for other items
            }
        }

        // Stephen Orendain's Contributions of Bottom Navigation Bar End
    }
}