package com.crazylegend.arcgisextensions

import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import java.util.concurrent.ExecutionException


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */


/**
 * Just base most used geocoding parameters
 * @param spatialReferences SpatialReference
 * @return GeocodeParameters
 */
fun geocodingParameters(spatialReferences: SpatialReference): GeocodeParameters {
    val geocodeParameters = GeocodeParameters()
    geocodeParameters.resultAttributeNames.add("*")
    geocodeParameters.maxResults = 10
    geocodeParameters.outputSpatialReference = spatialReferences
    return geocodeParameters
}

/**
 * Geocodes an address from given string name
 * @receiver LocatorTask?
 * @param address String
 * @param geocodeParameters GeocodeParameters
 * @param onError Function0<Unit>
 * @param onLocationCallback Function1<[@kotlin.ParameterName] GeocodeResult, Unit>
 */
fun LocatorTask?.geocodeAddress(address: String, geocodeParameters: GeocodeParameters, onError: () -> Unit, onLocationCallback: (geocodeResult: GeocodeResult) -> Unit = {}) {
    // Call geocodeAsync on LocatorTask, passing in an address
    val geocodeFuture = this?.geocodeAsync(address, geocodeParameters)
    geocodeFuture?.addDoneListener(object : Runnable {
        override fun run() {
            try {
                // Get the results of the async operation
                val geocodeResults = geocodeFuture.get()
                if (geocodeResults.isNullOrEmpty()) {
                    //empty shit
                    onError()
                } else {
                    // Get the top geocoded location from the result and use it.
                    val location = geocodeResults[0]
                    onLocationCallback(location)
                }
            } catch (e: InterruptedException) {
                // Deal with exception...
                e.printStackTrace()
                onError()
            } catch (e: ExecutionException) {
                e.printStackTrace()
                onError()
            }

            // Done processing and can remove this listener.
            geocodeFuture.removeDoneListener(this)
        }
    })
}

