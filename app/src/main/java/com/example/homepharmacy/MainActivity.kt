package com.example.homepharmacy

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.feature_medicine.ui.MedicineFragment
import com.example.feature_medicine.ui.MedicineFragmentDirections
import com.example.homepharmacy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        binding = ActivityMainBinding.bind(layoutInflater.inflate(R.layout.activity_main, viewGroup))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_activity_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController //getting navController for navigation in the ap

        val mainBottomNavigator = binding.mainActivityBottomNavigation
        mainBottomNavigator.itemIconTintList = null // setting icon color to null, because otherwise they would whole
        NavigationUI.setupWithNavController(mainBottomNavigator, navController) // connecting bottomNavigationView to navController for it's auto update
    }
}