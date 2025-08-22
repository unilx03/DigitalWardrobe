package com.digitalwardrobe.ui.wardrobe

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalwardrobe.DragResizeTouchListener
import com.digitalwardrobe.R
import com.digitalwardrobe.data.Outfit
import com.digitalwardrobe.data.OutfitViewModel
import com.digitalwardrobe.data.OutfitViewModelFactory
import com.digitalwardrobe.data.OutfitWearable
import com.digitalwardrobe.data.OutfitWearableViewModel
import com.digitalwardrobe.data.OutfitWearableViewModelFactory
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class OutfitCanvasViewModel : ViewModel() {
    val savedWearableStates = MutableLiveData<List<Bundle>>()
}

class OutfitPlannerFragment : Fragment(){
    private val canvasViewModel: OutfitCanvasViewModel by viewModels()

    private lateinit var canvas: FrameLayout
    private lateinit var currentOutfit: Outfit

    private lateinit var outfitViewModel: OutfitViewModel
    private lateinit var wearableViewModel: WearableViewModel
    private lateinit var outfitWearableViewModel: OutfitWearableViewModel

    private val args: OutfitPlannerFragmentArgs by navArgs()
    private val wearableMap = mutableMapOf<ImageView, OutfitWearable>()
    private var selectedWearableImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outfit_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        canvas = view.findViewById(R.id.canvas)
        currentOutfit = args.outfit

        lifecycleScope.launch {
            val savedStates = canvasViewModel.savedWearableStates.value?.associateBy { it.getLong("wearableId") } ?: emptyMap()
            val outfitWearables = outfitWearableViewModel.getWearablesForOutfit(args.outfit.id)

            outfitWearables.forEach { ow ->
                if (ow != null) {
                    val wearable = wearableViewModel.getWearableById(ow.wearableId)
                    if (wearable != null && wearable.image.isNotBlank()) {
                        val uri = Uri.parse(wearable.image)

                        val savedState = savedStates[ow.wearableId]
                        val x = savedState?.getFloat("x") ?: ow.wearableX
                        val y = savedState?.getFloat("y") ?: ow.wearableY
                        val scale = savedState?.getFloat("scale") ?: ow.wearableScale
                        val zIndex = savedState?.getInt("zIndex") ?: ow.wearableZIndex

                        val imageView = addImageToCanvas(uri, x, y, scale, zIndex)
                        wearableMap[imageView] = ow.copy(
                            wearableX = x,
                            wearableY = y,
                            wearableScale = scale,
                            wearableZIndex = zIndex
                        )
                    }
                }
            }
        }

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<String>("wearableId")?.observe(viewLifecycleOwner) { wearableId ->
            if (wearableId != null) {
                lifecycleScope.launch {
                    val outfitWearable = outfitWearableViewModel.getWearableForOutfit(
                        args.outfit.id,
                        wearableId.toLong()
                    )
                    if (outfitWearable == null) {
                        val wearable = wearableViewModel.getWearableById(wearableId.toLong())

                        if (wearable != null) {
                            val uri = Uri.parse(wearable.image)
                            val newWearableImageView =
                                addImageToCanvas(uri, 0f, 0f, 1f, canvas.childCount)

                            val newOutfitWearable = OutfitWearable(
                                outfitId = args.outfit.id,
                                wearableId = wearable.id,
                                wearableX = newWearableImageView.x,
                                wearableY = newWearableImageView.y,
                                wearableScale = newWearableImageView.scaleX,
                                wearableZIndex = canvas.indexOfChild(newWearableImageView)
                            )
                            wearableMap[newWearableImageView] = newOutfitWearable

                            outfitWearableViewModel.insert(newOutfitWearable)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Wearable already added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val btnAddWearable: MaterialButton = view.findViewById(R.id.btnAddWearable)
        btnAddWearable.setOnClickListener {
            val action = OutfitPlannerFragmentDirections.actionOutfitPlannerToSelectWearable()
            findNavController().navigate(action)
        }

        val btnSave: MaterialButton = view.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            updateOutfit()
        }

        val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.also {
                it
                    .setMessage("Delete Outfit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", { dialog, id -> deleteOutfit() })
                    .setNegativeButton("No", { dialog,id -> dialog.cancel() })
            }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        val btnMoveUpLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)
        btnMoveUpLayer.setOnClickListener {
            val currentIndex = canvas.indexOfChild(selectedWearableImage)
            if (currentIndex < canvas.childCount - 1) {
                canvas.removeView(selectedWearableImage)
                canvas.addView(selectedWearableImage, currentIndex + 1)
            }
        }

        val btnMoveDownLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)
        btnMoveDownLayer.setOnClickListener {
            val currentIndex = canvas.indexOfChild(selectedWearableImage)
            if (currentIndex > 0) {
                canvas.removeView(selectedWearableImage)
                canvas.addView(selectedWearableImage, currentIndex - 1)
            }
        }

        val btnDeleteWearable = view.findViewById<FloatingActionButton>(R.id.btnDeleteWearable)
        btnDeleteWearable.setOnClickListener {
            deleteOutfitWearable()
        }

        deselectImage()
    }

    override fun onPause() {
        super.onPause()

        val wearableStates = wearableMap.map { (imageView, outfitWearable) ->
            Bundle().apply {
                putLong("wearableId", outfitWearable.wearableId)
                putFloat("x", imageView.x)
                putFloat("y", imageView.y)
                putFloat("scale", imageView.scaleX)
                putInt("zIndex", canvas.indexOfChild(imageView))
            }
        }
        canvasViewModel.savedWearableStates.value = wearableStates
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
            tag = uri.toString()
        }

        imageView.setOnTouchListener(DragResizeTouchListener {
            selectImage(imageView)
        })

        canvas.addView(imageView, zIndex)

        return imageView;
    }

    private fun selectImage(imageView: ImageView) {
        selectedWearableImage?.background = null

        selectedWearableImage = imageView
        imageView.setBackgroundResource(R.drawable.image_selected_border) // Add drawable

        view?.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnDeleteWearable)?.isEnabled = true
    }

    private fun deselectImage() {
        selectedWearableImage?.background = null

        selectedWearableImage = null

        view?.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)?.isEnabled = false
        view?.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)?.isEnabled = false
        view?.findViewById<FloatingActionButton>(R.id.btnDeleteWearable)?.isEnabled = false
    }

    fun updateOutfit() {
        deselectImage()

        for (i in 0 until canvas.childCount) {
            val view = canvas.getChildAt(i)
            if (view is ImageView && wearableMap.containsKey(view)) {
                val updated = wearableMap[view]!!.copy(
                    wearableX = view.x,
                    wearableY = view.y,
                    wearableScale = view.scaleX,
                    wearableZIndex = i
                )

                wearableMap[view] = updated
                lifecycleScope.launch {
                    outfitWearableViewModel.update(updated)
                }
            }
        }

        val bitmap = captureCollageScreenshot(canvas)
        val previewUri = saveBitmapToInternalStorage(requireContext(), bitmap, "outfit_preview_${args.outfit.id}")

        viewLifecycleOwner.lifecycleScope.launch {
            val updatedOutfit = currentOutfit.copy(
                preview = previewUri.toString()
            )

            outfitViewModel.update(updatedOutfit)
            Toast.makeText(requireContext(), "Outfit updated", Toast.LENGTH_SHORT).show()
        }
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

    fun deleteOutfit() {
        val outfitId = currentOutfit.id

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

            findNavController().popBackStack()
        }
    }

    fun deleteOutfitWearable() {
        val selectedOutfitWearable = wearableMap[selectedWearableImage]
        wearableMap.remove(selectedWearableImage)

        if (selectedOutfitWearable != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                outfitWearableViewModel.delete(selectedOutfitWearable)
            }

            selectedWearableImage?.let {
                canvas.removeView(it)
                deselectImage()
            }
        }
    }
}