package com.crazylegend.arcgisextensions

import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol


/**
 * Created by crazy on 11/19/19 to long live and prosper !
 */


/**
 * Sets up a marker and it adds it to the map
 * @param markerSize Float
 * @param markerOutlineThickness Float
 * @param lineSymbolStyle Style
 * @param location Point
 * @param style Style
 * @param markerColor Int
 * @param outlineColor Int
 * @param graphicsOverlay GraphicsOverlay?
 * @param callback Function2<[@kotlin.ParameterName] SimpleMarkerSymbol, [@kotlin.ParameterName] Graphic, Unit>
 */
fun setMapMarker(
        markerSize: Float = 8.0f,
        markerOutlineThickness: Float = 2.0f,
        lineSymbolStyle: SimpleLineSymbol.Style = SimpleLineSymbol.Style.SOLID,
        location: Point,
        style: SimpleMarkerSymbol.Style,
        markerColor: Int,
        outlineColor: Int,
        graphicsOverlay: GraphicsOverlay?,
        callback: (pointSymbol: SimpleMarkerSymbol, pointGraphic: Graphic) -> Unit = { _, _ -> }) {

    val pointSymbol = SimpleMarkerSymbol(style, markerColor, markerSize)
    pointSymbol.outline = SimpleLineSymbol(lineSymbolStyle, outlineColor, markerOutlineThickness)
    val pointGraphic = Graphic(location, pointSymbol)
    callback(pointSymbol, pointGraphic)
    graphicsOverlay?.addGraphics(pointGraphic)
}

fun GraphicsOverlay?.addGraphics(pointGraphic: Graphic) {
    this?.graphics?.add(pointGraphic)
}
