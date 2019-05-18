package com.iyao.recyclerviewhelper.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


abstract class AutoRefreshAdapter<VH : RecyclerView.ViewHolder, E> : MutableListAdapter<VH, E>() {

    fun refresh(elements: Collection<E>, callback : DiffUtil.Callback?): Boolean {
        return if (callback != null) DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return callback.areItemsTheSame(oldItemPosition, newItemPosition)
            }

            override fun getOldListSize(): Int {
                return size
            }

            override fun getNewListSize(): Int {
                return elements.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return callback.areContentsTheSame(oldItemPosition, newItemPosition)
            }
        }).run {
            dispatchUpdatesTo(this@AutoRefreshAdapter)
            super.clear().let {
                super.addAll(elements)
            }
        } else clear().let {
            addAll(elements)
        }
    }

    override fun set(index: Int, element: E): E {
        return super.set(index, element).also {
            notifyItemChanged(index, element)
        }
    }

    override fun add(element: E): Boolean {
        return super.add(element).also {
            if (it) notifyItemInserted(size - 1)
        }
    }

    override fun add(index: Int, element: E) {
        super.add(index, element)
        notifyItemInserted(index)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return size.let {
            super.addAll(elements).also {
                add -> if (add) notifyItemRangeInserted(it, elements.size)
            }
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return super.addAll(index, elements).also {
            if (it) notifyItemRangeInserted(index, elements.size)
        }
    }

    override fun clear() {
        size.also {
            super.clear().run {
                notifyItemRangeRemoved(0, it)
            }
        }
    }

    override fun remove(element: E): Boolean {
        return indexOf(element).let {
            super.remove(element).also {
                remove -> if (remove) notifyItemRemoved(it)
            }
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return elements.map {
            remove(it)
        }.any {
            it
        }
    }

    override fun removeAt(index: Int): E {
        return super.removeAt(index).also {
            if (it != null) notifyItemRemoved(index)
        }
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return data.filter {
            !elements.contains(it)
        }.map {
            remove(it)
        }.any {
            it
        }
    }

    override fun toString() = data.toString()
}
