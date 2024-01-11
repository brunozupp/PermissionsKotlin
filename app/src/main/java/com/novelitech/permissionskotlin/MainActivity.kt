package com.novelitech.permissionskotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.novelitech.permissionskotlin.databinding.ActivityMainBinding
import java.security.Permission
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnPermissions.setOnClickListener {
            requestPermissions()
        }
    }

    private fun hasWriteExternalStoragePermission() : Boolean {

        // checkSelfPermission = Check if the user accepted the permission in the past
        val currentStatusPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return currentStatusPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun hasLocationForegroundPermission() : Boolean {
        val currentStatusPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return currentStatusPermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * If you need this for a real app, then you also should check if the user actually uses
     * Android Q (Android 10 - SDK API number 29). Otherwise, you don't want to check that permission and it may crash without
     * a check.
     * On Android 10 (API level 29) and higher, you must declare the ACCESS_BACKGROUND_LOCATION
     * permission in your app's manifest in order to request background location access at runtime.
     * On earlier versions of Android, when your app receives foreground location access, it
     * automatically receives background location access as well.
     * */
    private fun hasLocationBackgroundPermission() : Boolean {
        val currentStatusPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        return currentStatusPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissionsToBeRequested = mutableListOf<String>()

        //  For android versions which less than API 29 (android 10)
        //  you need to request WRITE_EXTERNAL_STORAGE .
        //  otherwise it's already granted because of ScopedStorage that released at android 11.
        //  Keep in mind that, on devices that run Android 10 (API level 29) or higher, your app can
        //  contribute to well-defined media collections such as MediaStore.Downloads without
        //  requesting any storage-related permissions.
        if(Build.VERSION.SDK_INT < 29) {
            Log.d("PermissionsRequest", "ENTROU NO WRITE_EXTERNAL_STORAGE ${Build.VERSION.SDK_INT}")
            if(!hasWriteExternalStoragePermission()) {
                permissionsToBeRequested.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if(!hasLocationForegroundPermission()) {
            permissionsToBeRequested.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        /**
         * On Android 10 (API level 29) and higher, you must declare the ACCESS_BACKGROUND_LOCATION
         * permission in your app's manifest in order to request background location access at runtime.
         * On earlier versions of Android, when your app receives foreground location access, it
         * automatically receives background location access as well.
         */
        if(Build.VERSION.SDK_INT >= 29) {
            if(!hasLocationBackgroundPermission() && hasLocationForegroundPermission()) {
                permissionsToBeRequested.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }

        if(permissionsToBeRequested.isNotEmpty()) {

            // IMPORTANT: the context need to be 'this' and not 'this.applicationContext'
            // The last attribute 'requestCode' is used to identify which request is the current called function.
            // For example, I can pass 0 to identify this as a Request of Background Location Permission and
            // pass 1 to identify it as a Request of Write External Storage Permission.
            // In this case, I don't need to differ between the requests, so I just pass all of them and send 0 or other number
            ActivityCompat.requestPermissions(this, permissionsToBeRequested.toTypedArray(), 0)
        }
    }

    // A function that is called when the user accepts ou declines all the permissions.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray // This is the PackageManager. permissions status
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionsRequest", "${permissions[i]} granted")
                } else {
                    Log.d("PermissionsRequest", "${permissions[i]} NOT granted")
                }
            }
        }
    }
}