package com.crazylegend.arcgisextensions.abstracts

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.crazylegend.arcgisextensions.addGraphicsOverlay
import com.crazylegend.arcgisextensions.addOnTouchListener
import com.crazylegend.kotlinextensions.context.showBackButton
import com.crazylegend.kotlinextensions.locale.LocaleHelper
import com.esri.arcgisruntime.internal.jni.it
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView


/**
 * Created by crazy on 11/19/19 to long live and prosper !
 */
abstract class AbstractMapActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {


    abstract val mapView: MapView
    var localMap: ArcGISMap? = null
    var graphicsOverlay: GraphicsOverlay? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView.addOnTouchListener(this) { screenPoint, locationPoint ->
            handleMapTouch(screenPoint, locationPoint)
        }
        loadMapPackage()

        createGraphicsOverlay()
    }

    abstract fun loadMapPackage()

    abstract fun handleMapTouch(screenPoint: Point, locationPoint: com.esri.arcgisruntime.geometry.Point)

    private fun createGraphicsOverlay() {
        graphicsOverlay = GraphicsOverlay()
        mapView.addGraphicsOverlay(graphicsOverlay)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
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