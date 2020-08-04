package com.crazylegend.arcgisextensions.abstracts

import android.Manifest.permission.CAMERA
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.crazylegend.kotlinextensions.context.showBackButton
import com.esri.arcgisruntime.toolkit.ar.ArcGISArView


abstract class AbstractARPlaneActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    abstract val arView: ArcGISArView
    abstract val showBackButton: Boolean


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (showBackButton)
            showBackButton()

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when (it) {
                null -> {
                    onPermissionDenied()
                }
                true -> {
                    arView.registerLifecycle(lifecycle)
                    setupArViewOnPermissionGranted()
                }
                false -> {
                    onPermissionDenied()
                }
            }
        }.launch(CAMERA)
    }

    abstract fun setupArViewOnPermissionGranted()
    abstract fun onPermissionDenied()

    override fun onPause() {
        super.onPause()
        arView.stopTracking()
    }

    override fun onResume() {
        super.onResume()
        arView.startTracking(ArcGISArView.ARLocationTrackingMode.IGNORE)
    }

}


