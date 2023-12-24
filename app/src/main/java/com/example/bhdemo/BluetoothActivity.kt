package com.example.bhdemo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bhdemo.brodcastreceiver.BluetoothDiscoverabilityReceiver
import com.example.bhdemo.brodcastreceiver.BluetoothReceiver
import com.example.bhdemo.databinding.ActivityBluetoothBinding


class BluetoothActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBluetoothBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothReceiver: BluetoothReceiver
    private lateinit var bluetoothDiscoverabilityReceiver: BluetoothDiscoverabilityReceiver
    private val discoveredDevices = mutableListOf<BluetoothDevice>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothReceiver = BluetoothReceiver()
        bluetoothDiscoverabilityReceiver = BluetoothDiscoverabilityReceiver()


        binding.onOffBT.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                Toast.makeText(this, "bluetooth is already enable", Toast.LENGTH_LONG).show()
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(enableBtIntent)
                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(bluetoothReceiver, intentFilter)
            }
        }

        binding.discoverBT.setOnClickListener {
            discoverability()
        }

        binding.pairedDevicesBT.setOnClickListener {
            getPairedDevices()
        }

        binding.scanDevices.setOnClickListener {
            discoverDevices()
        }

    }


    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(discoverDeviceReceiver,filter)
        bluetoothAdapter.startDiscovery()
    }

    private val discoverDeviceReceiver = object : BroadcastReceiver(){
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val progressDialog = ProgressDialog(this@BluetoothActivity)
            progressDialog.setMessage("Please Wait")
            var action = ""
            if(intent!=null){
                action = intent.action.toString()
            }
            when(action){
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.d("myTAG","STATE CHANGED")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("myTAG","Discovery Started")
                    discoveredDevices.clear()
                    progressDialog.show()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("myTAG","Discovery Finished")
                    progressDialog.dismiss()
                    showDiscoveredDevicesDialog()
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if(device!=null){
                        Log.d("myTAG","${device.name}  ${device.address}")
                        discoveredDevices.add(device)
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun showDiscoveredDevicesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discovered Devices")

        if (discoveredDevices.isNotEmpty()) {
            val deviceNames = discoveredDevices.map { "${it.name} (${it.address})" }.toTypedArray()

            builder.setItems(deviceNames) { _, position ->
                val selectedDevice = discoveredDevices[position]
                // Add your logic to handle the selected discovered device
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Connecting to ${selectedDevice.name}...")
                Log.d("MyTag", "Connection to ${selectedDevice.name}...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                selectedDevice.createBond()
                when (selectedDevice.bondState) {
                    BluetoothDevice.BOND_NONE -> {
                        Log.d("MyTag", "${selectedDevice.name} bond none")
                        progressDialog.dismiss()
                    }
                    BluetoothDevice.BOND_BONDING -> {
                        Log.d("MyTag", "${selectedDevice.name} bonding")
                        progressDialog.dismiss()
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        Log.d("MyTag", "${selectedDevice.name} bonded")
                        progressDialog.dismiss()
                    }
                    BluetoothDevice.ERROR -> {
                        Log.d("MyTag", "${selectedDevice.name} Error in connecting")
                        progressDialog.dismiss()
                    }
                }
                progressDialog.dismiss()
            }
        } else {
            builder.setMessage("No devices discovered.")
        }

        builder.setPositiveButton("OK") { _, _ ->
        }

        builder.setCancelable(false)
        builder.show()
    }

    @SuppressLint("MissingPermission")
    private fun getPairedDevices() {
        val pairedDevices = bluetoothAdapter.bondedDevices
        Log.d("MyTag", "Number of paired devices: ${pairedDevices.size}")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Paired Devices")

        if (pairedDevices.isNotEmpty()) {
            val deviceList = pairedDevices.toList()

            val deviceNames = deviceList.map { "${it.name} (${it.address})" }.toTypedArray()

            builder.setItems(deviceNames) { _, position ->
                val selectedDevice = deviceList[position]
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Connecting to ${selectedDevice.name}...")
                progressDialog.setCancelable(true)
                progressDialog.show()
                selectedDevice.createBond()
                when (selectedDevice.bondState) {
                    BluetoothDevice.BOND_NONE -> {
                        Log.d("MyTag", "${selectedDevice.name} bond none")
                        progressDialog.dismiss()
                    }
                    BluetoothDevice.BOND_BONDING -> {
                        Log.d("MyTag", "${selectedDevice.name} bonding")
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        Log.d("MyTag", "${selectedDevice.name} bonded")
                        progressDialog.dismiss()
                    }
                    BluetoothDevice.ERROR -> {
                        Log.d("MyTag", "${selectedDevice.name} Error in connecting")
                        progressDialog.dismiss()
                    }
                }
                progressDialog.dismiss()
            }
        } else {
            builder.setMessage("No paired devices found.")
        }

        builder.setPositiveButton("OK") { _, _ ->
            // Handle OK button click if needed
        }
        builder.setNegativeButton("Close") { _, _ ->
            builder.create().dismiss()
        }

        builder.setCancelable(false)
        builder.show()
    }


    @SuppressLint("MissingPermission")
    private fun discoverability() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20)
        startActivity(discoverableIntent)

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        registerReceiver(bluetoothDiscoverabilityReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        unregisterReceiver(bluetoothDiscoverabilityReceiver)
    }
}