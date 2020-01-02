package com.crazylegend.arcgisextensions.abstracts

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.crazylegend.kotlinextensions.context.showBackButton
import com.crazylegend.kotlinextensions.coroutines.mainCoroutine
import com.crazylegend.kotlinextensions.exhaustive
import com.crazylegend.kotlinextensions.permissionHandlers.PermissionResult
import com.crazylegend.kotlinextensions.permissionHandlers.coroutines.PermissionCouroutineManager
import com.esri.arcgisruntime.toolkit.ar.ArcGISArView


abstract class AbstractARPlaneActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    abstract val arView: ArcGISArView
    abstract val showBackButton: Boolean

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (showBackButton)
            showBackButton()

        mainCoroutine {
            val result = PermissionCouroutineManager.requestPermissions(this, CAMERA_PERMISSION_REQUEST_CODE, CAMERA)
            result?.apply {
                when (this) {
                    is PermissionResult.PermissionGranted -> {
                        arView.registerLifecycle(lifecycle)
                        setupArViewOnPermissionGranted()
                    }
                    is PermissionResult.PermissionDenied -> {
                        onPermissionDenied()
                    }
                    is PermissionResult.ShowRationale -> {
                        showPermissionRationale()
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        permissionDeniedPermanently()
                    }
                }.exhaustive
            }
        }
    }

    abstract fun setupArViewOnPermissionGranted()
    abstract fun onPermissionDenied()
    abstract fun showPermissionRationale()
    abstract fun permissionDeniedPermanently()

    override fun onPause() {
        super.onPause()
        arView.stopTracking()
    }

    override fun onResume() {
        super.onResume()
        arView.startTracking(ArcGISArView.ARLocationTrackingMode.IGNORE)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

}


