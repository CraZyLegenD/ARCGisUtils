package com.crazylegend.arcgisextensions

import com.esri.arcgisruntime.mapping.view.GraphicsOverlay


/**
 * Created by crazy on 11/12/19 to long live and prosper !
 */

/**
 * Removes graphics from the overlay
 * @receiver GraphicsOverlay?
 */
fun GraphicsOverlay?.clearGraphics(){
    this?:return
    graphics.clear()
}

fun GraphicsOverlay?.unSelectAllGraphics() {
    this?.graphics?.asSequence()?.forEach { it.isSelected = false }
}

fun GraphicsOverlay?.selectAllGraphics() {
    this?.graphics?.asSequence()?.forEach { it.isSelected = true }
}