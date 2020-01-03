package com.crazylegend.arcgisutils

import android.graphics.Color
import com.crazylegend.arcgisextensions.*
import com.crazylegend.arcgisextensions.abstracts.AbstractMapActivity
import com.crazylegend.kotlinextensions.log.debug
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by crazy on 11/6/19 to long live and prosper !
 */
class MainActivity : AbstractMapActivity(R.layout.activity_main) {
    override val mapView: MapView
        get() = mview

    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var firstClick = true

    override fun loadMapPackage() {
        loadMMPKandTPKfromRAW(mapID = R.raw.map, tpkID = R.raw.sf) { map, tile ->
            val loadedMaps = map.getFirstMapOrNull
            localMap = loadedMaps
            mapView.map = loadedMaps
            val arcgisLayer = ArcGISTiledLayer(tile)
            localMap?.basemap = Basemap(arcgisLayer)
            mapView.map = localMap
        }
    }

    override fun handleMapTouch(screenPoint: android.graphics.Point, locationPoint: Point) {
        if (firstClick) {
            startPoint = locationPoint
            firstClick = false
            startPoint?.let {
                setStartMarker(it)
            }
        } else {
            endPoint = locationPoint
            firstClick = true
            endPoint?.let {
                setEndMarker(it)
            }
        }
    }




    private fun setMapMarker(location: Point, style: SimpleMarkerSymbol.Style, markerColor: Int, outlineColor: Int) {
        setMapMarker(8.0f, 2.0f, SimpleLineSymbol.Style.SOLID,
                location, style, markerColor, outlineColor, graphicsOverlay)
    }

    private fun setStartMarker(location: Point) {
        graphicsOverlay?.clearGraphics()
        setMapMarker(location, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(226, 119, 40), Color.BLUE)
        startPoint = location
        endPoint = null
    }

    private fun setEndMarker(location: Point) {
        setMapMarker(location, SimpleMarkerSymbol.Style.SQUARE, Color.rgb(40, 119, 226), Color.RED)
        endPoint = location
        findRoute()
    }

    private fun findRoute() {
        mapView.transportationNetworkOrNull?.asTaskAndSolveRoute(this,
                listOf(startPoint.asStop(),
                endPoint.asStop()),
                graphicsOverlay,
                SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.CYAN, 3f),
                { params, task ->
                    params.isReturnStops = true
                    params.isReturnDirections = true
                    params.isReturnRoutes = true
                    val travelModes = task.routeTaskInfo.travelModes
                    val walkingMode = travelModes[0]
                    params.travelMode = walkingMode

                }, {

        }, {
            debug("ROUTE AVAILABLE ? $it")
        })
    }


}