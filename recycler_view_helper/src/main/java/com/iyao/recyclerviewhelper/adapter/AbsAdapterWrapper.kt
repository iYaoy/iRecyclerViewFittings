package com.iyao.recyclerviewhelper.adapter

import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


abstract class AbsAdapterWrapper<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), AdapterWrapper {

    companion object {
        val CHANGE_NONE_CONTENT = Any()
    }

    var client: RecyclerView.Adapter<VH> by Delegates.notNull()

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged()
                = notifyDataSetChanged()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?)
                = notifyItemRangeChanged(getWrapperAdapterPosition(positionStart), itemCount, payload)

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int)
                = notifyItemRangeChanged(getWrapperAdapterPosition(positionStart), itemCount)

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int)
                = notifyItemRangeInserted(getWrapperAdapterPosition(positionStart), itemCount)

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int)
                = notifyItemMoved(getWrapperAdapterPosition(fromPosition), getWrapperAdapterPosition(toPosition))

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(getWrapperAdapterPosition(positionStart), itemCount)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        client.onAttachedToRecyclerView(recyclerView)
        runCatching {
            client.registerAdapterDataObserver(observer)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        client.onDetachedFromRecyclerView(recyclerView)
        runCatching {
            client.unregisterAdapterDataObserver(observer)
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        client.onViewAttachedToWindow(holder)

    }

    override fun onViewDetachedFromWindow(holder: VH) {
        client.onViewDetachedFromWindow(holder)

    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        return client.onFailedToRecycleView(holder)
    }

    override fun onViewRecycled(holder: VH) {
        client.onViewRecycled(holder)
    }

    override fun getItemCount() = client.itemCount

    override fun getItemViewType(position: Int): Int {
        return client.getItemViewType(getWrappedPosition(position))
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNotEmpty() && payloads[0] == CHANGE_NONE_CONTENT -> Unit
            payloads.isNotEmpty() -> client.onBindViewHolder(holder, getWrappedPosition(position), payloads)
            else -> onBindViewHolder(holder, position)
        }
    }

    override fun getWrappedAdapter(): RecyclerView.Adapter<VH> = client
}