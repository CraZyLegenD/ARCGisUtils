package com.crazylegend.arcgisextensions

import android.content.Context
import com.esri.arcgisruntime.data.TileCache
import java.io.File


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Loads tpk from path and returns the tilecache as well as receives a callback
 * @param path String
 * @param onLoadedAction Function1<[@kotlin.ParameterName] TileCache, Unit>
 * @return TileCache
 */
fun loadTPK(path: String, onLoadedAction: (tpk: TileCache) -> Unit = { _ -> }): TileCache {
    val tileCache = TileCache(path)
    tileCache.addDoneLoadingListener {
        if (tileCache.loadStatus.isLoaded)
            onLoadedAction(tileCache)
    }
    tileCache.loadAsync()
    return tileCache
}

/**
 * Loads tpk from resource
 * @receiver Context
 * @param rawID Int
 * @param mmpkName String
 * @param onLoadedAction Function1<[@kotlin.ParameterName] TileCache, Unit>
 * @return String
 */
fun Context.loadTPK(rawID: Int, mmpkName: String = "tile.tpk", onLoadedAction: (tpk: TileCache) -> Unit = { _ -> }): String {
    val saveToFile = File(filesDir, mmpkName)
    val raw = resources.openRawResource(rawID)
    raw.use {
        saveToFile.writeBytes(it.readBytes())
    }
    loadTPK(saveToFile.path, onLoadedAction)
    return saveToFile.path
}
