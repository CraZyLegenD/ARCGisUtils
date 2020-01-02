package com.crazylegend.arcgisextensions

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crazylegend.kotlinextensions.coroutines.mainCoroutine
import com.crazylegend.kotlinextensions.exhaustive
import com.crazylegend.kotlinextensions.permissionHandlers.PermissionResult
import com.crazylegend.kotlinextensions.permissionHandlers.coroutines.PermissionCouroutineManager


/**
 * Created by crazy on 1/2/20 to long live and prosper !
 */



fun Fragment.checkLocationPermission(requestCode: Int = 131, onPermissionGranted: () -> Unit = {},
                                     onPermissionDenied: () -> Unit = {},
                                     onShowRationale: () -> Unit = {},
                                     onPermissionDeniedPermanently: () -> Unit = {}) {
    mainCoroutine {
        val permissionResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionCouroutineManager.requestPermissions(this, requestCode, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            PermissionCouroutineManager.requestPermissions(this, requestCode, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        permissionResult ?: return@mainCoroutine

        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                onPermissionGranted()
            }
            is PermissionResult.PermissionDenied -> {
                onPermissionDenied()
            }
            is PermissionResult.ShowRationale -> {
                onShowRationale()
                checkLocationPermission(requestCode, onPermissionGranted, onPermissionDenied, onShowRationale, onPermissionDeniedPermanently)
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                onPermissionDeniedPermanently()
            }
        }.exhaustive
    }

}

fun AppCompatActivity.checkLocationPermission( requestCode:Int = 131,
                                              onPermissionGranted: () -> Unit = {},
                                     onPermissionDenied: () -> Unit = {},
                                     onShowRationale: () -> Unit = {},
                                     onPermissionDeniedPermanently: () -> Unit = {}) {
    mainCoroutine {
        val permissionResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionCouroutineManager.requestPermissions(this, requestCode, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            PermissionCouroutineManager.requestPermissions(this, requestCode, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        permissionResult ?: return@mainCoroutine

        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                onPermissionGranted()
            }
            is PermissionResult.PermissionDenied -> {
                onPermissionDenied()

            }
            is PermissionResult.ShowRationale -> {
                onShowRationale()
                checkLocationPermission(requestCode, onPermissionGranted, onPermissionDenied, onShowRationale, onPermissionDeniedPermanently)
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                onPermissionDeniedPermanently()
            }
        }.exhaustive
    }

}