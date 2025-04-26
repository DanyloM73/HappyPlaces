package com.danylom73.happyplaces.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.danylom73.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.io.File
import java.io.FileOutputStream

class ImageHelper(
    private val activity: AppCompatActivity,
    private val binding: ActivityAddHappyPlaceBinding
) {
    var photoUri: Uri? = null

    private val openGalleryLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                val savedImagePath = saveImageToInternalStorage(activity, uri)
                if (savedImagePath.isNotEmpty()) {
                    binding.placeImageIv.setImageURI(photoUri)
                }
            }
        }
    }

    private val openCameraLauncher = activity.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            binding.placeImageIv.setImageURI(photoUri)
        }
    }

    fun openGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(pickIntent)
    }

    fun openCamera() {
        val photoFile = File(activity.getExternalFilesDir(null),
            "happy_place_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(activity,
            "com.danylom73.happyplaces.fileprovider", photoFile)
        photoUri?.let { openCameraLauncher.launch(it) }
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            val file = File(context.filesDir, "saved_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            photoUri = Uri.fromFile(File(file.absolutePath))
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
