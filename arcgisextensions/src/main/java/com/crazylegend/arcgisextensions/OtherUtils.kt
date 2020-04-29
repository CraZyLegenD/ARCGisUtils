package com.crazylegend.arcgisextensions

import android.content.Context
import com.crazylegend.kotlinextensions.exhaustive
import com.crazylegend.kotlinextensions.log.debug
import com.crazylegend.kotlinextensions.tryOrNull
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.data.TileCache
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.loadable.Loadable
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.tasks.networkanalysis.Stop
import java.text.SimpleDateFormat
import java.util.*


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


fun GregorianCalendar?.toDate(defaultDateFormat: SimpleDateFormat) = this?.let {
    defaultDateFormat.format(it.time)
}


fun esriQuery(builder: QueryParameters.() -> Unit): QueryParameters {
    val queryParameters = QueryParameters()
    builder.invoke(queryParameters)
    return queryParameters
}

fun generateToleranceEnvelope(mapView: MapView, locationPoint: Point): Envelope {
    val mapTolerance = mapView.getTolerance()
    return Envelope(locationPoint.x - mapTolerance, locationPoint.y - mapTolerance,
            locationPoint.x + mapTolerance, locationPoint.y + mapTolerance, mapView.spatialReference)
}

fun MapView?.getTolerance(tolerance: Double = 10.0): Double {
    if (this == null) return tolerance
    return tolerance * this.unitsPerDensityIndependentPixel
}


fun Loadable?.loadDSL(isDebug: Boolean = false, onFailedLoading: () -> Unit = {}, onLoading: () -> Unit = {}, onSuccessfulLoading: () -> Unit = {}): Loadable? {
    if (this == null) return null

    addDoneLoadingListener {
        when (loadStatus) {
            LoadStatus.NOT_LOADED -> {
                onFailedLoading()
                onDebug(isDebug) {
                    debug("NOT_LOADED")
                }
            }
            LoadStatus.LOADING -> {
                onLoading()
                onDebug(isDebug) {
                    debug("LOADING")
                }
            }
            LoadStatus.LOADED -> {
                onSuccessfulLoading()
                onDebug(isDebug) {
                    debug("LOADED")
                }
            }
            LoadStatus.FAILED_TO_LOAD -> {
                onFailedLoading()
                onDebug(isDebug) {
                    debug("FAILED_TO_LOAD ${loadError.errorCode} ${loadError.additionalMessage} ${loadError.localizedMessage}")
                }
            }

            null -> {
                onFailedLoading()
                onDebug(isDebug) {
                    debug("null returned when loading")
                }
            }
        }.exhaustive
        loadError?.cause?.printStackTrace()
    }
    loadAsync()
    return this
}

fun onDebug(debug: Boolean, function: () -> Any) {
    if (debug) function()
}

val MapView?.visibleAreaJSON get() = this?.visibleArea?.extent?.center?.toJson()

fun String?.envelopeFromJson() = tryOrNull { Envelope.fromJson(this).extent.center }

val FeatureLayer?.asServiceFeatureTable get() = this?.featureTable as? ServiceFeatureTable

fun generateQueryWithGeometry(toleranceEnvelope: Envelope): QueryParameters {
    val query = QueryParameters()
    query.geometry = toleranceEnvelope
    return query
}

val queryEverything get() = esriQuery { whereClause = "1=1" }

fun <V> ListenableFuture<V>?.loadDSL(onSuccessfulLoad: (V?) -> Unit): ListenableFuture<V>? {
    this?.addDoneListener {
        val result = tryOrNull {
            get()
        }
        onSuccessfulLoad(result)
    }
    return this
}
