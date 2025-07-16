package com.digitalwardrobe.ui.dressing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.digitalwardrobe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DressingMain : Fragment() {
    private var selectedMenuId : Int = R.id.dressing_planner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dressing_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set initial fragment only if savedInstanceState is null (first load)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, DressingPlannerFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            selectedMenuId = menuItem.itemId

            val selectedFragment = when (menuItem.itemId) {
                R.id.dressing_planner -> DressingPlannerFragment()
                R.id.dressing_moodboard -> DressingMoodboardFragment()
                else -> DressingPlannerFragment()
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