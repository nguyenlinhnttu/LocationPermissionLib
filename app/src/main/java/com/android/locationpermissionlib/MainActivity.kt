package com.android.locationpermissionlib

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnRequestLocation: AppCompatButton = findViewById(R.id.btnRequestLocation)
        btnRequestLocation.setOnClickListener {
            if (!LocationPermissionUtils.isPermissionGranted(this)) {
                LocationPopupUtils.dialogLocationDisclosures(this,
                    title = getString(R.string.title_location_disclosures),
                    message = getString(R.string.msg_explanation_location_permission),
                    getString(R.string.action_deny),
                    getString(R.string.action_accept),
                    onClickNeg = {
                        // Continue run app no permission.
                    },
                    onClickPos = {
                        requestLocationPermission()
                    })
            } else {
                checkPermissionAndroidQ()
            }
        }

    }

    /*
    * Receive result when request location permission.
    * */
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted = true
            permissions.entries.forEach {
                if (it.value == false) {
                    isGranted = false
                    return@registerForActivityResult
                }
            }
            if (isGranted) {
                // Check background permission android Q
                checkPermissionAndroidQ()
            } else {
                // Continue run app no permission.
            }

        }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestLocationPermissionLauncher.launch(permissions)
    }


    /*
     * Receive result when request background permission
     * */
    private val requestPermissionAndroidQ =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean ->
            // We just receive action when user close screen setting background mode.
            // Continue run app flow
        }

    private fun checkPermissionAndroidQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (LocationPermissionUtils.isBackgroundLocationGranted(this)) {
                // Continue run app flow
            } else {
                LocationPermissionUtils.openSettingBackgroundMode(requestPermissionAndroidQ)
            }

        } else {
            // Continue run app flow
        }
    }

}