package com.digitalwardrobe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.ContactsContract.Data
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.digitalwardrobe.data.Wearable

class WearableDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wearable_details)

        /*
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wearable_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        /*val getData = intent.getParcelableExtra<Wearable>("android")
        if (getData != null) {
            val detailTitle: TextView = findViewById(R.id.detailTitle)
            val detailImage: ImageView = findViewById(R.id.detailImage)
            detailTitle.text = getData.dataTitle
        }*/
    }
}