package com.crazylegend.arcgisextensions

import com.esri.arcgisruntime.mapping.view.LocationDisplay


/**
 * Created by crazy on 4/29/20 to long live and prosper !
 */

val LocationDisplay.latNLong: Pair<Double, Double>
    get() {
        val position = location.position
        val latitude = position.y
        val longitude = position.x
        return Pair(latitude, longitude)
    }


val LocationDisplay.latitude: Double
    get() {
        val position = location.position
        return  position.y
    }

val LocationDisplay.longitude: Double
    get() {
        val position = location.position
        return  position.x
    }