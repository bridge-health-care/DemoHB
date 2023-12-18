package com.example.bhdemo.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.bhdemo.R
import com.example.bhdemo.models.BluetoothDeviceInfo

class BluetoothDeviceAdapter(
    context: Context,
    private val deviceList: List<BluetoothDevice>) :
    ArrayAdapter<BluetoothDevice>(context, R.layout.bt_dialog, deviceList) {

    @SuppressLint("MissingPermission")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.bluetooth_item_list, parent, false)
        }

        val deviceInfo = getItem(position)

        val nameTextView: TextView = itemView!!.findViewById(R.id.name)
        val relativeLayout: RelativeLayout = itemView!!.findViewById(R.id.bluetooth_item_list_layout)

        if (deviceInfo != null) {
            nameTextView.text = deviceInfo.name.toString()
        }


        return itemView
    }
}
