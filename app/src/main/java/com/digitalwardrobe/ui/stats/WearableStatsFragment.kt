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
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WearableStatsFragment : Fragment(){

    private lateinit var wearableViewModel : WearableViewModel
    private lateinit var outfitWearableViewModel : OutfitWearableViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wearable_stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wearableViewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        outfitWearableViewModel = ViewModelProvider(
            requireActivity(),
            OutfitWearableViewModelFactory(requireActivity().application)
        )[OutfitWearableViewModel::class.java]

        totalValues()
        wearablesCategoryGraph()
        wearablesYearGraph()
        sumPricesYearGraph()
        wearablesUsage()
        mostUsedWearableAttributesGraph()
    }

    fun totalValues()
    {
        val totalWearables = view?.findViewById<MaterialTextView>(R.id.totalWearables)

        lifecycleScope.launch{
            val allWearables = wearableViewModel.getAllWearables()
            totalWearables?.text = allWearables.size.toString()
        }
    }

    fun wearablesYearGraph() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val lineChart = view?.findViewById<LineChart>(R.id.wearablesYearChart)

        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()

            // Map for holding month index (0–11)
            val wearablesPerMonth = IntArray(12) { 0 }

            for (wearable in allWearables) {
                try {
                    val itemDate = dateFormat.parse(wearable.addDate) ?: continue
                    calendar.time = itemDate
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) // 0 = Jan, 11 = Dec

                    if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                        wearablesPerMonth[month]++
                    }
                } catch (_: Exception) {
                    // Ignore invalid dates
                }
            }

            val entries = wearablesPerMonth.mapIndexed { index, count ->
                Entry(index.toFloat(), count.toFloat())
            }

            val dataSet = LineDataSet(entries, "Wearables added per Month").apply {
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

    fun wearablesCategoryGraph(){
        val barChart = view?.findViewById<BarChart>(R.id.wearableCategoryChart)

        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()

            val categoryCounts = allWearables
                .groupingBy { it.category }
                .eachCount()
                .toList()

            val entries = categoryCounts.mapIndexed { index, (_, count) ->
                BarEntry(index.toFloat(), count.toFloat())
            }

            val dataSet = BarDataSet(entries, "Wearables per Category").apply {
                setColors(ColorTemplate.MATERIAL_COLORS.toList())
                valueTextColor = Color.BLACK
            }

            barChart?.apply {
                data = BarData(dataSet)
                xAxis.valueFormatter = IndexAxisValueFormatter(categoryCounts.map { it.first })
                xAxis.granularity = 1f
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textSize = 11f
                xAxis.labelRotationAngle = -45f

                axisLeft.textColor = Color.DKGRAY
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.textColor = Color.BLACK

                animateX(800)
                invalidate()
            }
            barChart?.setExtraOffsets(0f, 0f, 0f, 40f)
        }
    }

    fun sumPricesYearGraph(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val lineChart = view?.findViewById<LineChart>(R.id.sumPricesYearChart)

        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()

            // Initialize array to hold prices per month (Jan = 0, Dec = 11)
            val sumPricesPerMonth = DoubleArray(12) { 0.0 }

            for (wearable in allWearables) {
                try {
                    val itemDate = dateFormat.parse(wearable.addDate) ?: continue
                    calendar.time = itemDate

                    val itemYear = calendar.get(Calendar.YEAR)
                    val itemMonth = calendar.get(Calendar.MONTH)

                    if (itemYear == Calendar.getInstance().get(Calendar.YEAR)) {
                        sumPricesPerMonth[itemMonth] += wearable.price
                    }
                } catch (_: Exception) {
                    // Ignore parsing errors
                }
            }

            val entries = sumPricesPerMonth.mapIndexed { index, price ->
                Entry(index.toFloat(), price.toFloat())
            }

            val dataSet = LineDataSet(entries, "Budget spent per month").apply {
                color = Color.RED
                valueTextColor = Color.BLACK
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color.RED)
            }

            lineChart?.data = LineData(dataSet)
            lineChart?.invalidate()
        }
    }

    fun wearablesUsage(n: Int = 4) {
        val topWearablesRecyclerView = view?.findViewById<RecyclerView>(R.id.topWearablesRecyclerView)
        topWearablesRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)

        val topWearablesAdapter = WearableAdapter(mutableListOf())
        topWearablesRecyclerView?.adapter = topWearablesAdapter

        val bottomWearablesRecyclerView = view?.findViewById<RecyclerView>(R.id.bottomWearablesRecyclerView)
        bottomWearablesRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)

        val bottomWearablesAdapter = WearableAdapter(mutableListOf())
        bottomWearablesRecyclerView?.adapter = bottomWearablesAdapter

        lifecycleScope.launch {
            val allOutfitWearables = outfitWearableViewModel.getAllOutfitWearables()
            val usageMap = allOutfitWearables
                .groupingBy { it.wearableId }
                .eachCount()

            val descendingUsageList = usageMap.entries
                .sortedByDescending { it.value }

            val topWearableIds = descendingUsageList
                .take(n)
                .map { it.key }

            val ascendingUsageList  = usageMap.entries
                .sortedBy { it.value }

            val bottomWearableIds = ascendingUsageList
                .take(n)
                .map { it.key }

            val allWearables = wearableViewModel.getAllWearables()
            val wearablesById = allWearables.associateBy { it.id }

            val topWearables = descendingUsageList
                .filter { it.key in topWearableIds }
                .mapNotNull { wearablesById[it.key] }

            val bottomWearables = ascendingUsageList
                .filter { it.key in bottomWearableIds }
                .mapNotNull { wearablesById[it.key] }

            topWearablesAdapter.updateData(topWearables)
            bottomWearablesAdapter.updateData(bottomWearables)
        }
    }

    fun mostUsedWearableAttributesGraph(n: Int = 8){
        val colorsChart = view?.findViewById<HorizontalBarChart>(R.id.topColorsChart) ?: return
        val tagsChart = view?.findViewById<HorizontalBarChart>(R.id.topTagsChart) ?: return
        val brandsChart = view?.findViewById<HorizontalBarChart>(R.id.topBrandChart) ?: return

        lifecycleScope.launch {
            val allWearables = wearableViewModel.getAllWearables()

            // COLORS
            val colorFrequencyMap = mutableMapOf<String, Int>()
            allWearables.forEach { wearable ->
                wearable.colors
                    ?.split(",")
                    ?.map { it.trim().lowercase() }
                    ?.filter { it.isNotEmpty() }
                    ?.forEach { color ->
                        colorFrequencyMap[color] = colorFrequencyMap.getOrDefault(color, 0) + 1
                    }
            }

            val topColors = colorFrequencyMap.entries
                .sortedByDescending { it.value }
                .take(n)

            val colorEntries = topColors.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat())
            }

            val colorDataSet = BarDataSet(colorEntries, "Top $n Colors").apply {
                setColors(ColorTemplate.MATERIAL_COLORS.toList())
                valueTextColor = Color.BLACK
            }

            colorsChart.data = BarData(colorDataSet)
            colorsChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(topColors.map { it.key })
                granularity = 1f
                labelRotationAngle = 45f
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }
            colorsChart.setExtraOffsets(20f, 0f, 0f, 0f)
            colorsChart.invalidate()

            // TAGS
            val tagsFrequencyMap = mutableMapOf<String, Int>()
            allWearables.forEach { wearable ->
                wearable.tags
                    ?.split(",")
                    ?.map { it.trim().lowercase() }
                    ?.filter { it.isNotEmpty() }
                    ?.forEach { tag ->
                        tagsFrequencyMap[tag] = tagsFrequencyMap.getOrDefault(tag, 0) + 1
                    }
            }

            val topTags = tagsFrequencyMap.entries
                .sortedByDescending { it.value }
                .take(n)

            val tagEntries = topTags.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat())
            }

            val tagDataSet = BarDataSet(tagEntries, "Top $n Tags").apply {
                setColors(ColorTemplate.MATERIAL_COLORS.toList())
                valueTextColor = Color.BLACK
            }

            tagsChart.data = BarData(tagDataSet)
            tagsChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(topTags.map { it.key })
                granularity = 1f
                labelRotationAngle = 45f
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }
            tagsChart.setExtraOffsets(20f, 0f, 0f, 0f)
            tagsChart.invalidate()

            // BRANDS
            val brandFrequencyMap = allWearables
                .filter { !it.brand.isNullOrBlank() }
                .groupingBy { it.brand.trim().lowercase() }
                .eachCount()
            val topBrands = brandFrequencyMap.entries.sortedByDescending { it.value }.take(n)
            val brandEntries = topBrands.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat())
            }
            val brandDataSet = BarDataSet(brandEntries, "Top $n Brands").apply {
                setColors(ColorTemplate.VORDIPLOM_COLORS.toList())
                valueTextColor = Color.BLACK
            }
            brandsChart.data = BarData(brandDataSet)
            brandsChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(topBrands.map { it.key })
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                labelRotationAngle = -45f
            }
            brandsChart.setExtraOffsets(20f, 0f, 0f, 0f)
            brandsChart.invalidate()
        }
    }
}