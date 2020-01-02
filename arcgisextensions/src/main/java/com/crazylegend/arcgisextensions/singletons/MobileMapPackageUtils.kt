package com.crazylegend.arcgisextensions.singletons

import android.content.Context
import com.crazylegend.arcgisextensions.getFirstMapOrNull
import com.crazylegend.arcgisextensions.isLoaded
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.MobileMapPackage
import java.io.File


/**
 * Use this if you want only one instance of mmpk
 * Created by crazy on 11/12/19 to long live and prosper !
 */
object MobileMapPackageUtils {

    var mapPackage: MobileMapPackage? = null
        private set

    private fun removeInstance() {
        mapPackage = null
    }

    /**
     * Loads a map from the directory
     * @param path String
     * @param onLoadedAction Function0<Unit>
     */
    fun loadMMPk(path: String, onLoadedAction: () -> Unit = {}) {
        removeInstance()
        mapPackage = MobileMapPackage(path)
        mapPackage ?: return
        mapPackage?.addDoneLoadingListener {
            val status = mapPackage?.loadStatus ?: return@addDoneLoadingListener
            if (status.isLoaded)
                onLoadedAction()
        }
        mapPackage?.loadAsync()
    }

    /**
     * Returns a locator task from the map package
     */
    val locatorTask get() = mapPackage?.locatorTask

    /**
     * Returns if the map is successfully loaded
     */
    val isMapLoaded
        get() : Boolean {
            val status = mapPackage?.loadStatus
            return status?.isLoaded ?: false
        }

    /**
     * Returns all the maps in the mmpk
     */
    val getMaps get() = mapPackage?.maps

    /**
     * Returns the first map in the mmpk, usually this is the most used thing
     */
    val getFirstMap get() = mapPackage?.getFirstMapOrNull

    /**
     * Loads a map from raw resources and copies it to the files directory
     * @receiver Context
     * @param rawID Int
     * @param mmpkName String
     * @param onLoadedAction Function0<Unit>
     * @return String
     */
    fun Context.loadMMPk(rawID: Int, mmpkName: String = "map.mmpk", onLoadedAction: () -> Unit = {}): String {
        removeInstance()
        val saveToFile = File(filesDir, mmpkName)
        val raw = resources.openRawResource(rawID)
        raw.use {
            saveToFile.writeBytes(it.readBytes())
        }
        loadMMPk(saveToFile.path, onLoadedAction)
        return saveToFile.path
    }

    /**
     *
     * @param onLoadedAction Function0<Unit>
     */
    fun loadLocatorTask(onLoadedAction: () -> Unit = {}) {
        val task = locatorTask ?: return

        task.addDoneLoadingListener {
            if (task.loadStatus.isLoaded)
                onLoadedAction()
        }
        task.loadAsync()
    }

}