package com.crazylegend.arcgisextensions

import android.content.Context
import com.esri.arcgisruntime.mapping.MobileMapPackage
import java.io.File


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Loads mmpk from a path
 * @param path String
 * @param onLoadedAction Function1<[@kotlin.ParameterName] MobileMapPackage, Unit>
 * @return MobileMapPackage
 */
fun loadMMPk(path: String, onLoadedAction: (map: MobileMapPackage) -> Unit = { _ -> }): MobileMapPackage {
    val mapPackage = MobileMapPackage(path)
    mapPackage.addDoneLoadingListener {
        if (mapPackage.loadStatus.isLoaded)
            onLoadedAction(mapPackage)
    }
    mapPackage.loadAsync()
    return mapPackage
}

/**
 * Loads mmpk from a raw resource
 * @receiver Context
 * @param rawID Int
 * @param mmpkName String
 * @param onLoadedAction Function1<[@kotlin.ParameterName] MobileMapPackage, Unit>
 * @return String
 */
fun Context.loadMMPk(rawID: Int, mmpkName: String = "map.mmpk", onLoadedAction: (map: MobileMapPackage) -> Unit = { _ -> }): String {
    val saveToFile = File(filesDir, mmpkName)
    val raw = resources.openRawResource(rawID)
    raw.use {
        saveToFile.writeBytes(it.readBytes())
    }
    loadMMPk(saveToFile.path, onLoadedAction)
    return saveToFile.path
}

val MobileMapPackage?.getFirstMapOrNull get() = if (this == null) null else maps?.firstOrNull()
