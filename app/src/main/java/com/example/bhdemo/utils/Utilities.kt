package com.example.bhdemo.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Utilities {

    companion object {
        const val PERMISSION_CAMERA_REQUEST_CODE = 1
        const val REQUEST_CODE_WRITE_STORAGE = 101
        const val MULTIPLE_PERMISSION_ID = 14

        fun appSettingOpen(context: Context) {
            Toast.makeText(context, "Go to setting and enable permission", Toast.LENGTH_LONG).show()

            val settingIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingIntent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(settingIntent)
        }


        fun warningPermissionDialog(context: Context, listener : DialogInterface.OnClickListener) {
            MaterialAlertDialogBuilder(context)
                .setMessage("All Permissions are required for this App")
                .setCancelable(false)
                .setPositiveButton("Ok", listener)
                .create()
                .show()
        }

    }

}