package com.digitalwardrobe.ui.stats

import android.graphics.Color
import com.digitalwardrobe.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.data.DailyOutfitViewModel
import com.digitalwardrobe.data.DailyOutfitViewModelFactory
import com.digitalwardrobe.data.OutfitAdapter
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OutfitStatsFragment : Fragment(){

    private lateinit var dailyOutfitViewModel : DailyOutfitViewModel
    private lateinit var outfitViewModel : OutfitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outfit_stats_fragment, container, false)
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

        totalValues()
        outfitsYearGraph()
        outfitsUsage()
    }

    fun totalValues()
    {
        val totalOutfits = view?.findViewById<MaterialTextView>(R.id.totalOutfits)
        val totalDailyOutfits = view?.findViewById<MaterialTextView>(R.id.totalDailyOutfits)

        lifecycleScope.launch{
            val allOutfits = outfitViewModel.getAllOutfits()
            totalOutfits?.text = allOutfits.size.toString()

            val allDailyOutfits = dailyOutfitViewModel.getAllDailyOutfits()
            totalDailyOutfits?.text = allDailyOutfits.size.toString()
        }
    }

    fun outfitsYearGraph() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val lineChart = view?.findViewById<LineChart>(R.id.outfitsYearChart)

        lifecycleScope.launch {
            val allDailyOutfits = dailyOutfitViewModel.getAllDailyOutfits()
            val outfitsPerMonth = IntArray(12) { 0 }

            for (outfit in allDailyOutfits) {
                val itemDate = dateFormat.parse(outfit.date) ?: continue
                calendar.time = itemDate
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)

                if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                    outfitsPerMonth[month]++
                }
            }

            val entries = outfitsPerMonth.mapIndexed { index, count ->
                Entry(index.toFloat(), count.toFloat())
            }

            val dataSet = LineDataSet(entries, "Daily outfits set per Month").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color.BLUE)
            }

            lineChart?.data = LineData(dataSet)
            lineChart?.invalidate()
        }
    }

    fun outfitsUsage(n: Int = 4) {
        val topOutfitsRecyclerView = view?.findViewById<RecyclerView>(R.id.topOutfitsRecyclerView)
        topOutfitsRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)

        val topOutfitsAdapter = OutfitAdapter(mutableListOf())
        topOutfitsRecyclerView?.adapter = topOutfitsAdapter

        lifecycleScope.launch {
            val allDailyOutfits = dailyOutfitViewModel.getAllDailyOutfits()
            val usageMap = allDailyOutfits
                .groupingBy { it.outfitId }
                .eachCount()

            val descendingUsageList = usageMap.entries
                .sortedByDescending { it.value }

            val topOutfitIds = descendingUsageList
                .take(n)
                .map { it.key }

            val allOutfits = outfitViewModel.getAllOutfits()
            val outfitsById = allOutfits.associateBy { it.id }

            val topOutfits = descendingUsageList
                .filter { it.key in topOutfitIds }
                .mapNotNull { outfitsById[it.key] }

            topOutfitsAdapter.updateData(topOutfits)
        }
    }
}