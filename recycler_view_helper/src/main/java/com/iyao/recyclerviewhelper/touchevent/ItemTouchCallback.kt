package com.iyao.recyclerviewhelper.touchevent

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.RecyclerView


open class ItemTouchCallback(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var swipeEnable = true
    var longPressDragEnable = false
    var onMove: (RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Boolean = { _, _, _ -> false }
    var onMoved: (RecyclerView, RecyclerView.ViewHolder, Int, RecyclerView.ViewHolder, Int, Int, Int) -> Unit = { _, _, _, _, _, _, _ -> Unit}
    var onSwiped: (RecyclerView.ViewHolder, Int) -> Unit = { _, _ -> }
    var canDragOver: (RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Boolean = { _, _, _ -> true }
    var itemTouchUIUtil: ItemTouchUIUtil = ItemTouchHelper.Callback.getDefaultUIUtil()
    var vBoundingBoxMargin = 0
    var moveThreshold: (RecyclerView.ViewHolder) -> Float = { _ -> 0.5f }
    var swipeThreshold: (RecyclerView.ViewHolder)->Float = { .5f }
    var swipeVelocityThreshold: (Float)->Float = { it }
    var actionState: Int = ItemTouchHelper.ACTION_STATE_IDLE
    private set

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return onMove.invoke(recyclerView, viewHolder, target)
    }

    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                         fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int,
                         y: Int) {
        onMoved.invoke(recyclerView, viewHolder, fromPos, target, toPos, x, y)
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
        this.actionState = actionState
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