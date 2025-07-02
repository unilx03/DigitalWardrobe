package com.digitalwardrobe.ui.wardrobe

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WardrobeViewPagerAdapter(@NonNull fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList: MutableList<Fragment> = ArrayList<Fragment>()
    private val fragmentTitleList: MutableList<String> = ArrayList()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    @NonNull
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun getTitle(position: Int): String {
        return fragmentTitleList[position]
    }
}