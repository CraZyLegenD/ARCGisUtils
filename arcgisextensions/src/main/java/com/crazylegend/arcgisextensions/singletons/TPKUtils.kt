package com.crazylegend.arcgisextensions.singletons

import android.content.Context
import com.esri.arcgisruntime.data.TileCache
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.Basemap
import java.io.File


/**
 * Use this if you want only one instance of tpk file
 * Created by crazy on 11/12/19 to long live and prosper !
 */
object TPKUtils {

    var tileCache: TileCache? = null
        private set
    var tiledLayer: ArcGISTiledLayer? = null
        private set

    fun loadTileCache(tpkPath: String) {
        tileCache = null
        tiledLayer = null
        tileCache = TileCache(tpkPath)
        tiledLayer = ArcGISTiledLayer(tileCache)
    }

    val generateBaseMap get() = if (tiledLayer == null) null else Basemap(tiledLayer)

    fun Context.loadTPK(rawID:Int, tpkName:String = "tile.tpk"){
        val saveToFile = File(filesDir, tpkName)
        val raw = resources.openRawResource(rawID)
        raw.use {
            saveToFile.writeBytes(it.readBytes())
        }

        loadTileCache(saveToFile.path)

    }
}