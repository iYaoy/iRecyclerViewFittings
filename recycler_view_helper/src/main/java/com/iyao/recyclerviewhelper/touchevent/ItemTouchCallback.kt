package com.iyao.recyclerviewhelper.touchevent

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchUIUtil
import android.util.Log


open class ItemTouchCallback(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var swipeEnable = true
    var longPressDragEnable = false
    var onMove: (RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Boolean = { _, _, _ -> false }
    var onSwiped: (RecyclerView.ViewHolder, Int) -> Unit = { _, _ -> }
    var canDragOver: (RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Boolean = { _, _, _ -> true }
    var itemTouchUIUtil: ItemTouchUIUtil = ItemTouchHelper.Callback.getDefaultUIUtil()
    var vBoundingBoxMargin = 0
    var moveThreshold: (RecyclerView.ViewHolder) -> Float = { _ -> 0.5f }
    var swipeThreshold: (RecyclerView.ViewHolder)->Float = { .5f }
    var swipeVelocityThreshold: (Float)->Float = { it }
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return onMove.invoke(recyclerView, viewHolder, target)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped.invoke(viewHolder, direction)
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return moveThreshold.invoke(viewHolder)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return swipeEnable
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longPressDragEnable
    }

    override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return canDragOver.invoke(recyclerView, current, target)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        itemTouchUIUtil.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        itemTouchUIUtil.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        itemTouchUIUtil.clearView(viewHolder.itemView)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        Log.e("tag", "viewHolder = $viewHolder, actionState = $actionState")
        if (viewHolder != null) {
            itemTouchUIUtil.onSelected(viewHolder.itemView)
        }
    }

    override fun getBoundingBoxMargin(): Int {
        return vBoundingBoxMargin
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold.invoke(viewHolder)
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return swipeVelocityThreshold.invoke(defaultValue)
    }
}