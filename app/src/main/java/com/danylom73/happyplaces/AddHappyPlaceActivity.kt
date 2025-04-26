package com.danylom73.happyplaces

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.danylom73.happyplaces.database.AppDatabase
import com.danylom73.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.danylom73.happyplaces.helpers.ImageHelper
import com.danylom73.happyplaces.helpers.LocationHelper
import com.danylom73.happyplaces.helpers.PermissionHelper
import com.danylom73.happyplaces.models.HappyPlaceModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private lateinit var database: AppDatabase
    private lateinit var imageHelper: ImageHelper
    private lateinit var locationHelper: LocationHelper
    private lateinit var permissionHelper: PermissionHelper
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var happyPlace: HappyPlaceModel

    var latitude = 0.0
    var longitude = 0.0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getInstance(this)
        imageHelper = ImageHelper(this, binding)
        locationHelper = LocationHelper(this, binding)
        permissionHelper = PermissionHelper(this, imageHelper, locationHelper)

        setSupportActionBar(binding.addPlaceToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.addPlaceToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (!Places.isInitialized()) {
            Places.initialize(
                this@AddHappyPlaceActivity,
                resources.getString(R.string.maps_api_key)
            )
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        binding.dateTv.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.locationEt.setOnClickListener {
            try {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                placePickerLauncher.launch(
                    Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields
                    ).build(this@AddHappyPlaceActivity)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.addImageTv.setOnClickListener {
            showImagePickerDialog()
        }

        binding.selectCurrentLocationTv.setOnClickListener {
            permissionHelper.requestLocationPermission()
        }

        binding.saveBtn.setOnClickListener {
            if (isAllFieldsFilled()) {
                saveHappyPlaceToDb()
            } else {
                Toast.makeText(
                    this@AddHappyPlaceActivity,
                    "Please fill in all fields and select a photo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val placePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            binding.locationEt.setText(place.displayName)
            latitude = place.location!!.latitude
            longitude = place.location!!.longitude
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showImagePickerDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select action")
        val pictureDialogItems = arrayOf(
            "Select photo from gallery",
            "Capture photo from camera"
        )
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> permissionHelper.requestStoragePermission()
                1 -> permissionHelper.requestCameraPermission()
            }
        }
        pictureDialog.show()
    }

    private fun updateDate() {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding.dateTv.text = sdf.format(calendar.time)
    }

    private fun saveHappyPlaceToDb() {
        happyPlace = HappyPlaceModel(
            title = binding.titleEt.text.toString(),
            description = binding.descriptionEt.text.toString(),
            image = imageHelper.photoUri.toString(),
            date = binding.dateTv.text.toString(),
            location = binding.locationEt.text.toString(),
            latitude = latitude,
            longitude = longitude
        )
        lifecycleScope.launch {
            database.happyPlaceDao().insertHappyPlace(happyPlace)
            clearAllFields()
            Toast.makeText(
                this@AddHappyPlaceActivity,
                "Happy Place saved to DB",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this@AddHappyPlaceActivity, MainActivity::class.java))
        }
    }

    private fun isAllFieldsFilled(): Boolean {
        return !(binding.titleEt.text.isEmpty() ||
                binding.descriptionEt.text.isEmpty() ||
                binding.dateTv.text.isEmpty() ||
                binding.locationEt.text.isEmpty() ||
                imageHelper.photoUri == null)
    }

    private fun clearAllFields() {
        binding.titleEt.text.clear()
        binding.descriptionEt.text.clear()
        binding.dateTv.text = ""
        binding.locationEt.text.clear()
        binding.placeImageIv.setImageResource(R.drawable.add_screen_image_placeholder)
    }
}
