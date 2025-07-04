package com.digitalwardrobe.ui.wardrobe

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
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.digitalwardrobe.R
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WearableDetailsFragment : Fragment() {
    private val args: WearableDetailsFragmentArgs by navArgs()
    private lateinit var viewModel: WearableViewModel
    private lateinit var currentWearable: Wearable

    private val selectedColors = mutableListOf<String>()
    private val selectedTags = mutableListOf<String>()

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

        //set date picker for addDate field
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

        //set color add chip group
        val colorInput = view.findViewById<EditText>(R.id.colorInput)
        val addColorButton = view.findViewById<Button>(R.id.addColorButton)
        val colorChipGroup = view.findViewById<ChipGroup>(R.id.colorChipGroup)

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

        //val colorsString = selectedColors.joinToString(",")
        //val colorsList = colorsString.split(",")

        //set tag add chip group
        val tagInput = view.findViewById<EditText>(R.id.tagInput)
        val addTagButton = view.findViewById<Button>(R.id.addTagButton)
        val tagChipGroup = view.findViewById<ChipGroup>(R.id.tagChipGroup)

        addTagButton.setOnClickListener {
            val tag = tagInput.text.toString().trim()
            if (tag.isNotEmpty() && !selectedTags.contains(tag)) {
                selectedTags.add(tag)
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

        //set categories droplist options
        var items = resources.getStringArray(R.array.wearableCategories)
        var adapter = ArrayAdapter(requireContext(), R.layout.droplist_item, items)
        val categoriesDropDown: AutoCompleteTextView =
            view.findViewById(R.id.wearableCategory)
        categoriesDropDown.setAdapter(adapter)

        //set seasons droplist options
        items = resources.getStringArray(R.array.wearableSeasons)
        adapter = ArrayAdapter(requireContext(), R.layout.droplist_item, items)
        val seasonsDropDown: AutoCompleteTextView =
            view.findViewById(R.id.wearableSeason)
        seasonsDropDown.setAdapter(adapter)

        val saveButton = view.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener{ updateWearable() }

        viewModel.getWearableById(wearableId.toLong()).observe(viewLifecycleOwner) { wearable ->
            wearable?.let {
                // Load image
                val bitmap = BitmapFactory.decodeStream(
                    context?.contentResolver?.openInputStream(it.image.toUri())
                )
                view.findViewById<ImageView>(R.id.wearableImage).setImageBitmap(bitmap)

                // Set basic fields
                view.findViewById<TextInputEditText>(R.id.wearableBrand).setText(it.brand)
                view.findViewById<TextInputEditText>(R.id.wearableAddDate).setText(it.addDate)
                if (it.price != 0.0)
                    view.findViewById<TextInputEditText>(R.id.wearablePrice).setText(it.price.toString())
                view.findViewById<AutoCompleteTextView>(R.id.wearableCategory).setText(it.category, false)
                view.findViewById<AutoCompleteTextView>(R.id.wearableSeason).setText(it.season, false)
                view.findViewById<TextInputEditText>(R.id.wearableNotes).setText(it.notes)

                // Populate color chips
                val colorChipGroup = view.findViewById<ChipGroup>(R.id.colorChipGroup)
                selectedColors.clear()
                colorChipGroup.removeAllViews()
                it.colors.split(",").forEach { color ->
                    if (color.isNotBlank()) {
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
                    }
                }

                // Populate tag chips
                val tagChipGroup = view.findViewById<ChipGroup>(R.id.tagChipGroup)
                selectedTags.clear()
                tagChipGroup.removeAllViews()
                it.tags.split(",").forEach { tag ->
                    if (tag.isNotBlank()) {
                        selectedTags.add(tag)
                        val chip = Chip(requireContext()).apply {
                            text = tag
                            isCloseIconVisible = true
                            setOnCloseIconClickListener {
                                tagChipGroup.removeView(this)
                                selectedTags.remove(tag)
                            }
                        }
                        tagChipGroup.addView(chip)
                    }
                }

                // Store current wearable for update
                currentWearable = it
            }
        }
    }

    fun updateWearable() {
        val updatedWearable = currentWearable.copy(
            addDate = view?.findViewById<TextInputEditText>(R.id.wearableAddDate)?.text.toString(),
            category = view?.findViewById<AutoCompleteTextView>(R.id.wearableCategory)?.text.toString(),
            brand = view?.findViewById<TextInputEditText>(R.id.wearableBrand)?.text.toString(),
            price = view?.findViewById<TextInputEditText>(R.id.wearablePrice)?.text.toString().toDoubleOrNull() ?: 0.0,
            season = view?.findViewById<AutoCompleteTextView>(R.id.wearableSeason)?.text.toString(),
            notes = view?.findViewById<TextInputEditText>(R.id.wearableNotes)?.text.toString(),
            colors = selectedColors.joinToString(","),
            tags = selectedTags.joinToString(",")
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateWearable(updatedWearable)
            Toast.makeText(requireContext(), "Wearable updated", Toast.LENGTH_SHORT).show()
        }
    }
}