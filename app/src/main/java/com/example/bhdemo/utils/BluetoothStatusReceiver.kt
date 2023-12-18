package com.example.bhdemo.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothStatusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_ON -> {
                    Toast.makeText(context, "State on", Toast.LENGTH_LONG).show()
                }
                BluetoothAdapter.STATE_OFF -> {
                    Toast.makeText(context, "State of ", Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    Toast.makeText(context, "Turning off", Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                    Toast.makeText(context, "Turning on", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}