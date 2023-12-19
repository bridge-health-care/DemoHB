package com.example.bhdemo.brodcastreceiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothDiscoverabilityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null) {
            if(action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED){
                when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR)){
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE ->{
                        Log.d("MyTAG","Connectable")
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE->{
                        Log.d("MyTAG","Connectable Discoverable")
                    }
                    BluetoothAdapter.SCAN_MODE_NONE->{
                        Log.d("MyTAG","Connectable NONE")
                    }
                    BluetoothAdapter.STATE_CONNECTING->{
                        Log.d("MyTAG","Connectable Connecting")
                    }
                    BluetoothAdapter.STATE_CONNECTED->{
                        Log.d("MyTAG","Connectable Connected")
                    }
                }
            }
        }
    }
}