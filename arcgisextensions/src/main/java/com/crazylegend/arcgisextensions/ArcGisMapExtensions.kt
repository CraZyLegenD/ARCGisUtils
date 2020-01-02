package com.crazylegend.arcgisextensions

import android.content.Context
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */
/**
 * Checks if there are any transportationNetworks available on a null object receiver
 */
val ArcGISMap?.areTransportationNetworksAvailable get() = if (this == null) null else (!transportationNetworks.isNullOrEmpty())

/**
 * Checks if there are any transportationNetworks available
 */
val ArcGISMap.areTransportationNetworksAvailable get() = !transportationNetworks.isNullOrEmpty()

/**
 * Creates a route task from the transportation networks that are available on the [ArcGISMap]
 * @receiver ArcGISMap?
 * @param context Context
 * @param onLoadedAction Function0<Unit>
 */
fun ArcGISMap?.routeTask(context: Context, onLoadedAction:()->Unit){
    this?:return
    if (areTransportationNetworksAvailable){
        val routeTask = RouteTask(context, transportationNetworks[0])
        routeTask.addDoneLoadingListener {
            onLoadedAction()
        }
        routeTask.loadAsync()
    }
}

