package com.crazylegend.arcgisextensions.ar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import com.crazylegend.arcgisextensions.R
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.MobileScenePackage
import com.esri.arcgisruntime.mapping.NavigationConstraint
import com.esri.arcgisruntime.mapping.view.Camera
import com.esri.arcgisruntime.mapping.view.DefaultSceneViewOnTouchListener
import com.esri.arcgisruntime.toolkit.ar.ArcGISArView
import com.esri.arcgisruntime.toolkit.extension.logTag
import com.google.ar.core.Plane
import java.io.File
import kotlin.math.roundToInt


/**
 * Created by crazy on 11/19/19 to long live and prosper !
 */


var sceneHasBeenConfigured = false

@SuppressLint("ClickableViewAccessibility")
fun ArcGISArView.onTouchListener(widthDetection: (detectedWidth: Float, detectedHeightFloat: Float) -> Unit = { _, _ -> },
                                 loadSceneFromPackage: (plane: Plane) -> Unit,
                                 onPlaneNotRecognized: () -> Unit = {}) {
    sceneView.setOnTouchListener(object : DefaultSceneViewOnTouchListener(sceneView) {
        override fun onSingleTapConfirmed(motionEvent: MotionEvent?): Boolean {
            // get the hit results for the tap
            val hitResults = arSceneView?.arFrame?.hitTest(motionEvent!!)
            // check if the tapped point is recognized as a plane by ArCore
            if (!hitResults.isNullOrEmpty() && hitResults[0].trackable is Plane) {
                // get a reference to the tapped plane
                val plane = hitResults[0].trackable as Plane
                // get the tapped point as a graphics point
                widthDetection.invoke(plane.extentX, plane.extentZ)
                val screenPoint = motionEvent?.let {
                    android.graphics.Point(motionEvent.x.roundToInt(),
                            motionEvent.y.roundToInt())
                }
                // if initial transformation set correctly
                val initialTransformSetCorrectly = screenPoint?.let { setInitialTransformationMatrix(it) }
                if (initialTransformSetCorrectly != null && initialTransformSetCorrectly) {
                    // the scene hasn't been configured
                    if (!sceneHasBeenConfigured) {
                        loadSceneFromPackage(plane)
                    } else if (sceneView.scene != null) {
                        // use information from the scene to determine the origin camera and translation factor
                        updateTranslationFactorAndOriginCamera(sceneView.scene, plane)
                    }
                }
            } else {
                onPlaneNotRecognized()
            }
            return super.onSingleTapConfirmed(motionEvent)
        }
    })
}

fun ArcGISArView.updateTranslationFactorAndOriginCamera(scene: ArcGISScene, plane: Plane) {
    // load the scene's first layer
    scene.operationalLayers[0].loadAsync()
    scene.operationalLayers[0].addDoneLoadingListener {
        // get the scene extent
        val layerExtent = scene.operationalLayers[0].fullExtent
        // calculate the width of the layer content in meters
        val width = GeometryEngine
                .lengthGeodetic(layerExtent, LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)
        // set the translation factor based on scene content width and desired physical size
        translationFactor = (width / plane.extentX)
        // find the center point of the scene content
        val centerPoint = layerExtent.center
        // find the altitude of the surface at the center
        val elevationFuture = sceneView.scene.baseSurface
                .getElevationAsync(centerPoint)
        elevationFuture.addDoneListener {
            try {
                val elevation = elevationFuture.get()
                // create a new origin camera looking at the bottom center of the scene
                originCamera = (Camera(Point(centerPoint.x, centerPoint.y, elevation), 0.toDouble(), 90.toDouble(), 0.toDouble()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


fun Context.loadSceneFromPath(path: String, plane: Plane, mArView: ArcGISArView) {
    val mobileScenePackage = MobileScenePackage(path)
    mobileScenePackage.loadAsync()
    mobileScenePackage.addDoneLoadingListener {
        // if it loaded successfully and the mobile scene package contains a scene
        if ((mobileScenePackage.loadStatus == LoadStatus.LOADED && mobileScenePackage.scenes.isNotEmpty())) {
            // get a reference to the first scene in the mobile scene package, which is of a section of philadelphia
            val philadelphiaScene = mobileScenePackage.scenes[0]
            // add the scene to the AR view's scene view
            mArView.sceneView.scene = philadelphiaScene
            // set the base surface to fully opaque
            philadelphiaScene.baseSurface.opacity = 0f
            // let the camera move below ground
            philadelphiaScene.baseSurface.navigationConstraint = NavigationConstraint.NONE
            sceneHasBeenConfigured = true
            // set translation factor and origin camera for scene placement in AR
            mArView.updateTranslationFactorAndOriginCamera(philadelphiaScene, plane)
        } else {
            val error = ("Failed to load mobile scene package: " + mobileScenePackage.loadError.message)
            Log.e(this.logTag, error)
        }
    }
}

fun Context.loadSceneFromPackage(rawID:Int, name:String = "ar.mspk", plane: Plane, mArView: ArcGISArView) {
    // create a mobile scene package from a path a local .mspk
    resources.openRawResource(rawID).use {
        val files = File(filesDir, name)
        files.writeBytes(it.readBytes())
        val mobileScenePackage = MobileScenePackage(files.path)
        mobileScenePackage.loadAsync()
        mobileScenePackage.addDoneLoadingListener {
            // if it loaded successfully and the mobile scene package contains a scene
            if ((mobileScenePackage.loadStatus == LoadStatus.LOADED && mobileScenePackage.scenes.isNotEmpty())) {
                // get a reference to the first scene in the mobile scene package, which is of a section of philadelphia
                val philadelphiaScene = mobileScenePackage.scenes[0]
                // add the scene to the AR view's scene view
                mArView.sceneView.scene = philadelphiaScene
                // set the base surface to fully opaque
                philadelphiaScene.baseSurface.opacity = 0f
                // let the camera move below ground
                philadelphiaScene.baseSurface.navigationConstraint = NavigationConstraint.NONE
                sceneHasBeenConfigured = true
                // set translation factor and origin camera for scene placement in AR
                mArView.updateTranslationFactorAndOriginCamera(philadelphiaScene, plane)
            } else {
                val error = ("Failed to load mobile scene package: " + mobileScenePackage.loadError.message)
                Log.e(this.logTag, error)
            }
        }
    }
    // load the mobile scene package

}