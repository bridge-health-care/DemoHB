package com.example.bhdemo

import android.Manifest.permission.CAMERA
import android.Manifest.permission.CAPTURE_AUDIO_OUTPUT
import android.Manifest.permission.MODIFY_AUDIO_SETTINGS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.bhdemo.databinding.ActivityMainBinding
import com.example.bhdemo.utils.Utilities
import com.example.bhdemo.utils.Utilities.Companion.MULTIPLE_PERMISSION_ID
import com.example.bhdemo.utils.Utilities.Companion.PERMISSION_CAMERA_REQUEST_CODE
import com.example.bhdemo.utils.Utilities.Companion.appSettingOpen
import com.example.bhdemo.utils.Utilities.Companion.warningPermissionDialog
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val multiPermissionList = if(Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRequest.setOnClickListener {
            if (checkMultiplePermission()) {
                doOperation()
            }
        }

    }

    private fun doOperation() {
        Toast.makeText(this, "All Permissions Granted Successfully!", Toast.LENGTH_LONG).show()
    }

    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()

        for (permission in multiPermissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission)
            }
        }

        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), MULTIPLE_PERMISSION_ID)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MULTIPLE_PERMISSION_ID) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }

                if (isGrant) {
                    doOperation()
                } else {
                    var someDenied = false
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                                someDenied = true
                            }
                        }
                    }

                    if (someDenied) {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                        appSettingOpen(this)
                    } else {
                        warningPermissionDialog(this){ _:DialogInterface, which:Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }
    }
}
