package com.example.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_FROM_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2

    lateinit var imageView: ImageView
    lateinit var camera_button: Button
    lateinit var gallery_button: Button
    lateinit var imageUri: Uri
    lateinit var camera_contract: ActivityResultLauncher<Uri>
    lateinit var gallery_contract: ActivityResultLauncher<String>

    @SuppressLint("MissingInflatedId")
    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageview)
        camera_button = findViewById(R.id.camera_btn)
        gallery_button = findViewById(R.id.gallery_btn)


        registerLauncherCamera()
        registerLauncherGallery()




        camera_button.setOnClickListener {
            imageUri = createImageUri()!!
            checkCameraPermission()
        }

        gallery_button.setOnClickListener {
            checkGalleryPermission()

        }
    }

    fun registerLauncherCamera() {
        camera_contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            imageView.setImageURI(null)
            imageView.setImageURI(imageUri)
            Log.d("aaaaaaaaa", imageUri.toString())
        }
    }

    fun registerLauncherGallery() {
        gallery_contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageView.setImageURI(null)
            imageView.setImageURI(it)
        }

    }

    fun createImageUri(): Uri? {

        var image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.permissions.fileProvider", image
        )

    }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PICK_IMAGE_FROM_CAMERA
            )
        } else {

            camera_contract.launch(imageUri)
        }


    }

    private fun checkGalleryPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PICK_IMAGE_GALLERY
            )
        } else {

            gallery_contract.launch("image/*")
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PICK_IMAGE_FROM_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera_contract.launch(imageUri)
                } else {
//                    checkCameraPermission()
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            PICK_IMAGE_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallery_contract.launch("image/*")
                } else {
//                    checkCameraPermission()
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}