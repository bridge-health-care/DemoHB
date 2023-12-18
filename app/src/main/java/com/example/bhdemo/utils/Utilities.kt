package com.example.bhdemo.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Utility class containing common functions for the application.
 */
class Utilities {

    companion object {
        // Constant representing the request code for multiple permissions
        const val MULTIPLE_PERMISSION_ID = 14
        const val REQUEST_ENABLE_BT = 15

        /**
         * Open app settings to allow the user to enable necessary permissions.
         * @param context The context in which the app settings will be opened.
         */
        fun appSettingOpen(context: Context) {
            // Show a toast message guiding the user to settings
            Toast.makeText(context, "Go to settings and enable permission", Toast.LENGTH_LONG).show()

            // Create an intent to open the app settings for the current package
            val settingIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingIntent.data = Uri.parse("package:${context.packageName}")

            // Start the intent to open app settings
            context.startActivity(settingIntent)
        }

        /**
         * Display a warning dialog indicating that all permissions are required for the app.
         * @param context The context in which the dialog will be displayed.
         * @param listener The listener to handle the button click in the dialog.
         */
        fun warningPermissionDialog(context: Context, listener: DialogInterface.OnClickListener) {
            // Create a material alert dialog with a warning message
            MaterialAlertDialogBuilder(context)
                .setMessage("All Permissions are required for this App")
                .setCancelable(false)
                .setPositiveButton("Ok", listener)
                .create()
                .show()
        }

    }
}
