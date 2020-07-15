package com.iyao.recyclerviewhelper.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iyao.recyclerviewhelper.pool.minus
import com.iyao.recyclerviewhelper.pool.plus


open class HeaderAndFooterWrapper<VH : RecyclerView.ViewHolder> : AbsAdapterWrapper<VH>() {

    var recycledViewPool = object : RecyclerView.RecycledViewPool() {
        override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
            return getFooterByViewType(viewType) ?: getHeaderByViewType(viewType)
        }
    }

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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setRecycledViewPool(recyclerView.recycledViewPool + recycledViewPool)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.setRecycledViewPool(recyclerView.recycledViewPool - recycledViewPool)
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

    private fun startFooterPosition() = headers.size().plus(client.itemCount)

    fun addHeader(viewType: Int, holder: VH) {
        val old = getHeaderByViewType(viewType)
        if (old != holder) {
            headers.putInternal(viewType, holder)
        }
        val position = getHeaderPosition(viewType)
        if (old == null) {
            //新增
            notifyItemInserted(position)
        } else {
            notifyItemChanged(position)
        }
    }


    fun addFooter(viewType: Int, holder: VH) {
        val old = getFooterByViewType(viewType)
        if (old != holder) {
            footers.putInternal(viewType, holder)
        }
        val position = getFooterPosition(viewType)
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
        val (fromPosition, holder) = when {
            isHeader(fromViewType) -> removeInternal(fromViewType, true)
            isFooter(fromViewType) -> removeInternal(fromViewType, false)
            else -> return false
        }
        if (holder == null || fromPosition < 0) {
            return false
        }
        val toPosition = if (toHeader) {
            headers.putInternal(toViewType, holder)
            getHeaderPosition(toViewType)
        } else {
            footers.putInternal(toViewType, holder)
            getFooterPosition(toViewType)
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun move(holder: VH, toViewType: Int, toHeader: Boolean): Boolean {
        val (fromPosition, viewType) = when {
            isHeader(holder) -> removeInternal(holder, true)
            isFooter(holder) -> removeInternal(holder, false)
            else -> return false
        }
        if (viewType == null || fromPosition < 0) {
            return false
        }
        val toPosition = if (toHeader) {
            headers.putInternal(toViewType, holder)
            getHeaderPosition(toViewType)
        } else {
            footers.putInternal(toViewType, holder)
            getFooterPosition(toViewType)
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun remove(viewType: Int): VH? {
        return if (isHeader(viewType)) {
            removeHeader(viewType)
        } else {
            removeFooter(viewType)
        }
    }

    fun removeHeader(viewType: Int): VH? {
        val (position, holder) = removeInternal(viewType, true)
        if (position >= 0) {
            notifyItemRemoved(position)
        }
        return holder
    }

    fun removeHeader(holder: VH): Int? {
        val (position, viewType) = removeInternal(holder, true)
        if (position >= 0) {
            notifyItemRemoved(position)
        }
        return viewType
    }



    fun removeFooter(viewType: Int): VH? {
        val (position, holder) = removeInternal(viewType, false)
        if (position >= 0) {
            notifyItemRemoved(position)
        }
        return holder
    }

    fun removeFooter(holder: VH): Int? {
        val (position, viewType) = removeInternal(holder, false)
        if (position >= 0) {
            notifyItemRemoved(position)
        }
        return viewType
    }

    private fun removeInternal(viewType: Int, isHeader: Boolean): Pair<Int, VH?> {
        val container = if (isHeader) headers else footers
        val index = container.indexOfKey(viewType)
        val holder = container.safeValueAt(index)
        container.safeRemoveAt(index)
        val position = when {
            index < 0 -> -1
            isHeader -> index
            else -> index + startFooterPosition()
        }
        return position to holder
    }

    private fun removeInternal(holder: VH, isHeader: Boolean): Pair<Int, Int?> {
        val container = if (isHeader) headers else footers
        val index = container.indexOfValue(holder)
        val viewType = container.safeKeyAt(index)
        container.safeRemoveAt(index)
        val position = when {
            index < 0 -> -1
            isHeader -> index
            else -> index + startFooterPosition()
        }
        return position to viewType
    }

    fun clearHeaders() {
        val size = headers.size()
        headers.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun clearFooters() {
        val size = footers.size()
        footers.clear()
        notifyItemRangeRemoved(startFooterPosition(), size)
    }

    fun clearAll() {
        clearHeaders()
        clearFooters()
    }

    fun getHeaderPosition(viewType: Int) = headers.indexOfKey(viewType)

    fun getHeaderPosition(holder: VH) = headers.indexOfValue(holder)

    fun getHeaderByPosition(position: Int) = headers.valueAt(position)

    fun getHeaderByViewType(viewType: Int): VH? = headers[viewType]

    fun getHeaderViewTypeByPosition(position: Int) = headers.keyAt(position)

    fun getFooterPosition(viewType: Int) = footers.indexOfKey(viewType) + startFooterPosition()

    fun getFooterPosition(holder: VH) = footers.indexOfValue(holder) + startFooterPosition()

    fun getFooterByPosition(position: Int) = headers.valueAt(position - startFooterPosition())

    fun getFooterByViewType(viewType: Int): VH? = footers[viewType]

    fun getFooterViewTypeByPosition(position: Int) = headers.keyAt(position - startFooterPosition())

    fun isHeader(viewType: Int) = headers.indexOfKey(viewType) >= 0

    fun isHeader(holder: VH) = headers.indexOfValue(holder)>=0

    fun isFooter(viewType: Int) = footers.indexOfKey(viewType) >= 0

    fun isFooter(holder: VH) = footers.indexOfValue(holder)>=0

    private fun SparseArray<VH>.putInternal(viewType: Int, holder: VH) {
        put(viewType, holder)
    }

    private fun SparseArray<VH>.safeKeyAt(index: Int): Int? {
        return if (index in 0 until size()) keyAt(index) else null
    }

    private fun SparseArray<VH>.safeValueAt(index: Int): VH? {
        return if (index in 0 until size()) valueAt(index) else null
    }

    private fun SparseArray<VH>.safeRemoveAt(index: Int) {
        if (index in 0 until size()) removeAt(index)
    }
}