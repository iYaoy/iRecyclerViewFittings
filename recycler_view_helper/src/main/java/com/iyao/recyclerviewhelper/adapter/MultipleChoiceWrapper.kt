package com.iyao.recyclerviewhelper.adapter

import android.support.annotation.MainThread
import android.support.v4.util.LongSparseArray
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.ViewGroup
import android.widget.Checkable

open class MultipleChoiceWrapper<VH : RecyclerView.ViewHolder> : AbsAdapterWrapper<VH>() {

    private val checkedIds: LongSparseArray<Int> = LongSparseArray()
    private val checkedStates: SparseBooleanArray = SparseBooleanArray()
    private var checkedCount = 0

    private val observer = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            getWrapperAdapterPosition(positionStart).run {
                (getItemCount() - 1 downTo this + itemCount).forEach {
                    setItemChecked(it, isItemChecked(it - itemCount))
                }.also {
                    (this until this.plus(itemCount)).forEach {
                        setItemChecked(it, false)
                    }
                }
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            getWrapperAdapterPosition(fromPosition).also { positionFrom ->
                getWrapperAdapterPosition(toPosition).also { positionTo ->
                    isItemChecked(positionFrom).run {
                        when {
                            positionFrom < positionTo -> (positionFrom until positionTo).forEach {
                                setItemChecked(it, isItemChecked(it + 1))
                            }
                            else -> (positionFrom downTo positionTo + 1).forEach {
                                setItemChecked(it, isItemChecked(it - 1))
                            }
                        }
                        setItemChecked(positionTo, this)
                    }
                }
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            getWrapperAdapterPosition(positionStart).run {
                (this until getItemCount()).forEach {
                    setItemChecked(it, isItemChecked(it + itemCount))
                }.also {
                    (getItemCount() until getItemCount() + itemCount).forEach {
                        setItemChecked(it, false)
                    }
                }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = client.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNotEmpty() && payloads[0] == MULTIPLE_CHOICE_PAYLOAD -> {
                (holder.itemView as? Checkable)?.isChecked = isItemChecked(position)
            }
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) = client.onBindViewHolder(holder, getWrappedPosition(position)).also {
        (holder.itemView as? Checkable)?.isChecked = isItemChecked(position)
    }

    override fun getWrappedPosition(wrapperPosition: Int) = wrapperPosition

    override fun getWrapperAdapterPosition(wrappedPosition: Int) = wrappedPosition

    @MainThread
    fun setItemChecked(position: Int, checked: Boolean) {
        if (checked != isItemChecked(position)) {
            checkedStates.put(position, checked)
            checkedCount = if (checked) checkedCount + 1 else checkedCount - 1
            notifyItemChanged(position, MULTIPLE_CHOICE_PAYLOAD)
            if (hasStableIds()) {
                getItemId(position).let {
                    checked.run {
                        when {
                            this -> checkedIds.put(it, position)
                            else -> checkedIds.delete(it)
                        }
                    }
                }
            }
        } else Unit
    }

    fun isItemChecked(position: Int) = checkedStates[position]

    fun getItemCheckedCount() = checkedCount

    fun getCheckedItemIds() = LongArray(getItemCheckedCount()) { position -> checkedIds.keyAt(position) }

    @MainThread
    fun clearChoices() = {
        checkedIds.takeIf { hasStableIds() }?.apply {
            (size() - 1 downTo 0).forEach { it ->
                valueAt(it)?.run {
                    setItemChecked(this, false)
                }
            }
        } ?: (0 until checkedStates.size()).forEach {
            setItemChecked(checkedStates.keyAt(it), false)
        }
        checkedStates.clear()
    }

    private companion object {
        const val MULTIPLE_CHOICE_PAYLOAD = "multiple_choice"
    }
}