package com.digitalwardrobe.ui.wardrobe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import kotlinx.coroutines.launch

class OutfitSelectWearableFragment : Fragment(){
    private lateinit var wearableViewModel: WearableViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var wearableAdapter: WearableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outfit_select_wearable_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.wearableRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        wearableAdapter = WearableAdapter(mutableListOf())
        recyclerView.adapter = wearableAdapter

        wearableViewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch{
            val wearables = wearableViewModel.getAllWearables()
            Log.v("LABEL", "Items received: ${wearables.size}")

            wearableAdapter.updateData(wearables)
        }

        wearableAdapter.onItemClick = { selectedWearable ->
            val wearableId = selectedWearable.id.toString()

            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("wearableId", wearableId)
            navController.popBackStack()
        }
    }
}