package com.digitalwardrobe.ui.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.digitalwardrobe.R

class WardrobeTabFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.wardrobe_tab_layout, container, false)

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        val adapter: WardrobeViewPagerAdapter = WardrobeViewPagerAdapter(this)
        adapter.addFragment(WardrobeItemsFragment(), "Wearables")
        adapter.addFragment(WardrobeOutfitsFragment(), "Outfits")

        viewPager.adapter = adapter

        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.setText(adapter.getTitle(position)) }.attach()

        return view
    }
}