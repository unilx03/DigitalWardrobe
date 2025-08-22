package com.digitalwardrobe.ui.dressing

import com.digitalwardrobe.DragResizeTouchListener
import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digitalwardrobe.R
import com.digitalwardrobe.data.MoodboardItem
import com.digitalwardrobe.data.MoodboardItemViewModel
import com.digitalwardrobe.data.MoodboardItemViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

interface SaveableFragment {
    fun saveState()
}

class MoodboardItemCanvasViewModel : ViewModel() {
    val savedMoodboardItemStates = MutableLiveData<List<Bundle>>()
}

class DressingMoodboardFragment : Fragment(), SaveableFragment{
    private val canvasViewModel: MoodboardItemCanvasViewModel by activityViewModels() //moodboard survive in mainactivity, so everywhere

    private lateinit var canvas: FrameLayout
    private lateinit var viewModel : MoodboardItemViewModel

    private val itemMap = mutableMapOf<ImageView, MoodboardItem>()
    private var selectedItemImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dressing_moodboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        canvas = view.findViewById(R.id.canvas)

        viewModel = ViewModelProvider(
            requireActivity(),
            MoodboardItemViewModelFactory(requireActivity().application)
        )[MoodboardItemViewModel::class.java]

        lifecycleScope.launch {
            val savedStates = canvasViewModel.savedMoodboardItemStates.value?.associateBy { it.getLong("itemId") } ?: emptyMap()

            val items = viewModel.getAllMoodboardItems()

            items.forEach { item ->
                val uri = Uri.parse(item.image)

                val savedState = savedStates[item.id]
                Log.d("SaveState", "Item ${savedState?.getLong("id")} at x=${savedState?.getFloat("x")}, y=${savedState?.getFloat("y")}")

                val x = savedState?.getFloat("x") ?: item.itemX
                val y = savedState?.getFloat("y") ?: item.itemY
                val scale = savedState?.getFloat("scale") ?: item.itemScale
                val zIndex = savedState?.getInt("zIndex") ?: item.itemZIndex

                val imageView = addImageToCanvas(uri, x, y, scale, zIndex)
                itemMap[imageView] = item.copy(
                    itemX = x,
                    itemY = y,
                    itemScale = scale,
                    itemZIndex = zIndex
                )
            }
        }

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val uri = data?.data

                    if (uri != null) {
                        requireContext().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        val imageView = addImageToCanvas(uri, 0f, 0f, 1f, canvas.childCount)

                        val newMoodboardIem = MoodboardItem(
                            image = uri.toString(),
                            itemX = imageView.x,
                            itemY = imageView.y,
                            itemScale = imageView.scaleX,
                            itemZIndex = canvas.indexOfChild(imageView)
                        )
                        itemMap[imageView] = newMoodboardIem

                        lifecycleScope.launch {
                            viewModel.insert(newMoodboardIem)
                        }
                    }
                }
            }


        val btnAddImage: MaterialButton = view.findViewById(R.id.btnAddImage)
        btnAddImage.setOnClickListener {
            Log.v("LABEL", "btn clicked")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            resultLauncher.launch(intent)
        }

        val btnSave: MaterialButton = view.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            update()
        }

        val btnMoveUpLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)
        btnMoveUpLayer.setOnClickListener {
            val currentIndex = canvas.indexOfChild(selectedItemImage)
            if (currentIndex < canvas.childCount - 1) {
                canvas.removeView(selectedItemImage)
                canvas.addView(selectedItemImage, currentIndex + 1)
            }
        }

        val btnMoveDownLayer = view.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)
        btnMoveDownLayer.setOnClickListener {
            val currentIndex = canvas.indexOfChild(selectedItemImage)
            if (currentIndex > 0) {
                canvas.removeView(selectedItemImage)
                canvas.addView(selectedItemImage, currentIndex - 1)
            }
        }

        val btnDeleteItem = view.findViewById<FloatingActionButton>(R.id.btnDeleteItem)
        btnDeleteItem.setOnClickListener {
            deleteItem()
        }

        deselectImage()
    }

    override fun saveState() {
        saveMoodboardState()
    }

    override fun onPause() {
        super.onPause()

        saveMoodboardState()
    }

    fun saveMoodboardState() {
        val itemStates = itemMap.map { (imageView, moodboardItem) ->
            Bundle().apply {
                putLong("itemId", moodboardItem.id)
                putFloat("x", imageView.x)
                putFloat("y", imageView.y)
                putFloat("scale", imageView.scaleX)
                putInt("zIndex", canvas.indexOfChild(imageView))
            }
        }
        canvasViewModel.savedMoodboardItemStates.value = itemStates
    }

    private fun addImageToCanvas(uri: Uri?, x: Float, y: Float, scale: Float, zIndex: Int) : ImageView {
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
        selectedItemImage?.background = null
        selectedItemImage = imageView
        imageView.setBackgroundResource(R.drawable.image_selected_border) // Add drawable

        view?.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)?.isEnabled = true
        view?.findViewById<FloatingActionButton>(R.id.btnDeleteItem)?.isEnabled = true
    }

    private fun deselectImage() {
        selectedItemImage?.background = null
        selectedItemImage = null

        view?.findViewById<FloatingActionButton>(R.id.btnMoveUpLayer)?.isEnabled = false
        view?.findViewById<FloatingActionButton>(R.id.btnMoveDownLayer)?.isEnabled = false
        view?.findViewById<FloatingActionButton>(R.id.btnDeleteItem)?.isEnabled = false
    }

    fun update() {
        deselectImage()

        for (i in 0 until canvas.childCount) {
            val view = canvas.getChildAt(i)
            if (view is ImageView && itemMap.containsKey(view)) {
                val updated = itemMap[view]!!.copy(
                    itemX = view.x,
                    itemY = view.y,
                    itemScale = view.scaleX,
                    itemZIndex = i
                )

                itemMap[view] = updated // update map
                lifecycleScope.launch {
                    viewModel.update(updated)
                    Toast.makeText(requireContext(), "Moodboard updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteItem() {
        val selectedItem = itemMap[selectedItemImage]
        itemMap.remove(selectedItemImage)

        if (selectedItem != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.delete(selectedItem)
            }

            selectedItemImage?.let {
                canvas.removeView(it)
                deselectImage()
            }
        }
    }
}