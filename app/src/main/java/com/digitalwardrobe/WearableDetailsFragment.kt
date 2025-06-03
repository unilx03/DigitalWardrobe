package com.digitalwardrobe

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.ComponentActivity

class WearableDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.wearable_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textView: TextView = view.findViewById(R.id.detailTitle)
        textView.text = arguments?.getString(DETAILS)
        super.onViewCreated(view, savedInstanceState)
    }

    companion object{
        const val DETAILS: String = "details"
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_wearable_details)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wearable_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*val getData = intent.getParcelableExtra<Wearable>("android")
        if (getData != null) {
            val detailTitle: TextView = findViewById(R.id.detailTitle)
            val detailImage: ImageView = findViewById(R.id.detailImage)
            detailTitle.text = getData.dataTitle
        }*/
    }*/
}