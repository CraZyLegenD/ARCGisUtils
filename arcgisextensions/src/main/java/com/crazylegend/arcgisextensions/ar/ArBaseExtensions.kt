package com.crazylegend.arcgisextensions.ar

import com.esri.arcgisruntime.toolkit.ar.ArcGISArView


/**
 * Created by crazy on 11/19/19 to long live and prosper !
 */

fun ArcGISArView?.disablePlaneVisualization() {
    this?.arSceneView?.planeRenderer?.isEnabled = false
    this?.arSceneView?.planeRenderer?.isVisible = false
}


