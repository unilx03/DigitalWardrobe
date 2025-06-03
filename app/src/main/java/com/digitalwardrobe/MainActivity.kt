package com.digitalwardrobe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.data.Wearable
import com.digitalwardrobe.data.WearableAdapter
import com.digitalwardrobe.data.WearableViewModel
import com.digitalwardrobe.data.WearableViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dressing, R.id.nav_wardrobe, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(
                R.id.action_global_settingsFragment
            )
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*
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
            val intent = Intent(this, WearableDetailsFragment::class.java)
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

        val builder = AlertDialog.Builder(this)
        builder.also {
            it
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false) //cancealable through back?
                .setPositiveButton("Yes", { dialog, id -> finish() })
                .setNegativeButton("No", { dialog,id -> dialog.cancel() })
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }*/
}