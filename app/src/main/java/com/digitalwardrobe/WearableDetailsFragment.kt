package com.digitalwardrobe

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WearableDetailsFragment : Fragment() {
    private val args: WearableDetailsFragmentArgs by navArgs()
    private lateinit var viewModel: WearableViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.wearable_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wearableId = args.wearableId
        viewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        val dateInput = view.findViewById<TextInputEditText>(R.id.wearableAddDate)
        val dateButton = view.findViewById<Button>(R.id.dateButton)

        // normal date picker
        val datePicker: MaterialDatePicker<Long> =
            MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Choose a date")
                .build()

        dateButton.setOnClickListener {
            // supportFragmentManager to interact with the fragments associated with the date picker
            datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(selection))
            dateInput.setText(formattedDate)
        }

        val colorInput = view.findViewById<EditText>(R.id.colorInput)
        val addColorButton = view.findViewById<Button>(R.id.addColorButton)
        val colorChipGroup = view.findViewById<ChipGroup>(R.id.colorChipGroup)

        val selectedColors = mutableListOf<String>()

        addColorButton.setOnClickListener {
            val color = colorInput.text.toString().trim()
            if (color.isNotEmpty() && !selectedColors.contains(color)) {
                selectedColors.add(color)
                val chip = Chip(requireContext()).apply {
                    text = color
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        colorChipGroup.removeView(this)
                        selectedColors.remove(color)
                    }
                }
                colorChipGroup.addView(chip)
                colorInput.text?.clear()
            }
        }

        val colorsString = selectedColors.joinToString(",")
        val colorsList = colorsString.split(",")

        val tagInput = view.findViewById<EditText>(R.id.tagInput)
        val addTagButton = view.findViewById<Button>(R.id.addTagButton)
        val tagChipGroup = view.findViewById<ChipGroup>(R.id.tagChipGroup)

        val selectedTag = mutableListOf<String>()

        addTagButton.setOnClickListener {
            val tag = tagInput.text.toString().trim()
            if (tag.isNotEmpty() && !selectedTag.contains(tag)) {
                selectedTag.add(tag)
                val chip = Chip(requireContext()).apply {
                    text = tag
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        tagChipGroup.removeView(this)
                        selectedColors.remove(tag)
                    }
                }
                tagChipGroup.addView(chip)
                tagInput.text?.clear()
            }
        }

        var items = resources.getStringArray(R.array.wearableCategories)
        var adapter = ArrayAdapter(requireContext(), R.layout.droplist_item, items)
        val categoriesDropDown: AutoCompleteTextView =
            view.findViewById(R.id.wearableCategory)
        categoriesDropDown.setAdapter(adapter)

        items = resources.getStringArray(R.array.wearableSeasons)
        adapter = ArrayAdapter(requireContext(), R.layout.droplist_item, items)
        val seasonsDropDown: AutoCompleteTextView =
            view.findViewById(R.id.wearableSeason)
        seasonsDropDown.setAdapter(adapter)

        viewModel.getWearableById(wearableId.toLong()).observe(viewLifecycleOwner) { wearable ->
            // Load image from URI
            val bitmap = BitmapFactory.decodeStream(
                context?.contentResolver?.openInputStream(wearable?.image?.toUri()!!)
            )
            view.findViewById<ImageView>(R.id.wearableImage).setImageBitmap(bitmap)

            val brandEditText = view.findViewById<TextInputEditText>(R.id.wearableBrand)
            brandEditText.setText(wearable.brand)

            //update wearable in database
            updateWearable()
        }
    }

    fun updateWearable(){

    }
}