package com.example.bhdemo.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothDiscoverConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = ""
        if (intent != null) {
            action = intent.action.toString()
        }

        when (action) {
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                Log.d("Tag", "Bluetooth discovery started")
            }
        }
    }
}