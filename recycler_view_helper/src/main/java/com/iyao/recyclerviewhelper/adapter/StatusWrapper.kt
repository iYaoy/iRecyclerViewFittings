package com.iyao.recyclerviewhelper.adapter

import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import java.lang.IllegalArgumentException

class StatusWrapper<VH : RecyclerView.ViewHolder> : AbsAdapterWrapper<VH>() {

    companion object {
        const val STATUS_NORMAL = -1
    }
    private val statusViewHolders = SparseArray<VH>()
    var currentStatus : Int = STATUS_NORMAL

    override fun getItemCount() = if (currentStatus == STATUS_NORMAL) super.getItemCount() else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return if (currentStatus != STATUS_NORMAL) {
            statusViewHolders.get(currentStatus)
        } else {
            client.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        when (currentStatus) {
            STATUS_NORMAL -> super.onBindViewHolder(holder, position, payloads)
            else -> onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (currentStatus == STATUS_NORMAL) {
            client.onBindViewHolder(holder, getWrappedPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentStatus != STATUS_NORMAL) currentStatus else client.getItemViewType(getWrappedPosition(position))
    }

    override fun getWrappedPosition(wrapperPosition: Int) = wrapperPosition

    override fun getWrapperAdapterPosition(wrappedPosition: Int) = wrappedPosition

    fun setStatusView(@IntRange(from = -100, to = -2) viewType: Int, holder : VH) {
        check(viewType in -2 downTo -100, {"status must not be negative integer"}).run {
            statusViewHolders.put(viewType, holder)
        }
    }

    fun setCurrentStatus(@IntRange(from = -100, to = -2) viewType: Int) = statusViewHolders[viewType]?.apply {
        currentStatus = viewType
        notifyDataSetChanged()
    } ?: throw IllegalArgumentException("Invalid viewType: $viewType")

    fun setCurrentStatusIf(@IntRange(from = -100, to = -2) status: Int, predicate: (VH) -> Boolean) {
        statusViewHolders[status]?.takeIf {
            predicate.invoke(it)
        }.run {
            currentStatus = this?.let { status } ?: STATUS_NORMAL
            notifyDataSetChanged()
        }
    }
}