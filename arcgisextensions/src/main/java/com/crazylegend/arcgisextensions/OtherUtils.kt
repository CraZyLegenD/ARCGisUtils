package com.crazylegend.arcgisextensions

import android.content.Context
import com.esri.arcgisruntime.data.TileCache
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.tasks.networkanalysis.Stop


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Checks if the status is loaded in a short way
 */
val LoadStatus.isLoaded get() = this == LoadStatus.LOADED


/**
 * Gets the first map since most of the time you'll have only one map in the package .mmpk
 */
val MobileMapPackage?.getFirstMap get() = this?.maps?.firstOrNull()

/**
 * Shortly casting the geometry as a point
 * @receiver Graphic
 * @return Point
 */
fun Graphic?.asPoint() = this?.geometry as? Point

/**
 * Converts the point to a stop
 * @receiver Point?
 * @return Stop
 */
fun Point?.asStop(): Stop = Stop(this)


fun Geometry?.asPoint() = this as? Point

/**
 * Loads async mmpk and tpk altogether see [loadMMPk] and [loadTPK]
 * @receiver Context
 * @param mapID Int
 * @param mmpkName String
 * @param tpkID Int
 * @param tpkName String
 * @param onBothLoadedCallback Function2<[@kotlin.ParameterName] MobileMapPackage, [@kotlin.ParameterName] TileCache, Unit>
 */
fun Context.loadMMPKandTPKfromRAW(mapID: Int, mmpkName: String = "map.mmpk",
                                  tpkID: Int, tpkName: String = "tp.tpk",
                                  onBothLoadedCallback: (map: MobileMapPackage, tile: TileCache) -> Unit = { _, _ -> }) {
    loadMMPk(mapID, mmpkName) { map ->
        loadTPK(tpkID, tpkName) { tpk ->
            onBothLoadedCallback.invoke(map, tpk)
        }
    }
}