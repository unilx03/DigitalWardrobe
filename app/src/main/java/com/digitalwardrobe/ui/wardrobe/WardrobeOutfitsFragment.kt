package com.digitalwardrobe.ui.wardrobe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import com.digitalwardrobe.data.Outfit
import com.digitalwardrobe.data.OutfitAdapter
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WardrobeOutfitsFragment : Fragment() {
    private lateinit var outfitViewModel: OutfitViewModel
    private lateinit var outfitWearableViewModel: OutfitWearableViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var outfitAdapter: OutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wardrobe_outfits_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.outfitsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        outfitAdapter = OutfitAdapter(mutableListOf())
        recyclerView.adapter = outfitAdapter

        outfitViewModel = ViewModelProvider(
            requireActivity(),
            OutfitViewModelFactory(requireActivity().application)
        )[OutfitViewModel::class.java]

        outfitWearableViewModel = ViewModelProvider(
            requireActivity(),
            OutfitWearableViewModelFactory(requireActivity().application)
        )[OutfitWearableViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch observe@{
            val outfits = outfitViewModel.getAllOutfits()
            Log.v("LABEL", "Items received: ${outfits.size}")

            val context = view?.context ?: return@observe
            val validOutfits = mutableListOf<Outfit>()

            outfits.forEach { outfit ->
                val file = File(context.filesDir, "outfit_preview_${outfit.id}.png")

                if (file.exists()) {
                    validOutfits.add(outfit)
                } else {
                    //delete invalid outfits with related outfitwearables
                    lifecycleScope.launch {
                        deleteOutfit(outfit.id)
                    }
                }
            }

            outfitAdapter.updateData(validOutfits)
        }

        outfitAdapter.onItemClick = { selectedOutfit ->
            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(selectedOutfit)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
        }

        view.findViewById<FloatingActionButton>(R.id.buttonAddOutfit)?.setOnClickListener {
            addOutfit()
        }
    }

    private fun addOutfit() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        val newOutfit = Outfit(
            preview = null.toString(),
            addDate = todayDate,
        )

        lifecycleScope.launch {
            val outfitId = outfitViewModel.insert(newOutfit)
            val fullOutfit = newOutfit.copy(id = outfitId)
            val action = WardrobeOutfitsFragmentDirections.actionWardrobeToOutfit(fullOutfit)
            findNavController().navigate(action)
        }
    }

    fun deleteOutfit(outfitId : Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val outfitWearables = outfitWearableViewModel.getWearablesForOutfit(outfitId)
            outfitWearables.forEach { ow ->
                if (ow != null)
                    outfitWearableViewModel.delete(ow)
            }

            val outfit = outfitViewModel.getOutfitById(outfitId)

            if (outfit != null) {
                outfitViewModel.delete(outfit)
            }
        }
    }
}