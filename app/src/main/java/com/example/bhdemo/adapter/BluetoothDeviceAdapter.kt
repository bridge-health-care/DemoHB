package com.example.bhdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.bhdemo.R
import com.example.bhdemo.models.BluetoothDeviceInfo

class BluetoothDeviceAdapter(
    context: Context,
    private val deviceList: List<BluetoothDeviceInfo>,
    private val itemClickListener: ((BluetoothDeviceInfo) -> Unit )) :
    ArrayAdapter<BluetoothDeviceInfo>(context, R.layout.bt_dialog, deviceList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.bluetooth_item_list, parent, false)
        }

        val deviceInfo = getItem(position)

        val nameTextView: TextView = itemView!!.findViewById(R.id.name)

        // Set the text for each TextView
        nameTextView.text = deviceInfo?.name

        itemView.setOnClickListener {
            deviceInfo?.let { itemClickListener.invoke(it) }
        }

        return itemView
    }
}
