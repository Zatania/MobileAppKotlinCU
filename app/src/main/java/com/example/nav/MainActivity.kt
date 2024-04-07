package com.example.nav

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nav.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up the BottomNavigationView with NavController
        navView.setupWithNavController(navController)

        // Optional: If you want to listen to item reselection
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        Log.d("HomeFragment", "Home selected")
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_lesson -> {
                    Log.d("LessonFragment", "Lesson selected")
                    navController.navigate(R.id.navigation_lesson)
                    true
                }
                R.id.navigation_progress -> {
                    Log.d("ProgressFragment", "Progress selected")
                    navController.navigate(R.id.navigation_progress)
                    true
                }
                R.id.navigation_profile -> {
                    Log.d("ProfileFragment", "Profile selected")
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                R.id.navigation_quiz -> {
                    Log.d("QuizFragment", "Quiz selected")
                    navController.navigate(R.id.navigation_quiz)
                    true
                }
                else -> false
            }
        }
    }
}