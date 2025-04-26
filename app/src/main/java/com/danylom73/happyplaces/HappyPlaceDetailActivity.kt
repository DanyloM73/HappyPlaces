package com.danylom73.happyplaces

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.danylom73.happyplaces.database.AppDatabase
import com.danylom73.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.danylom73.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.launch

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding
    private lateinit var database: AppDatabase
    private var placeId: Int = -1

    private var location = ""
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getInstance(this)
        placeId = intent.getIntExtra("place_id", -1)

        setSupportActionBar(binding.placeDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.placeDetailToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (placeId != -1) {
            loadPlaceDetails(placeId)
        }

        binding.viewOnMapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("location", location)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
        }
    }

    private fun loadPlaceDetails(id: Int) {
        lifecycleScope.launch {
            val place = database.happyPlaceDao().getHappyPlaceById(id)
            binding.placeImageIv.setImageURI(place.image.toUri())
            binding.descriptionTv.text = place.description
            binding.locationTv.text = place.location
            location = place.location
            latitude = place.latitude
            longitude = place.longitude
        }
    }
}