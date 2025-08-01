package com.digitalwardrobe.ui.stats

import android.graphics.Color
import com.digitalwardrobe.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.mutableIntListOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digitalwardrobe.data.DailyOutfitViewModel
import com.digitalwardrobe.data.DailyOutfitViewModelFactory
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsFragment : Fragment(){

    private lateinit var dailyOutfitViewModel : DailyOutfitViewModel
    private lateinit var outfitViewModel : OutfitViewModel
    private lateinit var wearableViewModel : WearableViewModel
    private lateinit var outfitWearableViewModel : OutfitWearableViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dailyOutfitViewModel = ViewModelProvider(
            requireActivity(),
            DailyOutfitViewModelFactory(requireActivity().application)
        )[DailyOutfitViewModel::class.java]

        outfitViewModel = ViewModelProvider(
            requireActivity(),
            OutfitViewModelFactory(requireActivity().application)
        )[OutfitViewModel::class.java]

        wearableViewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        outfitWearableViewModel = ViewModelProvider(
            requireActivity(),
            OutfitWearableViewModelFactory(requireActivity().application)
        )[OutfitWearableViewModel::class.java]

        wearablesAddedLastMonth()
        outfitsAddedLastMonth()

        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        // 1. Create test data (X, Y points)
        val lineEntries = listOf(
            Entry(0f, 20f),
            Entry(1f, 24f),
            Entry(2f, 22f),
            Entry(3f, 27f),
            Entry(4f, 23f)
        )

        // 2. Create a dataset
        val lineDataSet = LineDataSet(lineEntries, "Test Data").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.BLUE)
        }

        // 3. Set up LineData and apply to chart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart
    }

    fun wearablesAddedLastMonth(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val wearablesLastMonth = view?.findViewById<MaterialTextView>(R.id.wearablesLastMonth)
        lifecycleScope.launch{
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfLastMonth = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfLastMonth = calendar.time

            // Filter items added last month
            val allWearables = wearableViewModel.getAllWearables()
            val wearablesAddedLastMonth = allWearables.filter {
                try {
                    val itemDate = dateFormat.parse(it.addDate)
                    itemDate != null && itemDate >= startOfLastMonth && itemDate <= endOfLastMonth
                } catch (e: Exception) {
                    false
                }
            }

            // Display the count
            wearablesLastMonth?.text = wearablesAddedLastMonth.size.toString()
        }
    }

    fun outfitsAddedLastMonth(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val wearablesLastMonth = view?.findViewById<MaterialTextView>(R.id.outfitsLastMonth)
        lifecycleScope.launch{
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfLastMonth = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfLastMonth = calendar.time

            // Filter items added last month
            val allWearables = wearableViewModel.getAllWearables()
            val wearablesAddedLastMonth = allWearables.filter {
                try {
                    val itemDate = dateFormat.parse(it.addDate)
                    itemDate != null && itemDate >= startOfLastMonth && itemDate <= endOfLastMonth
                } catch (e: Exception) {
                    false
                }
            }

            // Display the count
            wearablesLastMonth?.text = wearablesAddedLastMonth.size.toString()
        }
    }

    /*fun mostUsedWearables(topN: Int = 5) {
        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()
            val sortedWearables = allWearables.sortedByDescending { it.usageCount }
            val topWearables = sortedWearables.take(topN)

            // TODO: Display or process `topWearables` as needed
            // Example: Log or update UI
            // Log.d("Stats", "Top Wearables: $topWearables")
        }
    }

    fun leastUsedWearables(topN: Int = 5) {
        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()
            val sortedWearables = allWearables.sortedBy { it.usageCount }
            val bottomWearables = sortedWearables.take(topN)

            // TODO: Display or process `bottomWearables`
        }
    }

    fun mostUsedOutfits(topN: Int = 5) {
        lifecycleScope.launch {
            val allOutfits = outfitViewModel.getAllOutfits()
            val sortedOutfits = allOutfits.sortedByDescending { it.usageCount }
            val topOutfits = sortedOutfits.take(topN)

            // TODO: Display or process `topOutfits`
        }
    }

    fun mostUsedColors(topN: Int = 5) {
        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()
            val colorCount = allWearables.groupingBy { it.color }.eachCount()
            val topColors = colorCount.entries.sortedByDescending { it.value }.take(topN)

            // TODO: Use topColors for UI or further processing
        }
    }

    fun mostUsedTags(topN: Int = 5) {
        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()
            val allTags = allWearables.flatMap { it.tags }
            val tagCount = allTags.groupingBy { it }.eachCount()
            val topTags = tagCount.entries.sortedByDescending { it.value }.take(topN)

            // TODO: Use topTags
        }
    }

    fun mostUsedBrand(topN: Int = 5) {
        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()
            val brandCount = allWearables.groupingBy { it.brand }.eachCount()
            val topBrands = brandCount.entries.sortedByDescending { it.value }.take(topN)

            // TODO: Use topBrands
        }
    }

    fun sumPriceLastMonth() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        lifecycleScope.launch {
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfLastMonth = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfLastMonth = calendar.time

            val allWearables = wearableViewModel.getAllWearables()
            val sumPrice = allWearables.filter {
                try {
                    val itemDate = dateFormat.parse(it.addDate)
                    itemDate != null && itemDate >= startOfLastMonth && itemDate <= endOfLastMonth
                } catch (e: Exception) {
                    false
                }
            }.sumOf { it.price ?: 0.0 }

            // TODO: Display sumPrice in UI
        }
    }*/
}