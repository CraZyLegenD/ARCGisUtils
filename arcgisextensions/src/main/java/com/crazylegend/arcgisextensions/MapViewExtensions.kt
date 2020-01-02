package com.crazylegend.arcgisextensions

import android.content.Context
import android.graphics.Point
import android.view.MotionEvent
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Shortens the graphic overlays addition to the map
 * @receiver MapView?
 * @param graphicsOverlay GraphicsOverlay?
 */
fun MapView?.addGraphicsOverlay(graphicsOverlay: GraphicsOverlay?) {
    this?.graphicsOverlays?.add(graphicsOverlay)
}

/**
 * Shortening the [ArcGISTiledLayer] layer addition
 * @receiver MapView?
 * @param layer ArcGISTiledLayer
 */
fun MapView?.setViewpointGeometryFromLayer(layer: ArcGISTiledLayer) {
    this?.setViewpointGeometryAsync(layer.fullExtent)
}

/**
 * Shortening the [ArcGISTiledLayer] layer addition but also adds padding
 * @receiver MapView?
 * @param layer ArcGISTiledLayer
 * @param padding Double
 */
fun MapView?.setViewpointGeometryFromLayer(layer: ArcGISTiledLayer, padding: Double) {
    this?.setViewpointGeometryAsync(layer.fullExtent, padding)
}

/**
 * Removes any thing on the map that the user is shown
 * @receiver MapView?
 */
fun MapView?.removeCallOut() {
    this ?: return
    if (callout.isShowing) {
        callout.dismiss()
    }
}

/**
 * Gets all the transportation networks on the mapview, beware it can throw [NullPointerException] or the list to be empty
 */
val MapView?.transportationNetworks get() = this?.map?.transportationNetworks

/**
 * Returns the first transporatation network from the map if available
 */
val MapView?.transportationNetworkOrNull get() = this?.map?.transportationNetworks?.firstOrNull()


/**
 * Adds on touch listener to the map, acts like a click listener, has a callback returning the screen point and the screen geometry point from which
 * you can extract geometry etc...
 * @receiver MapView?
 * @param context Context
 * @param onPointTouched Function2<[@kotlin.ParameterName] Point, [@kotlin.ParameterName] Point, Unit>
 */
fun MapView?.addOnTouchListener(context: Context, onPointTouched:(screenPoint:Point, locationPoint:com.esri.arcgisruntime.geometry.Point)->Unit = { _, _->}){
    this?:return
    onTouchListener = object : DefaultMapViewOnTouchListener(context, this) {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val screenPoint = e?.let {
                Point(e.x.toInt(), e.y.toInt())
            }
            screenPoint?.let {
                onPointTouched(it, this@addOnTouchListener.screenToLocation(it))
            }
            return super.onSingleTapConfirmed(e)
        }
    }
}