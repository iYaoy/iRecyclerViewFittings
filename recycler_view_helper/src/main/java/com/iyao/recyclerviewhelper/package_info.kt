package com.iyao.recyclerviewhelper

import android.support.v7.widget.RecyclerView
import com.iyao.recyclerviewhelper.adapter.AbsAdapterWrapper
import com.iyao.recyclerviewhelper.adapter.AdapterWrapper

inline fun <reified R : Any> RecyclerView.Adapter<*>.takeIsInstance(): R?  {
    var adapter : RecyclerView.Adapter<*>? = this
    while (adapter !is R && adapter is AbsAdapterWrapper<*>) {
        adapter = adapter.getWrappedAdapter()
    }
    return adapter as? R?
}
tailrec fun RecyclerView.Adapter<*>.getWrappedPosition(wrappedAdapter: RecyclerView.Adapter<*>, outerPosition: Int): Int {
    return when {
        wrappedAdapter == this -> outerPosition
        this is AdapterWrapper -> getWrappedAdapter().getWrappedPosition(wrappedAdapter, getWrappedPosition(outerPosition))
        else -> -1
    }
}