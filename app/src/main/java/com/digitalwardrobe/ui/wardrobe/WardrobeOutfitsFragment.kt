package com.digitalwardrobe.ui.wardrobe

import com.digitalwardrobe.ui.wardrobe.DragResizeTouchListener
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R

class WardrobeOutfitsFragment : Fragment(){
    private lateinit var canvas: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.wardrobe_outfits_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        canvas = view.findViewById(R.id.canvas)
        val btnAddImage: Button = view.findViewById(R.id.btnAddImage)

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val uri = data?.data

                    if (uri != null) {
                        addImageToCanvas(uri, 0f, 0f, 1f, canvas.childCount)
                    }
                } }

        btnAddImage.setOnClickListener {
            Log.v("LABEL", "btn clicked")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            resultLauncher.launch(intent)
        }
    }

    private fun addImageToCanvas(uri: Uri?, x: Float, y: Float, scale: Float, zIndex: Int) {
        if (uri == null) return

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

        imageView.setOnTouchListener(DragResizeTouchListener())
        canvas.addView(imageView, zIndex)
    }
}