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
    var tail: RecyclerView.RecycledViewPool = this
    var parent: CompositeRecycledViewPool? = null
    while (tail is CompositeRecycledViewPool) {
        parent = tail
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


operator fun RecyclerView.RecycledViewPool.minus(extra: RecyclerView.RecycledViewPool?): RecyclerView.RecycledViewPool {
    if (this !is CompositeRecycledViewPool) {
        return this
    }
    var cur: RecyclerView.RecycledViewPool = this
    var parent : CompositeRecycledViewPool? = null
    var result: RecyclerView.RecycledViewPool = this
    loop@ while (cur is CompositeRecycledViewPool) {
        when {
            cur.value === extra -> {
                if (parent == null) {
                    result = cur.next
                } else {
                    parent.next = cur.next
                }
                break@loop
            }
            else -> {
                parent = cur
                cur = cur.next
            }
        }
    }
    return result
}