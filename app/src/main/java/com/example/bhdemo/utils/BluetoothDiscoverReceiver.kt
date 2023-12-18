package com.example.bhdemo.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothDiscoverReceiver : BroadcastReceiver() {
    override fun onReceive(contet: Context?, intent: Intent?) {
        val action = intent?.action

        if (action != null) {
            if (action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                    }
                    BluetoothAdapter.SCAN_MODE_NONE -> {
                    }
                }
            }
        }
    }
}