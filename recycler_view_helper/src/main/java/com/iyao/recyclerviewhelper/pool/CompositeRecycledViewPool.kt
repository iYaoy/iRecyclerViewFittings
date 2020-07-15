package com.iyao.recyclerviewhelper.pool

import androidx.recyclerview.widget.RecyclerView

class CompositeRecycledViewPool(val core: RecyclerView.RecycledViewPool, var extra: RecyclerView.RecycledViewPool?): RecyclerView.RecycledViewPool() {

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        return core.getRecycledView(viewType) ?: extra?.getRecycledView(viewType)
    }

    override fun putRecycledView(scrap: RecyclerView.ViewHolder?) {
        core.putRecycledView(scrap)
    }
}

operator fun RecyclerView.RecycledViewPool.plus(other: RecyclerView.RecycledViewPool): RecyclerView.RecycledViewPool {
    return CompositeRecycledViewPool(this, other)
}


operator fun RecyclerView.RecycledViewPool.minus(extra: RecyclerView.RecycledViewPool?): RecyclerView.RecycledViewPool {
    var pool = this
    while (pool is CompositeRecycledViewPool) {
        if (pool.extra === extra) {
            pool.extra = null
        } else {
            pool = pool.core
        }
    }
    return this
}