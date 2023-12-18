package com.example.bhdemo.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothDiscoverReceiver : BroadcastReceiver() {
    override fun onReceive(contet: Context?, intent: Intent?) {
        val action = intent?.action

        if (action != null) {
            if (action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                        Toast.makeText(contet, "Bluetooth Connectable", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                        Toast.makeText(contet, "Bluetooth Discoverable", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothAdapter.SCAN_MODE_NONE -> {
                    }
                }
            }
        }
    }
}