package com.digitalwardrobe.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class StatsMain : Fragment() {
    private var selectedMenuId : Int = R.id.stats_wearables

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.stats_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, WearableStatsFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            selectedMenuId = menuItem.itemId

            val selectedFragment = when (menuItem.itemId) {
                R.id.stats_wearables -> WearableStatsFragment()
                R.id.stats_outfits -> OutfitStatsFragment()
                else -> WearableStatsFragment()
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, selectedFragment)
                .commit()

            true
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = requireView().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = selectedMenuId
    }
}