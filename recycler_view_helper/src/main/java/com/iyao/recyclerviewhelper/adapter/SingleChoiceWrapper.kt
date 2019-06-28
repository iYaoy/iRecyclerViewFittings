package com.iyao.recyclerviewhelper.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView

class SingleChoiceWrapper<VH: RecyclerView.ViewHolder>(@IdRes private val checkableId: Int = 0): AbsAdapterWrapper<VH>() {

    var checkedPosition = -1
    private set(value) {
        field = value
        onCheckedChangeListener?.invoke(value)
    }

    private var onCheckedChangeListener: ((Int)->Unit)? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val position = getWrapperAdapterPosition(positionStart)
            if (position < checkedPosition) {
                setItemChecked(checkedPosition + itemCount, true)
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val positionFrom = getWrapperAdapterPosition(fromPosition)
            val positionTo = getWrapperAdapterPosition(toPosition)
            if (checkedPosition == positionFrom) {
                setItemChecked(positionFrom, true)
            } else if (positionFrom < positionTo && checkedPosition in positionFrom.. positionTo) {
                setItemChecked(checkedPosition-1, true)
            } else if (positionTo < positionFrom && checkedPosition in positionTo.. positionFrom) {
                setItemChecked(checkedPosition+1, true)
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            val position = getWrapperAdapterPosition(positionStart)
            if (checkedPosition in position until position + itemCount) {
                setItemChecked(-1, true)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        client.registerAdapterDataObserver(observer)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        client.unregisterAdapterDataObserver(observer)
    }

    override fun getItemId(position: Int) = client.getItemId(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = client.onCreateViewHolder(parent, viewType)
        viewHolder.itemView.findViewById<CompoundButton>(checkableId)?.setOnCheckedChangeListener { _, isChecked ->
            val adapter = (parent as RecyclerView).adapter ?: return@setOnCheckedChangeListener
            adapter.getWrappedPosition(this, viewHolder.adapterPosition).takeIf { it in 0 until itemCount }?.run {
                setItemChecked(this, isChecked)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNotEmpty() && payloads[0] == SINGLE_CHOICE_PAYLOAD -> {
                holder.getCheckable()?.isChecked = isItemChecked(position)
            }
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        client.onBindViewHolder(holder, getWrappedPosition(position))
        holder.getCheckable()?.isChecked = isItemChecked(position)
    }

    override fun getWrappedPosition(wrapperPosition: Int) = wrapperPosition

    override fun getWrapperAdapterPosition(wrappedPosition: Int) = wrappedPosition

    fun setOnCheckedChangeListener(onCheckedChangeListener: ((Int)->Unit)?) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }

    @MainThread
    fun setItemChecked(position: Int, checked: Boolean) {
        if (checked != isItemChecked(position)) {
            val oldPosition = checkedPosition
            val newPosition = if (oldPosition == position && !checked) -1 else position
            checkedPosition = newPosition
            notifyItemChanged(oldPosition, SINGLE_CHOICE_PAYLOAD)
            if (newPosition in 0 until itemCount) {
                notifyItemChanged(newPosition, SINGLE_CHOICE_PAYLOAD)
            }
        }
    }

    fun isItemChecked(position: Int) = checkedPosition == position

    fun clearChoice() {
        setItemChecked(-1, true)
    }

    private fun RecyclerView.ViewHolder.getCheckable(): Checkable? {
        return itemView.findViewById<View>(checkableId) as? Checkable
    }

    private companion object {
        const val SINGLE_CHOICE_PAYLOAD = "single_choice"
    }
}