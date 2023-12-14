package com.example.bhdemo

import android.Manifest.permission.CAMERA
import android.Manifest.permission.CAPTURE_AUDIO_OUTPUT
import android.Manifest.permission.MODIFY_AUDIO_SETTINGS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bhdemo.databinding.ActivityMainBinding
import com.example.bhdemo.utils.Utilities.Companion.PERMISSION_CAMERA_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

/*

 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            if (hasPermissions()){
                Toast.makeText(this@MainActivity, "All Permissions Granted", Toast.LENGTH_LONG).show()
            }

            btnRequest.setOnClickListener {
                requestPermissions()
            }

            requestPermissions()

        }

    }


    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.request_permission),
            PERMISSION_CAMERA_REQUEST_CODE,
            CAMERA,
            RECORD_AUDIO,
            MODIFY_AUDIO_SETTINGS,
            CAPTURE_AUDIO_OUTPUT,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    }


    private fun hasPermissions() =
        EasyPermissions.hasPermissions(
            this,
            CAMERA,
            RECORD_AUDIO,
            MODIFY_AUDIO_SETTINGS,
            CAPTURE_AUDIO_OUTPUT,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

}