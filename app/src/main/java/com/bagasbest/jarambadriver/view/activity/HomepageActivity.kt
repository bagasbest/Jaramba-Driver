package com.bagasbest.jarambadriver.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.ActivityHomepageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompatActivity() {

    private var binding: ActivityHomepageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}