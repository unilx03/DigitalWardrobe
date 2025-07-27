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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.net.Uri
import android.widget.Button
import androidx.navigation.fragment.navArgs
import androidx.room.ColumnInfo
import com.digitalwardrobe.R
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
import com.digitalwardrobe.ui.wardrobe.OutfitPlannerFragmentArgs
import com.digitalwardrobe.ui.wardrobe.OutfitPlannerFragmentDirections
import com.digitalwardrobe.ui.wardrobe.WardrobeOutfitsFragmentDirections
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DressingCalendarFragment : Fragment(){
    private lateinit var calendarView : CalendarView
    private lateinit var dailyOutfitViewModel : DailyOutfitViewModel
    private lateinit var outfitViewModel : OutfitViewModel
    private lateinit var wearableViewModel : WearableViewModel
    private lateinit var outfitWearableViewModel : OutfitWearableViewModel

    private lateinit var outfitImageView : ImageView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentTemperature : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dressing_calendar_fragment, container, false)
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

        outfitImageView = view.findViewById(R.id.outfitImage)

        calendarView = view.findViewById(R.id.calendarView)
        calendarView.post {
            val todayMillis = calendarView.date
            val calendar = Calendar.getInstance().apply {
                timeInMillis = todayMillis
            }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            onDateSelected(year, month, day)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ContextCompat.checkSelfPermission(
                requireContext(),
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
    }

    private fun getWeatherByLocation(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val apiKey = getString(R.string.weatherAPIKey)
                val response = RetrofitClient.weatherService.getCurrentWeatherByCoords(lat, lon, apiKey)
                currentTemperature = response.main.temp
                //Log.v("Weather","Temp: $temp°C\nWear: $clothes")
            } catch (e: Exception) {
                Log.v("Weather","Error: ${e.message}")
            }
        }
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = dateFormat.format(Calendar.getInstance().apply {
            set(year, month, day)  // Set the date selected
        }.time)

        //set outfit in the imageview for the date
        lifecycleScope.launch {
            val dateOutfit = dailyOutfitViewModel.getDailyOutfitByDate(selectedDate)

            if (dateOutfit == null) {
                Log.d("Calendar", "No daily outfit for $selectedDate")
                outfitImageView.setImageResource(R.drawable.ic_launcher_foreground)
                outfitNotPresentVisual()
                outfitImageView.setOnClickListener(null)
                return@launch
            }

            val outfit = outfitViewModel.getOutfitById(dateOutfit.outfitId)
            if (outfit != null) {
                val file = File(context?.filesDir, "outfit_preview_${outfit.id}.png")

                if (file.exists()) {
                    // Load bitmap from file directly
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    outfitImageView.setImageBitmap(bitmap)
                    outfitPresentVisual()
                } else {
                    outfitImageView.setImageResource(R.drawable.ic_launcher_foreground) // fallback if file missing
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        val newOutfit = Outfit(
            preview = null.toString(),
            addDate = todayDate,
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val outfitId = outfitViewModel.insert(newOutfit)
            val fullOutfit = newOutfit.copy(id = outfitId)

            addDailyOutfit(outfitId)

            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(fullOutfit)
            findNavController().navigate(action)
        }
    }

    private fun createRandomDailyOutfit() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = dateFormat.format(Date())

            val newOutfit = Outfit(
                preview = null.toString(),
                addDate = todayDate,
            )

            val generatedOutfitId = outfitViewModel.insert(newOutfit)
            Log.v("id", generatedOutfitId.toString())

            val allWearables = wearableViewModel.getAllWearables()
            val notSpecificTemperature = getString(R.string.wearable_notSpecific)

            val wearablesBasedOnTemperature = allWearables.filter {
                it.temperature.equals(getClothingRecommendation(currentTemperature), ignoreCase = true) ||
                        it.temperature.equals(notSpecificTemperature, ignoreCase = true)
            }

            val wearablesByCategory = wearablesBasedOnTemperature
                .filter { it.category != null }
                .groupBy { it.category }

            val selectedWearables = mutableListOf<Wearable>()

            // Pick 1 from each category
            for ((_, categoryGroup) in wearablesByCategory) {
                categoryGroup.randomOrNull()?.let { selectedWearables.add(it) }
                if (selectedWearables.size >= 4) break
            }

            // Fill up to 4 with temperature-based wearables
            if (selectedWearables.size < 4) {
                val alreadySelectedIds = selectedWearables.map { it.id }.toSet()
                val remainingWearables = wearablesBasedOnTemperature.filterNot { it.id in alreadySelectedIds }
                selectedWearables += remainingWearables.shuffled().take(4 - selectedWearables.size)
            }

            // If after this, you still have less than 4, fill from allWearables ignoring temp
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
                    wearableScale = 1.0f,
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        viewLifecycleOwner.lifecycleScope.launch {
            val dailyOutfit = DailyOutfit(
                outfitId = outfitId,
                date = todayDate,
            )

            dailyOutfitViewModel.insert(dailyOutfit)
        }
    }

    fun getClothingRecommendation(temp: Double): String {
        val temperatures = resources.getStringArray(R.array.wearableTemperatures)
        return when {
            temp < 5 -> temperatures[0]  // e.g. "Below 5°C - Cold: Heavy coat, gloves, boots"
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