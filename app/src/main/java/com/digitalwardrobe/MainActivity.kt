package com.digitalwardrobe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableAdapter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Wearable>
    private lateinit var imageList: Array<Int>
    private lateinit var titleList: Array<String>
    private lateinit var imgGallery: ImageView
    private lateinit var wearableAdapter: WearableAdapter

    private val GALLERY_REQ_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageList = arrayOf(R.drawable.ic_launcher_background)

        titleList = arrayOf(
            "ListView",
            "CheckBox",
            "ImageView",
            "Toggle Switch",
            "Date Picker",
            "Rating Bar",
            "Time Picker",
            "TextView",
            "EditText",
            "Camera"
        )

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        getData()

        wearableAdapter = WearableAdapter(dataList)
        recyclerView.adapter = wearableAdapter

        // Optional: Click listener for item
        wearableAdapter.onItemClick = {
            val intent = Intent(this, WearableDetailsActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }

        imgGallery = findViewById(R.id.imgGallery)
        val btnGallery: Button = findViewById(R.id.btnGallery)

        btnGallery.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(iGallery, GALLERY_REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE) {
            imgGallery.setImageURI(data?.data)
        }
    }

    private fun getData() {
        for (i in imageList.indices) {
            val item = Wearable(imageList[i], titleList.getOrNull(i) ?: "Unnamed")
            dataList.add(item)
        }
    }
}