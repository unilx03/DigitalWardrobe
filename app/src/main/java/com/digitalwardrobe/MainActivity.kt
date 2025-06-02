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

        val wearableViewModel = ViewModelProvider(
            this,
            WearableViewModelFactory(application)
        )[WearableViewModel::class.java]

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

        //observe LiveData and insert/delete items
        wearableViewModel.allWearables.observe(this) { wearables ->
            wearableAdapter = WearableAdapter(wearables)
            recyclerView.adapter = wearableAdapter
            //recyclerView.layoutManager = ConstraintLayout(this)
        }

        wearableAdapter.onItemClick = {
            val intent = Intent(this, WearableDetailsActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }

        imgGallery = findViewById(R.id.imgGallery)
        val btnGallery: Button = findViewById(R.id.btnGallery)

        /*var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                /*if (result.resultCode == Activity.RESULT_OK && result.requestCode == GALLERY_REQ_CODE) {
                // There are no request codes
                val data: Intent? = result.data
                imgGallery.setImageURI(data?.data)
            }*/
            }*/

        btnGallery.setOnClickListener {
            Log.v("LABEL", "btn clicked")
            //val iGallery = Intent(Intent.ACTION_PICK)
            //iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            //startActivityForResult(iGallery, GALLERY_REQ_CODE)

            //val intent = Intent(Intent.ACTION_PICK, WearableDetailsActivity::class.java)
            //resultLauncher.launch(intent)
        }
    }

    /*private fun getData() {
        for (i in imageList.indices) {
            val item = Wearable(imageList[i], titleList.getOrNull(i) ?: "Unnamed")
            dataList.add(item)
        }
    }*/
}