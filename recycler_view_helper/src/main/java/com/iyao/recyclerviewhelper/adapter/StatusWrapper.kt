package com.iyao.recyclerviewhelper.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

class StatusWrapper<VH : RecyclerView.ViewHolder>: AbsAdapterWrapper<VH>() {

    companion object {
        const val STATUS_NORMAL = -1
    }
    private val statusViewHolders = SparseArray<VH>()
    var currentStatus : Int = STATUS_NORMAL

    override fun getItemCount() = if (currentStatus == STATUS_NORMAL) super.getItemCount() else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when {
            currentStatus != STATUS_NORMAL -> {
                val vh = statusViewHolders.get(currentStatus)
                (vh.itemView.parent as? ViewGroup)?.removeView(vh.itemView)
                vh
            }
            else -> client.onCreateViewHolder(parent, viewType)
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

    override fun getWrappedPosition(wrapperPosition: Int): Int {
        return if (currentStatus == STATUS_NORMAL) wrapperPosition else -1
    }

    override fun getWrapperAdapterPosition(wrappedPosition: Int): Int {
        return if (currentStatus == STATUS_NORMAL) wrappedPosition else -1
    }

    fun addStatusView(viewType: Int, holder : VH) {
        check(viewType in -2 downTo -100) {"status must not be negative integer"}.run {
            statusViewHolders.put(viewType, holder)
        }
    }

    fun setCurrentStatus(viewType: Int) = statusViewHolders[viewType]?.apply {
        currentStatus = viewType
        notifyDataSetChanged()
    } ?: throw IllegalArgumentException("Invalid viewType: $viewType")

    fun setCurrentStatusIf(status: Int, predicate: (VH) -> Boolean) {
        statusViewHolders[status]?.takeIf {
            predicate.invoke(it)
        }.run {
            currentStatus = this?.let { status } ?: STATUS_NORMAL
            notifyDataSetChanged()
        }

    }
}