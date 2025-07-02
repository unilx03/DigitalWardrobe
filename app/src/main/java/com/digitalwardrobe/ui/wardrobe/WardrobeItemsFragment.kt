package com.digitalwardrobe.ui.wardrobe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WardrobeItemsFragment : Fragment(){
    private lateinit var wearableViewModel: WearableViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var wearableAdapter: WearableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.wardrobe_items_fragment, container, false)
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

        //observe LiveData and insert/delete items
        wearableViewModel.allWearables.observe(viewLifecycleOwner) { wearables ->
            Log.v("LABEL", "Items received: ${wearables.size}")

            wearableAdapter.updateData(wearables)
        }

        wearableAdapter.onItemClick = { selectedWearable ->
            val wearableId = selectedWearable.id.toString()

            // This gets the NavController from the hosting activity's NavHostFragment
            val navController = requireActivity().findNavController(R.id.nav_view)

            val action = WardrobeItemsFragmentDirections.actionWardrobeToDetails(wearableId)
            navController.navigate(action)
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data

                    val uri = data?.data
                    if (uri != null) {
                        requireContext().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        // Now you can safely store this uri.toString() in the database
                        val imageUriString = uri.toString()
                        addWearable(imageUriString)
                    }
                } }

        view.findViewById<FloatingActionButton>(R.id.buttonAddWearable)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            resultLauncher.launch(intent)
        }

        view.findViewById<FloatingActionButton>(R.id.buttonRemoveWearables)?.setOnClickListener {
            deleteWearables()
        }

        /*val builder = AlertDialog.Builder(context)
        builder.also {
            it
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false) //cancealable through back?
                .setPositiveButton("Yes", { dialog, id -> finish() })
                .setNegativeButton("No", { dialog,id -> dialog.cancel() })
        }
        val alert: AlertDialog = builder.create()
        alert.show()*/
    }

    private fun addWearable(uri: String){
        // Add a new wearable on launch
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        val newWearable = Wearable(
            image = uri,
            addDate = todayDate,
            category = "",
            colors = "",
            tags = "",
            brand = "",
            price = 0.0,
            season = "",
            notes = ""
        )
        wearableViewModel.insert(newWearable)
    }

    private fun deleteWearables(){
        wearableViewModel.deleteAll()
    }
}