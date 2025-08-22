package com.digitalwardrobe.ui.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class WardrobeMain : Fragment() {
    private var selectedMenuId : Int = R.id.wardrobe_wearables

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.wardrobe_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, WardrobeItemsFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            selectedMenuId = menuItem.itemId

            val selectedFragment = when (menuItem.itemId) {
                R.id.wardrobe_wearables -> WardrobeItemsFragment()
                R.id.wardrobe_outfits -> WardrobeOutfitsFragment()
                else -> WardrobeItemsFragment()
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