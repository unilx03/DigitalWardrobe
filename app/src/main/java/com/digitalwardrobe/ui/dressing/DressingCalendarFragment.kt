package com.digitalwardrobe.ui.dressing

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.digitalwardrobe.R
import com.digitalwardrobe.data.DailyOutfitViewModel
import com.digitalwardrobe.data.DailyOutfitViewModelFactory
import com.digitalwardrobe.data.Outfit
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.ui.wardrobe.WardrobeOutfitsFragmentDirections
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

    private lateinit var outfitImageView : ImageView

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
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = dateFormat.format(Calendar.getInstance().apply {
            set(year, month, day)  // Set the date selected
        }.time)

        Toast.makeText(requireContext(), "Selected: $selectedDate", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val dateOutfit = dailyOutfitViewModel.getDailyOutfitByDate(selectedDate)

            if (dateOutfit == null) {
                Log.d("Calendar", "No daily outfit for $selectedDate")
                outfitImageView.setImageResource(R.drawable.ic_launcher_foreground)
                outfitImageView.setOnClickListener(null)
                return@launch
            }

            val outfit = outfitViewModel.getOutfitById(dateOutfit.outfitId)
            if (outfit != null) {
                val file = File(requireContext().filesDir, "outfit_preview_${outfit.id}.png")

                if (file.exists()) {
                    // Load bitmap from file directly
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    outfitImageView.setImageBitmap(bitmap)
                } else {
                    outfitImageView.setImageResource(R.drawable.ic_launcher_foreground) // fallback if file missing
                }

                outfitImageView.setOnClickListener {
                    val action = DressingCalendarFragmentDirections.actionCalendarToOutfitPlanner(outfit)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun addOutfit(random : Boolean) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        var newOutfit = Outfit(
            preview = null.toString(),
            addDate = todayDate,
        )

        if (random){
            newOutfit = createRandomOutfit(newOutfit)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val outfitId = outfitViewModel.insert(newOutfit)
            val fullOutfit = newOutfit.copy(id = outfitId)
            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(fullOutfit)
            findNavController().navigate(action)
        }
    }

    private fun createRandomOutfit(outfit : Outfit) : Outfit {
        //create random outfit

        return outfit
    }
}