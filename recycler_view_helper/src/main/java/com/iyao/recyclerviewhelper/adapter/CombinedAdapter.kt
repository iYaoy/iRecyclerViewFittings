package com.iyao.recyclerviewhelper.adapter

import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup

class CombinedAdapter<VH : RecyclerView.ViewHolder>(vararg adapters: RecyclerView.Adapter<VH>) :
        MutableListAdapter<VH, RecyclerView.Adapter<VH>>() {
    private val adapterRange = ArrayList<Point>()
    private var recyclerView: RecyclerView? = null
    private val viewTypeToAdapter = SparseArray<RecyclerView.Adapter<VH>>()
    private val dataObservers = ArrayList<RecyclerView.AdapterDataObserver>()

    init {
        data.addAll(adapters)
        data.fold(0) { acc, adapter ->
            (acc + adapter.itemCount).also { adapterRange.add(Point(acc, it - 1)) }
        }
    }

    override fun getItemCount(): Int {
        return adapterRange.lastOrNull()?.y ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return mappedComponent(position).let {
            viewTypeToAdapter.put(it.second, it.first)
            it.first.getItemViewType(it.second)
        }
    }

    override fun getItemId(position: Int): Long {
        return mappedComponent(position).let { it.first.getItemId(it.second) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return viewTypeToAdapter[viewType].onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val component = mappedComponent(position)
        component.first.onBindViewHolder(holder, component.second)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        val component = mappedComponent(position)
        component.first.onBindViewHolder(holder, component.second, payloads)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        forEach {
            it.onAttachedToRecyclerView(recyclerView)
            val observer = ComponentAdapterDataObserver(it)
            it.registerAdapterDataObserver(observer)
            dataObservers.add(observer)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
        forEachIndexed { index, adapter ->
            adapter.onDetachedFromRecyclerView(recyclerView)
            adapter.unregisterAdapterDataObserver(dataObservers.removeAt(index))
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        val component = mappedComponentByViewHolder(holder)
        component?.first?.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        val component = mappedComponentByViewHolder(holder)
        component?.first?.onViewDetachedFromWindow(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        val component = mappedComponentByViewHolder(holder)
        return component?.first?.onFailedToRecycleView(holder) ?: false
    }

    override fun onViewRecycled(holder: VH) {
        val component = mappedComponentByViewHolder(holder)
        component?.first?.onViewRecycled(holder)
    }

    public fun mappedComponentByViewHolder(holder: VH): Pair<RecyclerView.Adapter<VH>, Int>? {
        val position = recyclerView?.adapter?.getWrappedPosition(this, holder.adapterPosition)
        return position?.let { mappedComponent(it) }
    }

    public fun mappedComponent(position: Int): Pair<RecyclerView.Adapter<VH>, Int> {
        val adapterIndex = adapterRange.indexOfFirst { position <= it.y }
        val start = adapterRange[adapterIndex].x
        return get(adapterIndex) to position - start
    }

    private inner class ComponentAdapterDataObserver(val adapter: RecyclerView.Adapter<VH>) :
            RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            val range = adapterRange[indexOf(adapter)]
            notifyItemRangeChanged(range.x, range.y)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            val range = adapterRange[indexOf(adapter)]
            notifyItemRangeChanged(range.x + positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            val range = adapterRange[indexOf(adapter)]
            notifyItemRangeChanged(range.x + positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val index = indexOf(adapter)
            val range = adapterRange[index]
            range.y += itemCount
            rangeOffsetAfter(index, itemCount)
            notifyItemRangeInserted(range.x + positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val range = adapterRange[indexOf(adapter)]
            notifyItemMoved(range.x + fromPosition, range.x + toPosition)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            val index = indexOf(adapter)
            val range = adapterRange[index]
            range.y -= itemCount
            rangeOffsetAfter(index, -itemCount)
            notifyItemRangeRemoved(range.x + positionStart, itemCount)
        }

        fun rangeOffsetAfter(index: Int, itemCount: Int) {
            (index + 1 until adapterRange.size).forEach {
                adapterRange[it].offset(itemCount, itemCount)
            }
        }
    }
}