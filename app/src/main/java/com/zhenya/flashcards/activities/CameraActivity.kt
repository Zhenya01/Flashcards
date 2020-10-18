package com.zhenya.flashcards.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.zhenya.flashcards.BuildConfig
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 100

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        btCapturePhoto.setOnClickListener {
            openCamera()
        }

        btOpenGallery.setOnClickListener {
            openGallery()
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {

                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    // If there is error while creating the File, it will be null
                    null
                }

                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
                ivImage.setImageURI(uri)
            }

            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.data
                ivImage.setImageURI(uri)
            }
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("PHOTO_${timestamp}",".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
}
