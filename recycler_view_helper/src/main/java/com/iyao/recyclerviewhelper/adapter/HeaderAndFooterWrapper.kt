package com.iyao.recyclerviewhelper.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup


open class HeaderAndFooterWrapper<VH : RecyclerView.ViewHolder> : AbsAdapterWrapper<VH>() {


    private val headers : SparseArray<VH> = SparseArray()
    private val footers : SparseArray<VH> = SparseArray()

    override fun getItemCount() = headers.size() + super.getItemCount() + footers.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when {
            headers.indexOfKey(viewType) >= 0 -> headers[viewType]
            footers.indexOfKey(viewType) >= 0 -> footers[viewType]
            else -> client.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position in headers.size() until itemCount - footers.size()) {
            client.onBindViewHolder(holder, getWrappedPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            in 0 until headers.size() -> headers.keyAt(position)
            in headers.size() until itemCount - footers.size() -> client.getItemViewType(getWrappedPosition(position))
            else -> footers.keyAt(position - headers.size() - client.itemCount)
        }
    }

    override fun getWrappedPosition(wrapperPosition: Int) = wrapperPosition.minus(headers.size())

    override fun getWrapperAdapterPosition(wrappedPosition: Int) = wrappedPosition.plus(headers.size())

    fun addHeader(viewType: Int, holder: VH) = headers.put(viewType, holder)

    fun addFooter(viewType: Int, holder: VH) = footers.put(viewType, holder)

    fun removeHeader(viewType: Int) = headers.indexOfKey(viewType).run {
        headers.removeAt(this)
        notifyItemRemoved(this)
    }

    fun removeFooter(viewType: Int) = headers.indexOfKey(viewType).run {
        footers.removeAt(this)
        notifyItemRemoved(headers.size().plus(client.itemCount).plus(this))
    }
}