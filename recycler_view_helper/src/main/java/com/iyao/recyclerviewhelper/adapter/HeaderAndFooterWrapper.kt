package com.iyao.recyclerviewhelper.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


open class HeaderAndFooterWrapper<VH : RecyclerView.ViewHolder> : AbsAdapterWrapper<VH>() {


    private val headers : SparseArray<VH> = SparseArray()
    private val footers : SparseArray<VH> = SparseArray()

    override fun getItemCount() = headers.size() + super.getItemCount() + footers.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when {
            headers.indexOfKey(viewType) >= 0 -> {
                val vh = headers[viewType]
                (vh.itemView.parent as? ViewGroup)?.removeView(vh.itemView)
                vh
            }
            footers.indexOfKey(viewType) >= 0 -> {
                val vh = footers[viewType]
                (vh.itemView.parent as? ViewGroup)?.removeView(vh.itemView)
                vh
            }
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

    fun addHeader(viewType: Int, holder: VH) {
        val old = headers[viewType]
        if (old != holder) {
            headers.put(viewType, holder)
        }
        val position = headers.indexOfKey(viewType)
        if (old == null) {
            //新增
            notifyItemInserted(position)
        } else {
            notifyItemChanged(position)
        }
    }


    fun addFooter(viewType: Int, holder: VH) {
        val old = footers[viewType]
        if (old != holder) {
            footers.put(viewType, holder)
        }
        val position = footers.indexOfKey(viewType) + headers.size() + client.itemCount
        if (old == null) {
            notifyItemInserted(position)
        } else {
            notifyItemChanged(position)
        }
    }

    /**
     * 移动Header或Footer到指定位置
     */
    fun move(fromViewType: Int, toViewType: Int, toHeader: Boolean): Boolean {
        val old = removeHeader(fromViewType) ?: removeFooter(fromViewType) ?: return false
        if (toHeader) {
            addHeader(toViewType, old)
        } else {
            addFooter(toViewType, old)
        }
        return true
    }

    fun move(holder: VH, toViewType: Int, toHeader: Boolean): Boolean {
        removeHeader(holder) ?: removeFooter(holder) ?: return false
        if (toHeader) {
            addHeader(toViewType, holder)
        } else {
            addFooter(toViewType, holder)
        }
        return true
    }

    fun removeHeader(viewType: Int): VH? {
        val position = headers.indexOfKey(viewType).takeIf { it in 0 until headers.size() } ?: return null
        val holder = headers.valueAt(position)
        headers.removeAt(position)
        notifyItemRemoved(position)
        return holder
    }

    fun removeHeader(holder: VH): Int? {
        val position = headers.indexOfValue(holder).takeIf { it in 0 until headers.size() } ?: return null
        val viewType = headers.keyAt(position)
        headers.removeAt(position)
        notifyItemRemoved(position)
        return viewType
    }

    fun removeFooter(viewType: Int): VH? {
        val position = footers.indexOfKey(viewType).takeIf { it in 0 until footers.size() } ?: return null
        val holder = footers.valueAt(position)
        footers.removeAt(position)
        notifyItemRemoved(headers.size().plus(client.itemCount).plus(position))
        return holder
    }

    fun removeFooter(holder: VH): Int? {
        val position = footers.indexOfValue(holder).takeIf { it in 0 until footers.size() } ?: return null
        val viewType = footers.keyAt(position)
        footers.removeAt(position)
        notifyItemRemoved(headers.size().plus(client.itemCount).plus(position))
        return viewType
    }

    fun clearHeaders() {
        val itemCount = headers.size()
        headers.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    fun clearFooters() {
        val itemCount = footers.size()
        footers.clear()
        notifyItemRangeRemoved(itemCount-itemCount, itemCount)
    }

    fun clearAll() {
        clearHeaders()
        clearFooters()
    }
}