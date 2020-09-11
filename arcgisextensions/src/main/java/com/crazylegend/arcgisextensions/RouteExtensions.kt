package com.crazylegend.arcgisextensions

import android.content.Context
import android.graphics.Color
import com.esri.arcgisruntime.data.TransportationNetworkDataset
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.*
import java.util.concurrent.ExecutionException


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Convert the route task to a route with the following parameters
 * @receiver RouteTask?
 * @param fromAddressResult Graphic?
 * @param toAddressResult Graphic?
 * @param onError Function0<Unit>
 * @param onStartStop [@kotlin.ExtensionFunctionType] Function1<Stop, Unit>
 * @param onFinishStop [@kotlin.ExtensionFunctionType] Function1<Stop, Unit>
 * @param routeParamsCallback Function1<[@kotlin.ParameterName] RouteParameters, Unit>
 * @param onRouteCallback Function1<[@kotlin.ParameterName] Route, Unit>
 */
fun RouteTask?.solveRoute(
        fromAddressResult: Graphic?,
        toAddressResult: Graphic?,
        onError: () -> Unit,
        onStartStop: Stop.() -> Unit,
        onFinishStop: Stop.() -> Unit,
        routeParamsCallback: (params: RouteParameters) -> Unit,
        onRouteCallback: (route: Route) -> Unit) {

    if (fromAddressResult == null) {
        return
    }
    if (toAddressResult == null) {
        return
    }

    if (this == null) {
        return
    }

    val routeParams: RouteParameters
    try {
        routeParams = createDefaultParametersAsync().get()
        routeParamsCallback.invoke(routeParams)
        val start = Stop(fromAddressResult.asPoint())
        val finish = Stop(toAddressResult.asPoint())
        start.onStartStop()
        finish.onFinishStop()
        routeParams.setStops(listOf(start, finish))

        val routeFuture = solveRouteAsync(routeParams)
        routeFuture.addDoneListener {
            // Show results of solved route.
            val routeResult: RouteResult
            try {
                routeResult = routeFuture.get()
                if (!routeResult.routes.isNullOrEmpty()) {
                    // Add first result to the map as a graphic.
                    val topRoute = routeResult.routes[0]
                    onRouteCallback.invoke(topRoute)
                }
            } catch (e: InterruptedException) {
                onError()
            } catch (e: ExecutionException) {
                onError()
            }
        }
    } catch (e: InterruptedException) {
        onError()
    } catch (e: ExecutionException) {
        onError()
    }

}


/**
 * Solves the route and checks if it's available
 * @receiver RouteTask?
 * @param routeParameters RouteParameters?
 * @param graphicsOverlay GraphicsOverlay?
 * @param routeSymbol SimpleLineSymbol
 * @param routeAvailability Function1<[@kotlin.ParameterName] Boolean, Unit>
 * @param routeCallback Function1<[@kotlin.ParameterName] Route, Unit>
 */
fun RouteTask?.solveRoute(routeParameters: RouteParameters?, graphicsOverlay: GraphicsOverlay?,
                          routeSymbol: SimpleLineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3f),
                          routeAvailability: (availability: Boolean) -> Unit,
                          routeCallback: (route: Route) -> Unit = {}) {
    val routeResult = this?.solveRouteAsync(routeParameters)
    routeResult?.addDoneListener {
        val success = tryOrElse(false) {
            val result = routeResult.get()
            val firstRoute = result.routes.firstOrNull()
            firstRoute ?: return@addDoneListener
            routeCallback(firstRoute)
            val routePolyLine = firstRoute.routeGeometry
            val routeGraphics = Graphic(routePolyLine, routeSymbol)
            graphicsOverlay?.graphics?.add(routeGraphics)
            true
        }
        routeAvailability.invoke(success)
    }
}

internal inline fun <T> tryOrElse(defaultValue: T, block: () -> T): T = tryOrNull(block)
        ?: defaultValue
/**
 * Directly converts the transportation network data set
 * @receiver TransportationNetworkDataset?
 * @param context Context
 * @param stops List<Stop?>?
 * @param graphicsOverlay GraphicsOverlay?
 * @param routeSymbol SimpleLineSymbol
 * @param routeParamsCallback Function2<[@kotlin.ParameterName] RouteParameters, [@kotlin.ParameterName] RouteTask, Unit>
 * @param routeCallback Function1<[@kotlin.ParameterName] Route, Unit>
 * @param routeAvailability Function1<[@kotlin.ParameterName] Boolean, Unit>
 */
fun TransportationNetworkDataset?.asTaskAndSolveRoute(context: Context,
                                                      stops: List<Stop?>?,
                                                      graphicsOverlay: GraphicsOverlay?,
                                                      routeSymbol: SimpleLineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3f),
                                                      routeParamsCallback: (params: RouteParameters, task: RouteTask) -> Unit = { _, _ -> },
                                                      routeCallback: (route: Route) -> Unit = {},
                                                      routeAvailability: (availability: Boolean) -> Unit) {
    val routeTask = RouteTask(context, this)
    routeTask.loadAsync()
    routeTask.addDoneLoadingListener {
        if (routeTask.loadStatus == LoadStatus.LOADED) {
            val params = routeTask.createDefaultParametersAsync()
            params.addDoneListener {
                val routeParameters = params.get()
                routeParamsCallback.invoke(routeParameters, routeTask)
                routeParameters.setStops(stops ?: emptyList())
                routeTask.solveRoute(routeParameters = routeParameters, graphicsOverlay = graphicsOverlay,routeSymbol = routeSymbol, routeAvailability = {
                    routeAvailability.invoke(it)
                }, routeCallback = {
                    routeCallback.invoke(it)
                })
            }
        }
    }
}

