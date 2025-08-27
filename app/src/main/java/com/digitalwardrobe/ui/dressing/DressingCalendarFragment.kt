package com.digitalwardrobe.ui.dressing

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.content.Context
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalwardrobe.R
import com.digitalwardrobe.RetrofitClient
import com.digitalwardrobe.data.DailyOutfit
import com.digitalwardrobe.data.DailyOutfitViewModel
import com.digitalwardrobe.data.DailyOutfitViewModelFactory
import com.digitalwardrobe.data.Outfit
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearable
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.digitalwardrobe.ui.wardrobe.OutfitCanvasViewModel
import com.digitalwardrobe.ui.wardrobe.OutfitPlannerFragmentArgs
import com.digitalwardrobe.ui.wardrobe.OutfitPlannerFragmentDirections
import com.digitalwardrobe.ui.wardrobe.WardrobeOutfitsFragmentDirections
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DressingCalendarViewModel : ViewModel() {
    val lastSavedDate = MutableLiveData<String>()
}

class DressingCalendarFragment : Fragment(){
    private val calendarViewModel: DressingCalendarViewModel by activityViewModels()

    private lateinit var calendarView : CalendarView
    private lateinit var dailyOutfitViewModel : DailyOutfitViewModel
    private lateinit var outfitViewModel : OutfitViewModel
    private lateinit var wearableViewModel : WearableViewModel
    private lateinit var outfitWearableViewModel : OutfitWearableViewModel

    private lateinit var outfitImageView : ImageView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentTemperature : Double = 0.0
    private var currentDate : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dressing_calendar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isAdded || context == null) return

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    getWeatherByLocation(it.latitude, it.longitude)
                }
            }
        }

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

        outfitImageView = view.findViewById(R.id.outfitImage)

        calendarView = view.findViewById(R.id.calendarView)
        calendarViewModel.lastSavedDate.value?.let { saved ->
            currentDate = saved
            val savedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(saved)
            savedDate?.let {
                calendarView.setDate(it.time, false, true)

                val calendar = Calendar.getInstance().apply { time = it }
                onDateSelected(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        } ?: run {
            //no saved date, default to today
            calendarView.post {
                val millis = calendarView.date
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = millis
                }
                onDateSelected(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            onDateSelected(year, month, dayOfMonth)
        }

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Long>("outfitId")?.observe(viewLifecycleOwner) { outfitId ->
            if (outfitId != null) {
                addDailyOutfit(outfitId)
                outfitPresentVisual()
            }
        }

        val btnSetNewOutfit = view.findViewById<MaterialButton>(R.id.btnSetNewOutfit)
        btnSetNewOutfit.setOnClickListener{
            addNewOutfit()
        }

        val btnSetRandomOutfit = view.findViewById<MaterialButton>(R.id.btnSetRandomOutfit)
        btnSetRandomOutfit.setOnClickListener{
            createRandomDailyOutfit()
        }

        val btnSelectOutfit = view.findViewById<MaterialButton>(R.id.btnSelectOutfit)
        btnSelectOutfit.setOnClickListener{
            val action = DressingCalendarFragmentDirections.actionCalendarToSelectOutfit()
            findNavController().navigate(action)
        }

        val btnRemoveOutfit = view.findViewById<MaterialButton>(R.id.btnRemoveOutfit)
        btnRemoveOutfit.setOnClickListener{
            outfitNotPresentVisual()
            removeOutfit()
        }
    }

    private fun getWeatherByLocation(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val apiKey = getString(R.string.weatherAPIKey)
                val response = RetrofitClient.weatherService.getCurrentWeatherByCoords(lat, lon, apiKey)
                currentTemperature = response.main.temp

                val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putFloat("last_known_temp", currentTemperature.toFloat())
                    .putString("last_known_condition", response.weather.firstOrNull()?.main ?: "")
                    .apply()

            } catch (e: Exception) {
                Log.v("Weather","Error: ${e.message}")
            }
        }
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentDate = dateFormat.format(Calendar.getInstance().apply { set(year, month, day) }.time)

        calendarViewModel.lastSavedDate.value = currentDate
        Log.v("date", currentDate)

        view?.findViewById<MaterialTextView>(R.id.descriptionText)?.text = currentDate + " Daily Outfit";

        //set outfit in the imageview for the date
        lifecycleScope.launch {
            val dateOutfit = dailyOutfitViewModel.getDailyOutfitByDate(currentDate)

            if (dateOutfit == null) {
                Log.d("Calendar", "No daily outfit for $currentDate")
                outfitImageView.setImageResource(R.drawable.ic_launcher_foreground)
                outfitNotPresentVisual()
                outfitImageView.setOnClickListener(null)
                return@launch
            }

            val outfit = outfitViewModel.getOutfitById(dateOutfit.outfitId)
            if (outfit != null) {
                val file = File(context?.filesDir, "outfit_preview_${outfit.id}.png")

                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    outfitImageView.setImageBitmap(bitmap)
                    outfitPresentVisual()
                } else {
                    outfitImageView.setImageResource(R.drawable.ic_launcher_foreground)
                    outfitNotPresentVisual()
                }

                outfitImageView.setOnClickListener {
                    val action = DressingCalendarFragmentDirections.actionCalendarToOutfitPlanner(outfit)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun outfitNotPresentVisual(){
        view?.findViewById<MaterialButton>(R.id.btnSetNewOutfit)?.visibility = View.VISIBLE
        view?.findViewById<MaterialButton>(R.id.btnSetRandomOutfit)?.visibility = View.VISIBLE
        view?.findViewById<MaterialButton>(R.id.btnSelectOutfit)?.visibility = View.VISIBLE
        view?.findViewById<MaterialButton>(R.id.btnRemoveOutfit)?.visibility = View.GONE
        outfitImageView.visibility = View.GONE
    }

    private fun outfitPresentVisual(){
        view?.findViewById<MaterialButton>(R.id.btnSetNewOutfit)?.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btnSetRandomOutfit)?.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btnSelectOutfit)?.visibility = View.GONE
        view?.findViewById<MaterialButton>(R.id.btnRemoveOutfit)?.visibility = View.VISIBLE
        outfitImageView.visibility = View.VISIBLE
    }

    private fun addNewOutfit() {
        Log.v("new date", currentDate)

        val newOutfit = Outfit(
            preview = null.toString(),
            addDate = currentDate,
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val outfitId = outfitViewModel.insert(newOutfit)
            val fullOutfit = newOutfit.copy(id = outfitId)

            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(fullOutfit)
            findNavController().navigate(action)
        }
    }

    private fun createRandomDailyOutfit() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendarDateString = dateFormat.format(calendarView.date)

            val calendarDate = Calendar.getInstance().apply { timeInMillis = calendarView.date }

            val newOutfit = Outfit(
                preview = null.toString(),
                addDate = calendarDateString,
            )

            val generatedOutfitId = outfitViewModel.insert(newOutfit)
            Log.v("id", generatedOutfitId.toString())

            val allWearables = wearableViewModel.getAllWearables()
            val notSpecificTemperature = getString(R.string.wearable_notSpecific)

            val todaySeason = getSeason(Calendar.getInstance())
            val selectedSeason = getSeason(calendarDate) //selected day season

            val adjustedTemperature = if (todaySeason.equals(selectedSeason, ignoreCase = true)) {
                currentTemperature
            } else {
                val seasonalTemperatureMap = mapOf(
                    "Winter" to 0.0,
                    "Spring" to 15.0,
                    "Summer" to 25.0,
                    "Autumn" to 10.0
                )
                seasonalTemperatureMap[selectedSeason] ?: currentTemperature
            }

            val wearablesBasedOnTemperature = allWearables.filter {
                it.temperature.equals(getClothingRecommendation(adjustedTemperature), ignoreCase = true) ||
                        it.temperature.equals(notSpecificTemperature, ignoreCase = true)
            }

            val wearablesByCategory = wearablesBasedOnTemperature
                .groupBy { it.category }

            val selectedWearables = mutableListOf<Wearable>()

            //one for each category
            for ((_, categoryGroup) in wearablesByCategory) {
                categoryGroup.randomOrNull()?.let { selectedWearables.add(it) }
                if (selectedWearables.size >= 4) break
            }

            //fill based on temperature if available, otherwise random
            if (selectedWearables.size < 4) {
                val alreadySelectedIds = selectedWearables.map { it.id }.toSet()
                val remainingWearables = wearablesBasedOnTemperature.filterNot { it.id in alreadySelectedIds }
                selectedWearables += remainingWearables.shuffled().take(4 - selectedWearables.size)
            }

            if (selectedWearables.size < 4) {
                val alreadySelectedIds = selectedWearables.map { it.id }.toSet()
                val remainingWearables = allWearables.filterNot { it.id in alreadySelectedIds }
                selectedWearables += remainingWearables.shuffled().take(4 - selectedWearables.size)
            }

            for ((index, wearable) in selectedWearables.withIndex()) {
                val outfitWearable = OutfitWearable(
                    outfitId = generatedOutfitId,
                    wearableId = wearable.id,
                    wearableX = 0.0f,
                    wearableY = 0.0f,
                    wearableScale = 0.25f,
                    wearableZIndex = index
                )

                outfitWearableViewModel.insert(outfitWearable)
            }

            val fullOutfit = newOutfit.copy(id = generatedOutfitId)
            addDailyOutfit(generatedOutfitId)

            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(fullOutfit)
            findNavController().navigate(action)
        }
    }

    private fun addDailyOutfit(outfitId : Long) {
        val selectedDate = calendarViewModel.lastSavedDate.value
        if (selectedDate != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val dailyOutfit = DailyOutfit(
                    outfitId = outfitId,
                    date = selectedDate,
                )

                dailyOutfitViewModel.insert(dailyOutfit)
            }
        }
    }

    fun getSeason(calendar: Calendar): String {
        val month = calendar.get(Calendar.MONTH)

        return when (month) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> "Winter"
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> "Spring"
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> "Summer"
            Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER -> "Autumn"
            else -> "Unknown"
        }
    }

    fun getClothingRecommendation(temp: Double): String {
        val temperatures = resources.getStringArray(R.array.wearableTemperatures)
        return when {
            temp < 5 -> temperatures[0]
            temp in 5.0..15.0 -> temperatures[1]
            temp in 15.0..25.0 -> temperatures[2]
            else -> temperatures[3]
        }
    }

    private fun removeOutfit(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        viewLifecycleOwner.lifecycleScope.launch {
            val dailyOutfit = dailyOutfitViewModel.getDailyOutfitByDate((todayDate))
            if (dailyOutfit != null) {
                dailyOutfitViewModel.delete(dailyOutfit)
                outfitNotPresentVisual()
            }
        }
    }
}