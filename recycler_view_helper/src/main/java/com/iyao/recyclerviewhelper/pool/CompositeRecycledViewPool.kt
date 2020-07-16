package com.iyao.recyclerviewhelper.pool

import androidx.recyclerview.widget.RecyclerView

internal class CompositeRecycledViewPool(internal var value: RecyclerView.RecycledViewPool, internal var next: RecyclerView.RecycledViewPool): RecyclerView.RecycledViewPool() {

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        return value.getRecycledView(viewType) ?: next.getRecycledView(viewType)
    }

    override fun putRecycledView(scrap: RecyclerView.ViewHolder?) {
        value.putRecycledView(scrap)
    }
}

operator fun RecyclerView.RecycledViewPool.plus(other: RecyclerView.RecycledViewPool): RecyclerView.RecycledViewPool {
    if (this == other) {
        return this
    }
    var tail: RecyclerView.RecycledViewPool = this
    var parent: CompositeRecycledViewPool? = null
    while (tail is CompositeRecycledViewPool) {
        parent = tail
        //already contained, ignore
        if (tail.value == other || tail.next == other) {
            return this
        }
        tail = tail.next
    }
    return when (parent) {
        null -> CompositeRecycledViewPool(tail, other)
        else -> {
            parent.next = CompositeRecycledViewPool(tail, other)
            this
        }
    }
}


operator fun RecyclerView.RecycledViewPool.minus(other: RecyclerView.RecycledViewPool?): RecyclerView.RecycledViewPool {
    if (this == other) {
        return RecyclerView.RecycledViewPool()
    }
    if (this !is CompositeRecycledViewPool) {
        return this
    }
    var cur: RecyclerView.RecycledViewPool = this
    var parent : CompositeRecycledViewPool? = null
    var result: RecyclerView.RecycledViewPool = this
    loop@ while (cur is CompositeRecycledViewPool) {
        val value = cur.value
        val next = cur.next
        when {
            value == other -> {
                if (parent == null) {
                    result = next
                } else {
                    parent.next = next
                }
                break@loop
            }
            next == other -> {
                if (parent == null) {
                    result = value
                } else {
                    parent.next = value
                }
                break@loop
            }
            else -> {
                parent = cur
                cur = next
            }
        }
    }
    return result
}