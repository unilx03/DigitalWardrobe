package com.digitalwardrobe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>

    private lateinit var wearableAdapter: WearableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageList = arrayOf(
            R.drawable.ic_launcher_background)

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
            "Camera")

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<Wearable>()
        getData()

        wearableAdapter = WearableAdapter(dataList)
        recyclerView.adapter = wearableAdapter
        wearableAdapter.onItemClick = {
            val intent = Intent(this, WearableDetailsActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }
    }
    private fun getData(){
        for (i in imageList.indices){
            val dataClass = Wearable(imageList[i], titleList[i])
            dataList.add(dataClass)
        }

        recyclerView.adapter = WearableAdapter(dataList)
    }
}