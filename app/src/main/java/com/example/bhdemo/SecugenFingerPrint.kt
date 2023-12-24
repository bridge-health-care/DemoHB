package com.example.bhdemo

import SecuGen.FDxSDKPro.JSGFPLib
import SecuGen.FDxSDKPro.SGDeviceInfoParam
import SecuGen.FDxSDKPro.SGFDxDeviceName
import SecuGen.FDxSDKPro.SGFDxErrorCode
import SecuGen.FDxSDKPro.SGFDxSecurityLevel
import SecuGen.FDxSDKPro.SGFDxTemplateFormat
import SecuGen.FDxSDKPro.SGFingerInfo
import SecuGen.FDxSDKPro.SGFingerPosition
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bhdemo.databinding.ActivitySecugenFingerPrintBinding
import com.example.bhdemo.utils.ImageConverter


class SecugenFingerPrint : AppCompatActivity() {

    private lateinit var binding: ActivitySecugenFingerPrintBinding

    // Image Info >>>>>
    private var mImageWidth = 0L
    private var mImageHeight = 0L

    // Images >>>>
    private lateinit var mFingerPrintImage: ByteArray
    private lateinit var mRegisterImage: ByteArray
    private lateinit var mVerifyImage: ByteArray

    // Template >>>>>
    private lateinit var  mRegisterTemplate: ByteArray
    private lateinit var  mVerifyTemplate: ByteArray

    // Pending Intent >>>>>
    private var mPermissionIntent: PendingIntent? = null
    private var intentFilter: IntentFilter? = null

    private val ACTION_USB_PERMISSION = "com.example.bhdemo.USB_PERMISSION"
    private val TIME_OUT = 10000L
    private val IMAGE_QUALITY = 80L

    // JSGFLib >>>
    private var sgfplib: JSGFPLib? = null

    private var alertDialog: AlertDialog? = null
    private var isUsbReceiverRegistered = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecugenFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTakeFingerPrint.setOnClickListener{this};

        binding.btnRegister.setOnClickListener {
            registerFingerPrint()
        }

        binding.btnMatch.setOnClickListener {
            matchFingerPrint()
        }

        binding.btnTakeFingerPrint.isEnabled = false;
        mPermissionIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        intentFilter = IntentFilter()
        intentFilter!!.addAction(ACTION_USB_PERMISSION)
        intentFilter!!.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        intentFilter!!.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)

        sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)

    }

    private fun matchFingerPrint() {
//        if (mVerifyImage != null) mVerifyImage = null

        // Taking Verify Image >>>>>>
        mVerifyImage = ByteArray((mImageWidth * mImageHeight).toInt())
        //sgfplib.GetImage(mVerifyImage); // Capture Image instantly clicked the button without checking the presence of a fingerprint
        sgfplib!!.GetImageEx(
            mVerifyImage,
            TIME_OUT,
            IMAGE_QUALITY
        ) // Check the presence of finger and capture image. If no finger found in device until timeout.
        binding.imgFingerPrint.setImageBitmap(
            ImageConverter.toGrayscale(
                mVerifyImage,
                mImageWidth.toInt(), mImageHeight.toInt()
            )
        )
        val imgQuality = IntArray(1)
        sgfplib!!.GetImageQuality(mImageWidth, mImageHeight, mVerifyImage, imgQuality)
        val fingerInfo = SGFingerInfo()
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI
        fingerInfo.ImageQuality = imgQuality[0]
        fingerInfo.ViewNumber = 1
        sgfplib!!.CreateTemplate(fingerInfo, mVerifyImage, mVerifyTemplate)

        // Matching >>>>
        val matched = BooleanArray(1)
        sgfplib!!.MatchTemplate(
            mRegisterTemplate,
            mVerifyTemplate,
            SGFDxSecurityLevel.SL_NORMAL,
            matched
        )
        if (matched[0]) {
            Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Not Matched", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerFingerPrint() {
//        if (mRegisterImage != null) {
//            mRegisterImage = null
//        }

        // Taking Finger Print Image >>>
        mRegisterImage = ByteArray((mImageWidth * mImageHeight).toInt())
//        /135
        sgfplib?.GetImage(mRegisterImage)
        sgfplib?.GetImageEx(mRegisterImage, TIME_OUT, IMAGE_QUALITY)
        binding.imgFingerPrint.setImageBitmap(ImageConverter.toGrayscale(mRegisterImage, mImageWidth.toInt(), mImageHeight.toInt()))

        val imgQuality = IntArray(1)
        sgfplib?.GetImageQuality(mImageWidth, mImageHeight, mRegisterImage, imgQuality)

        val fingerInfo = SGFingerInfo()
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI
        fingerInfo.ImageQuality = imgQuality[0]
        fingerInfo.ViewNumber = 1

        sgfplib?.CreateTemplate(fingerInfo, mRegisterImage, mRegisterTemplate)
        Toast.makeText(this, "${imgQuality[0]}", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if (!isUsbReceiverRegistered) {
            registerReceiver(mUsbReceiver, intentFilter)
            isUsbReceiverRegistered = true
        }
        initFingerPrintDevice()
    }

    private fun captureFingerPrint() {
        mFingerPrintImage = ByteArray((mImageWidth * mImageHeight).toInt())
        sgfplib!!.GetImageEx(mFingerPrintImage, 10000, 70)
        binding.imgFingerPrint.setImageBitmap(
            ImageConverter.toGrayscale(
                mFingerPrintImage,
                mImageWidth.toInt(), mImageHeight.toInt()
            )
        )
    }

    private fun initFingerPrintDevice() {
        val error = sgfplib!!.Init(SGFDxDeviceName.SG_DEV_AUTO)
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) { // IF get Error
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND) { // If error type device not found
                dialogBuilder.setTitle("No Device Found !")
                dialogBuilder.setMessage("No fingerprint device found. Please connect a fingerprint device.")
            } else {
                dialogBuilder.setTitle("Initialization Failed !")
                dialogBuilder.setMessage("Fingerprint device initialization failed!")
            }
            dialogBuilder.setPositiveButton(
                "EXIT"
            ) { dialog, whichButton -> finish() }
            dialogBuilder.setCancelable(false)
            alertDialog = dialogBuilder.create()
            alertDialog!!.show()
        } else {
            val usbDevice = sgfplib!!.GetUsbDevice() // If Init done Now Get Device
            if (usbDevice == null) {
                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("SDU04P or SDU03P fingerprint sensor not found!")
                dialogBuilder.setTitle("SecuGen Fingerprint SDK")
                dialogBuilder.setPositiveButton(
                    "OK"
                ) { dialog, whichButton -> finish() }
                dialogBuilder.setCancelable(false)
                alertDialog = dialogBuilder.create()
                alertDialog!!.show()
            } else {
                sgfplib!!.GetUsbManager().requestPermission(usbDevice, mPermissionIntent)
            }
        }
    }

    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            /**
                             * If only one USB fingerprint reader is connected to the PC, devId will be 0. If multiple USB fingerprint readers are
                             * connected to one PC, devId can range from 0 to 9. The maximum number of SecuGen USB readers that can be
                             * connected to one PC is 10. In general, if only one USB reader is connected to the PC,
                             * then 0 or USB_AUTO_DETECT is recommended.
                             */
                            sgfplib!!.OpenDevice(0) // Opening Device
                            val deviceInfo = SGDeviceInfoParam()
                            sgfplib!!.GetDeviceInfo(deviceInfo) // Getting Device Information
                            mImageWidth = deviceInfo.imageWidth.toLong()
                            mImageHeight = deviceInfo.imageHeight.toLong()
                            sgfplib!!.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                            val mMaxTemplateSize = IntArray(1)
                            sgfplib!!.GetMaxTemplateSize(mMaxTemplateSize)
                            mRegisterTemplate = ByteArray(mMaxTemplateSize[0])
                            mVerifyTemplate = ByteArray(mMaxTemplateSize[0])
                            binding.btnTakeFingerPrint.setEnabled(true)
                        } else {
                            Toast.makeText(
                                this@SecugenFingerPrint,
                                "Device is Null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SecugenFingerPrint,
                            "Permission denied for device $device",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
                initFingerPrintDevice()
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                binding.btnTakeFingerPrint.setEnabled(false)
                Toast.makeText(
                    this@SecugenFingerPrint,
                    "Fingerprint Device Removed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        if (isUsbReceiverRegistered) {
            unregisterReceiver(mUsbReceiver)
            isUsbReceiverRegistered = false
        }
        sgfplib!!.CloseDevice()
        super.onPause()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btnTakeFingerPrint -> {
                captureFingerPrint()
            }
        }
    }

}