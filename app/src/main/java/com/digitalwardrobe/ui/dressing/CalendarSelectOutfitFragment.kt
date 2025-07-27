package com.digitalwardrobe.ui.dressing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import com.digitalwardrobe.data.OutfitAdapter
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CalendarSelectOutfitFragment : Fragment(){
    private lateinit var outfitViewModel: OutfitViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var outfitAdapter: OutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calendar_select_outfit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.outfitRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        outfitAdapter = OutfitAdapter(mutableListOf())
        recyclerView.adapter = outfitAdapter

        outfitViewModel = ViewModelProvider(
            requireActivity(),
            OutfitViewModelFactory(requireActivity().application)
        )[OutfitViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch{
            val outfits = outfitViewModel.getAllOutfits()
            Log.v("LABEL", "Items received: ${outfits.size}")

            outfitAdapter.updateData(outfits)
        }

        outfitAdapter.onItemClick = { selectedOutfit ->
            val outfitId = selectedOutfit.id
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("outfitId", outfitId)
            navController.popBackStack()
        }
    }
}