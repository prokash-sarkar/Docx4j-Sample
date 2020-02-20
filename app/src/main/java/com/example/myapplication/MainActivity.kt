package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart

class MainActivity : AppCompatActivity() {

    private val WRITE_STORAGE_PERMISSION_REQUEST = 100
    private val READ_IN_FILE: Int = 2 // Request code used when reading in a file

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.choose_file).setOnClickListener(::chooseFile)

        getRuntimePermission()
    }

    private fun getRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) -> {
                }
                else -> {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_STORAGE_PERMISSION_REQUEST
                    )
                }
            }
        } else {
            // Permission has already been granted
        }
    }

    // Asks a user to read in a file
    private fun chooseFile(view: View) {
        // Only the below specified mime types are allowed in the picker
        var selectFile = Intent(Intent.ACTION_GET_CONTENT)
        selectFile.type = "*/*"
        selectFile = Intent.createChooser(selectFile, "Choose a file")
        startActivityForResult(selectFile, READ_IN_FILE)
    }

    // After receiving a result for a launched activity...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == READ_IN_FILE) { // When a result has been received, check if it is the result for READ_IN_FILE
            if (resultCode == Activity.RESULT_OK) { // heck if the operation to retrieve the Activity's result is successful
                // Attempt to retrieve the file
                try {
                    val uri = data?.data // Retrieve the file's resource locator
                    val document =
                        WordprocessingMLPackage.load(uri?.let { contentResolver.openInputStream(it) })
                    var documentPart: MainDocumentPart = document.mainDocumentPart
                    Toast.makeText(
                        this,
                        "DiffChecker successfully read in the file :D",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) { // If the app failed to attempt to retrieve the error file, throw an error alert
                    println("Exception: $e")
                    Toast.makeText(
                        this,
                        "Sorry, but there was an error reading in the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

}
