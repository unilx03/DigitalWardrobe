package com.digitalwardrobe.ui.wardrobe

import com.digitalwardrobe.ui.wardrobe.DragResizeTouchListener
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalwardrobe.R
import com.digitalwardrobe.data.Outfit
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearable
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.WearableDao
import com.digitalwardrobe.data.WearableRoomDatabase
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class OutfitPlannerFragment : Fragment(){
    private lateinit var canvas: FrameLayout
    private lateinit var wearableDao: WearableDao

    private lateinit var outfitViewModel: OutfitViewModel
    private lateinit var wearableViewModel: WearableViewModel
    private lateinit var outfitWearableViewModel: OutfitWearableViewModel

    private val args: OutfitPlannerFragmentArgs by navArgs()
    private var selectedWearableImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.outfit_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outfitViewModel = ViewModelProvider(
            requireActivity(),
            OutfitViewModelFactory(requireActivity().application)
        )[OutfitViewModel::class.java]

        val db = WearableRoomDatabase.getDatabase(requireContext())
        wearableDao = db.wearableDao()

        wearableViewModel = ViewModelProvider(
            requireActivity(),
            WearableViewModelFactory(requireActivity().application)
        )[WearableViewModel::class.java]

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<String>("wearableId")?.observe(viewLifecycleOwner) { wearableId ->
            if (wearableId != null) {
                wearableViewModel.getWearableById(wearableId.toLong()).observe(viewLifecycleOwner) { wearable ->
                    wearable?.image?.takeIf { it.isNotBlank() }?.let { imageString ->
                        val uri = Uri.parse(imageString)
                        val newWearableImageView =
                            addImageToCanvas(uri, 0f, 0f, 1f, canvas.childCount)

                        val outfitWearable = OutfitWearable(
                            outfitId = args.outfitId,
                            wearableId = wearable.id, // get from existing wearable
                            wearableX = newWearableImageView.x,
                            wearableY = newWearableImageView.y,
                            wearableScale = newWearableImageView.scaleX, // assume uniform scale
                            wearableZIndex = canvas.indexOfChild(newWearableImageView),
                        )

                        // Save to DB or ViewModel here
                        viewLifecycleOwner.lifecycleScope.launch {
                            outfitWearableViewModel.insert(outfitWearable)
                        }
                    }
                }
            }
        }

        canvas = view.findViewById(R.id.canvas)
        val btnAddWearable: MaterialButton = view.findViewById(R.id.btnAddWearable)
        btnAddWearable.setOnClickListener {
            val action = OutfitPlannerFragmentDirections.actionOutfitPlannerToSelectWearable()
            findNavController().navigate(action)
        }

        val btnSave: MaterialButton = view.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {

        }

        val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.also {
                it
                    .setMessage("Are you sure you want to delete?")
                    .setCancelable(false) //cancealable through back?
                    .setPositiveButton("Yes", { dialog, id -> deleteOutfit() })
                    .setNegativeButton("No", { dialog,id -> dialog.cancel() })
            }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        val btnMoveUpLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)
        btnMoveUpLayer?.isEnabled = false
        btnDelete.setOnClickListener {

        }

        val btnMoveDownLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)
        btnMoveDownLayer?.isEnabled = false
        btnDelete.setOnClickListener {

        }

        val btnDeleteWearable = view.findViewById<FloatingActionButton>(R.id.btnDeleteWearable)
        btnDeleteWearable?.isEnabled = false
        btnDelete.setOnClickListener {
            selectedWearableImage?.let {
                canvas.removeView(it)
                selectedWearableImage = null
                btnDeleteWearable.isEnabled = false
            }
        }

        /*viewModel.getWearablesForOutfit(outfitId).observe(viewLifecycleOwner) { list ->
            list.forEach { ow ->
                viewModel.getWearableById(ow.wearableId).observe(viewLifecycleOwner) { wearable ->
                    val uri = Uri.parse(wearable.image)
                    addImageToCanvas(uri, ow.posX, ow.posY, ow.scale, ow.zIndex)
                }
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (canvas.childCount == 0) {
            val outfitId = args.outfitId.toLong()
            outfitViewModel.getOutfitById(outfitId).observe(viewLifecycleOwner) { outfit : Outfit? ->
                if (outfit != null && outfit.preview.isBlank()) {
                    // No preview and no items – delete it
                    lifecycleScope.launch {
                        outfitViewModel.delete(outfit)
                    }
                }
            }
        }
    }

    private fun addImageToCanvas(uri: Uri, x: Float, y: Float, scale: Float, zIndex: Int) : ImageView {
        val imageView = ImageView(requireContext()).apply {
            setImageURI(uri)
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                leftMargin = x.toInt()
                topMargin = y.toInt()
            }
            scaleX = scale
            scaleY = scale
            tag = uri.toString()  // used for saving
        }

        imageView.setOnTouchListener(DragResizeTouchListener {
            selectImage(imageView) // This acts as the click
        })

        canvas.addView(imageView, zIndex)
        return imageView;
    }

    private fun selectImage(imageView: ImageView) {
        // Clear previous selection
        selectedWearableImage?.background = null

        // Update selected
        selectedWearableImage = imageView
        imageView.setBackgroundResource(R.drawable.image_selected_border) // Add drawable

        // Enable buttons
        view?.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnDeleteWearable)?.isEnabled = true
    }

    fun captureCollageScreenshot(collageView: View): Bitmap {
        val bitmap = Bitmap.createBitmap(collageView.width, collageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        collageView.draw(canvas)
        return bitmap
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): Uri {
        val file = File(context.filesDir, "$fileName.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.toUri()
    }

    /*fun updateOutfit() {
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
    }*/

    fun deleteOutfit() {
        val outfitId = args.outfitId
        outfitViewModel.getOutfitById(outfitId).observe(viewLifecycleOwner) { outfit : Outfit? ->
            if (outfit != null && outfit.preview.isBlank()) {
                // No preview and no items – delete it
                lifecycleScope.launch {
                    outfitViewModel.delete(outfit)
                }
            }
        }
    }
}