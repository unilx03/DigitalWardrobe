package com.digitalwardrobe.ui.dressing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DressingMain : Fragment() {
    private var selectedMenuId : Int = R.id.dressing_calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dressing_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, DressingCalendarFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            selectedMenuId = menuItem.itemId

            val selectedFragment = when (menuItem.itemId) {
                R.id.dressing_calendar -> DressingCalendarFragment()
                R.id.dressing_moodboard -> DressingMoodboardFragment()
                else -> DressingCalendarFragment()
            }

            /*val currentFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            if (currentFragment is SaveableFragment) {
                currentFragment.saveState()
            }*/

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