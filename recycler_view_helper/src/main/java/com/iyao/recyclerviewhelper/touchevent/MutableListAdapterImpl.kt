package com.iyao.recyclerviewhelper.touchevent

import com.iyao.recyclerviewhelper.adapter.MutableListAdapter
import com.iyao.recyclerviewhelper.adapter.getWrappedPosition
import com.iyao.recyclerviewhelper.adapter.takeAdapterInstance
import java.util.*

open class MutableListAdapterImpl(dragDirs: Int, swipeDirs: Int) : ItemTouchCallback(dragDirs, swipeDirs) {

    init {
        swipeEnable = true
        longPressDragEnable = false
        onMove = { recyclerView, viewHolder, target ->
            when {
                recyclerView.isComputingLayout || recyclerView.isComputingLayout -> false
                else -> recyclerView.adapter?.let {
                    recyclerView.takeAdapterInstance<MutableListAdapter<*, *>>().apply {
                        val fromPosition = viewHolder.adapterPosition
                        val toPosition = target.adapterPosition
                        val wrappedFromPosition = it.getWrappedPosition(this, fromPosition)
                        val wrappedToPosition = it.getWrappedPosition(this, toPosition)
                        if (fromPosition < toPosition) {
                            for (i in wrappedFromPosition until wrappedToPosition) {
                                Collections.swap(this, i, i + 1)
                            }
                        } else {
                            for (i in wrappedFromPosition downTo wrappedToPosition + 1) {
                                Collections.swap(this, i, i - 1)
                            }
                        }
                        notifyItemMoved(wrappedFromPosition, wrappedToPosition)
                    }
                }?.let { true } ?: throw NullPointerException("the adapter is null")
            }
        }
        canDragOver = { _, current, target ->
            current.itemViewType == target.itemViewType
        }


        itemTouchUIUtil = PlaceHolderItemTouchUIUtilImpl().apply {
            radius = 25f
            elevation = 20f
        }
    }
}