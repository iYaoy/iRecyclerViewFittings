package com.iyao.recyclerviewhelper.adapter

import android.support.v7.widget.RecyclerView


abstract class AbsAdapterWrapper<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), AdapterWrapper {

    companion object {
        val CHANGE_NONE_CONTENT = Any()
    }

    lateinit var client: RecyclerView.Adapter<VH>
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
        check(::client.isInitialized) {"client is null ： ${javaClass.simpleName}"}
        client.onAttachedToRecyclerView(recyclerView)
        client.registerAdapterDataObserver(observer)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        client.onDetachedFromRecyclerView(recyclerView)
        client.unregisterAdapterDataObserver(observer)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        check(::client.isInitialized) {"client is null : ${javaClass.simpleName}"}
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

    override fun getItemCount() = if (::client.isInitialized) client.itemCount else 0

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