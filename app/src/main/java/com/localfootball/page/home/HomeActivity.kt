package com.localfootball.page.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.localfootball.R
import com.localfootball.page.events_map.EventsMapFragment
import com.localfootball.page.settings.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private val mapFragment = EventsMapFragment()
//    private val profileFragment = ProfileFragment()
//    private val chatFragment = ChatFragment()
    private val settingsFragment = SettingsFragment()
//    private val teamFragment = TeamFragment()

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.map_events -> {
                supportFragmentManager.beginTransaction().replace(R.id.homePageMainFrameLayout, mapFragment).commit()
            }
//            R.id.player_profile -> {
//                supportFragmentManager.beginTransaction().replace(R.id.homePageMainFrameLayout, profileFragment).commit()
//            }
//            R.id.chat -> {
//                supportFragmentManager.beginTransaction().replace(R.id.homePageMainFrameLayout, chatFragment).commit()
//            }
//            R.id.team -> {
//                supportFragmentManager.beginTransaction().replace(R.id.homePageMainFrameLayout, teamFragment).commit()
//            }
            R.id.settings -> {
                supportFragmentManager.beginTransaction().replace(R.id.homePageMainFrameLayout, settingsFragment).commit()
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        configureBottomNavigationView()
    }

    private fun configureBottomNavigationView(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.homePageBottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = R.id.map_events
    }

}