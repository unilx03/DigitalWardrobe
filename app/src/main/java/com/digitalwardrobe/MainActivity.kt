package com.digitalwardrobe

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imgGallery: ImageView
    private lateinit var wearableAdapter: WearableAdapter

    private val GALLERY_REQ_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.wearableRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        wearableAdapter = WearableAdapter(mutableListOf())
        recyclerView.adapter = wearableAdapter

        val wearableViewModel = ViewModelProvider(
            this,
            WearableViewModelFactory(application)
        )[WearableViewModel::class.java]

        //observe LiveData and insert/delete items
        wearableViewModel.allWearables.observe(this) { wearables ->
            Log.v("LABEL", "observe")

            wearableAdapter = WearableAdapter(wearables)
            recyclerView.adapter = wearableAdapter
        }

        // Example: Add a new wearable on launch
        val newWearable = Wearable(
            title = "Red T-Shirt",
            image = "image",
            type = "Topwear",
            color = "Red",
            season = "Summer",
            notes = "Bought on sale"
        )
        wearableViewModel.insert(newWearable)

        wearableAdapter.onItemClick = {
            val intent = Intent(this, WearableDetailsActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }

        imgGallery = findViewById(R.id.imgGallery)
        val btnGallery: Button = findViewById(R.id.btnGallery)

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                imgGallery.setImageURI(data?.data)
            } }

        btnGallery.setOnClickListener {
            Log.v("LABEL", "btn clicked")
            val iGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(iGallery, GALLERY_REQ_CODE)

            //val intent = Intent(this, WearableDetailsActivity::class.java)
            //resultLauncher.launch(intent)
        }
    }
}