package com.iyao.recyclerviewhelper.touchevent

import android.graphics.Canvas
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchUIUtil
import android.view.View
import com.iyao.recyclerviewhelper.R


open class BaseItemTouchUIUtilImpl : ItemTouchUIUtil {

    var elevation = 1f

    override fun onDrawOver(c: Canvas, recyclerView: RecyclerView, view: View, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
    }


    override fun onDraw(c: Canvas, recyclerView: RecyclerView, view: View, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        view.apply {
            translationX = dX
            translationY = dY
            if (isCurrentlyActive) {
                var originalElevation: Any? = getTag(R.integer.item_touch_helper_previous_elevation)
                if (originalElevation == null) {
                    originalElevation = ViewCompat.getElevation(view)
                    val newElevation = this@BaseItemTouchUIUtilImpl.elevation + findMaxElevation(recyclerView, view)
                    ViewCompat.setElevation(this, newElevation)
                    setTag(R.integer.item_touch_helper_previous_elevation, originalElevation)
                }
            }
        }
    }

    override fun onSelected(view: View) {
    }


    override fun clearView(view: View) {
        view.apply {
            translationX = 0f
            translationY = 0f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                (getTag(R.integer.item_touch_helper_previous_elevation) as? Float)?.run {
                    ViewCompat.setElevation(this@apply, this)
                }
                setTag(R.integer.item_touch_helper_previous_elevation, null)
            }
        }
    }

    private fun findMaxElevation(recyclerView: RecyclerView, itemView: View): Float {
        val childCount = recyclerView.childCount
        var max = 0f
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (child === itemView) {
                continue
            }
            val elevation = ViewCompat.getElevation(child)
            if (elevation > max) {
                max = elevation
            }
        }
        return max
    }

}