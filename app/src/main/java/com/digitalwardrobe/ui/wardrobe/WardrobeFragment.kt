package com.digitalwardrobe.ui.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import com.digitalwardrobe.data.WearableAdapter

class WardrobeFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var imgGallery: ImageView
    private lateinit var wearableAdapter: WearableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.wardrobe_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*val textView: TextView = view.findViewById(R.id.detailTitle)
        textView.text = arguments?.getString(DETAILS)
        super.onViewCreated(view, savedInstanceState)*/
    }
}