package com.iyao.recyclerviewhelper.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

open class SingleChoiceWrapper<VH: RecyclerView.ViewHolder>(@IdRes private val checkableId: Int = 0): AbsAdapterWrapper<VH>() {

    private companion object {
        const val SINGLE_CHOICE_PAYLOAD = "single_choice"
    }

    /**
     * 当前选中的Item Position
     */
    var checkedPosition = RecyclerView.NO_POSITION
    private set(value) {
        field = value
        onCheckedChangeListener?.invoke(value)
    }

    var recyclerView: WeakReference<RecyclerView?> = WeakReference(null)

    /**
     * 是否允许取消选中
     */
    var isCheckedCancelable: Boolean = true

    private var mPreviousCheckedPosition: Int = RecyclerView.NO_POSITION

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
                setItemChecked(RecyclerView.NO_POSITION, true)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = WeakReference(recyclerView)
        runCatching {
            client.registerAdapterDataObserver(observer)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        runCatching {
            client.unregisterAdapterDataObserver(observer)
        }
        this.recyclerView = WeakReference(null)
    }

    override fun getItemId(position: Int) = client.getItemId(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = client.onCreateViewHolder(parent, viewType)
        (viewHolder.itemView.findViewById(checkableId) as? CompoundButton)?.setOnCheckedChangeListener { view, isChecked ->
            if (!isCheckedCancelable && !isChecked) {
                view.toggle()
                return@setOnCheckedChangeListener
            }
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
                invalidateCheckableState(position, holder)
            }
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        client.onBindViewHolder(holder, getWrappedPosition(position))
        invalidateCheckableState(position, holder)
    }

    override fun getWrappedPosition(wrapperPosition: Int) = wrapperPosition

    override fun getWrapperAdapterPosition(wrappedPosition: Int) = wrappedPosition

    fun setOnCheckedChangeListener(onCheckedChangeListener: ((Int)->Unit)?) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }

    @MainThread
    fun setItemChecked(position: Int) {
        setItemChecked(position, true)
    }

    @MainThread
    fun setItemChecked(position: Int, checked: Boolean) {
        if ((checked || isCheckedCancelable) && checked != isItemChecked(position)) {
            val oldPosition = checkedPosition
            val newPosition = if (oldPosition == position && !checked) RecyclerView.NO_POSITION else position
            checkedPosition = newPosition
            mPreviousCheckedPosition = oldPosition
            if (recyclerView.get()?.isComputingLayout == true) return
            if (oldPosition in 0 until itemCount) {
                notifyItemChanged(oldPosition, SINGLE_CHOICE_PAYLOAD)
            }
            if (newPosition in 0 until itemCount) {
                notifyItemChanged(newPosition, SINGLE_CHOICE_PAYLOAD)
            }
        }
    }

    fun isItemChecked(position: Int) = checkedPosition == position

    fun clearChoice() {
        setItemChecked(RecyclerView.NO_POSITION, true)
    }

    private fun invalidateCheckableState(position: Int, holder: VH) {
        val itemChecked = isItemChecked(position)
        val checkable = holder.getCheckable()
        if (checkable != null) {
            checkable.isChecked = itemChecked
        }
    }

    private fun RecyclerView.ViewHolder.getCheckable(): Checkable? {
        return itemView.findViewById<View>(checkableId) as? Checkable
    }
}