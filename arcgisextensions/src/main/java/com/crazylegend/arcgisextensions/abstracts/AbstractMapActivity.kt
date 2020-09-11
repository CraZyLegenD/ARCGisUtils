package com.crazylegend.arcgisextensions.abstracts

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.crazylegend.arcgisextensions.addGraphicsOverlay
import com.crazylegend.arcgisextensions.addOnTouchListener
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.MapView


/**
 * Created by crazy on 11/19/19 to long live and prosper !
 */
abstract class AbstractMapActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {


    abstract val mapView: MapView
    var localMap: ArcGISMap? = null
    var graphicsOverlay: GraphicsOverlay? = null
    var locationDisplay: LocationDisplay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView.addOnTouchListener(this) { screenPoint, locationPoint ->
            handleMapTouch(screenPoint, locationPoint)
        }
        loadMapPackage()
        createGraphicsOverlay()
    }

    fun setupLocationDisplay(onPermissionDenied: () -> Unit = {},onPermissionGranted: () -> Unit = {}) {
        locationDisplay = mapView.locationDisplay
        locationDisplay?.addDataSourceStatusChangedListener { dataSourceStatusChangedEvent ->
            if (dataSourceStatusChangedEvent.isStarted || dataSourceStatusChangedEvent.error == null) {
                return@addDataSourceStatusChangedListener
            }
            askForMultiplePermissions(onPermissionDenied, onPermissionGranted).launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }
        locationDisplay?.autoPanMode = LocationDisplay.AutoPanMode.COMPASS_NAVIGATION
        locationDisplay?.startAsync()
    }

    fun askForMultiplePermissions(onDenied: () -> Unit = {}, onPermissionsGranted: () -> Unit = {}) =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                val granted = result.map { it.value }.filter { it == false }
                if (granted.isNullOrEmpty()) {
                    onPermissionsGranted()
                } else {
                    onDenied()
                }
            }

    abstract fun loadMapPackage()

    abstract fun handleMapTouch(screenPoint: Point, locationPoint: com.esri.arcgisruntime.geometry.Point)

    private fun createGraphicsOverlay() {
        graphicsOverlay = GraphicsOverlay()
        mapView.addGraphicsOverlay(graphicsOverlay)
    }


    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }


    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }
}