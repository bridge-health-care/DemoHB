package com.example.bhdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bhdemo.adapter.BluetoothDeviceAdapter
import com.example.bhdemo.databinding.ActivityMainBinding
import com.example.bhdemo.models.BluetoothDeviceInfo
import com.example.bhdemo.utils.BluetoothDiscoverReceiver
import com.example.bhdemo.utils.Utilities.Companion.MULTIPLE_PERMISSION_ID
import com.example.bhdemo.utils.Utilities.Companion.REQUEST_ENABLE_BT
import com.example.bhdemo.utils.Utilities.Companion.appSettingOpen
import com.example.bhdemo.utils.Utilities.Companion.warningPermissionDialog

/**
 * Author: Vahid Soudagar
 * MainActivity demonstrating handling multiple permissions
 */
class MainActivity : AppCompatActivity() {

    // View binding for the activity
    private lateinit var binding: ActivityMainBinding

//    receuiver
    private lateinit var receiver : BluetoothDiscoverReceiver

//    bluetooth adapter
    private lateinit var bluetoothAdapter: BluetoothAdapter

//    list view
    private lateinit var deviceListView: ListView

    // List of permissions based on Android version
    private val multiPermissionList = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        receiver
        receiver = BluetoothDiscoverReceiver()
//        bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (checkMultiplePermission()) {
            doOperation()
        }

        binding.scan.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                startBluetoothDiscovery()
            }
        }

    }


    // Method to perform the desired operation
    private fun doOperation() {
        Toast.makeText(this, "All Permissions Granted Successfully!", Toast.LENGTH_LONG).show()
    }


    // Method to check and request multiple permissions
    private fun checkMultiplePermission(): Boolean {
        // List to store permissions that need to be requested
        val listPermissionNeeded = arrayListOf<String>()

        // Check each permission in the list
        for (permission in multiPermissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission)
            }
        }


        // Request permissions if needed
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), MULTIPLE_PERMISSION_ID)
            return false
        }

        // Return true if all permissions are already granted
        return true
    }

    // Override onRequestPermissionsResult to handle permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Handle the result of multiple permissions request
        if (requestCode == MULTIPLE_PERMISSION_ID) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true

                // Check if all permissions are granted
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }

                // Perform the operation if all permissions are granted
                if (isGrant) {
                    doOperation()
                } else {
                    var someDenied = false

                    // Check if some permissions were permanently denied
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                                someDenied = true
                            }
                        }
                    }

                    // Show a toast and open app settings if some permissions were permanently denied
                    if (someDenied) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                        appSettingOpen(this)
                    } else {
                        // Show a warning dialog and recheck permissions if denied
                        warningPermissionDialog(this) { _: DialogInterface, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startBluetoothDiscovery() {
        // Show a toast indicating that Bluetooth is enabled
        Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_LONG).show()

        // Start Bluetooth discovery
        val discoverIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20)
        startActivity(discoverIntent)

        // Register the broadcast receiver for scan mode changes
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        registerReceiver(receiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startBluetoothDiscovery()
            } else {
                Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
