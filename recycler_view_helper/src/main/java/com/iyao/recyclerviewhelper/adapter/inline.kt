package com.iyao.recyclerviewhelper.adapter

import android.view.View
import android.widget.Checkable
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import java.lang.NullPointerException

/*********************Adapter***************************/

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withSingleChoice(checkableId: Int):  SingleChoiceWrapper<VH> {
    return withWrapper(SingleChoiceWrapper(checkableId))
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withMultiChoice(checkableId: Int):  MultipleChoiceWrapper<VH> {
    return withWrapper(MultipleChoiceWrapper(checkableId))
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withStatusViews(vararg statusViews: Pair<Int, VH>):  StatusWrapper<VH> {
    val statusWrapper = StatusWrapper<VH>()
    statusViews.forEach {
        statusWrapper.addStatusView(it.first, it.second)
    }
    return withWrapper(statusWrapper)
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withHeaderAndFooter(headers: Array<Pair<Int, VH>> = arrayOf(),
                                                                               footers: Array<Pair<Int, VH>> = arrayOf()): HeaderAndFooterWrapper<VH> {
    val headerAndFooterWrapper = HeaderAndFooterWrapper<VH>()
    headers.forEach {
        headerAndFooterWrapper.addHeader(it.first, it.second)
    }
    footers.forEach {
        headerAndFooterWrapper.addFooter(it.first, it.second)
    }
    return withWrapper(headerAndFooterWrapper)
}


private fun <VH: RecyclerView.ViewHolder, Adapter: AbsAdapterWrapper<VH>> RecyclerView.Adapter<VH>.withWrapper(wrapper: Adapter): Adapter {
    return wrapper.also { it.client = this }
}

fun RecyclerView.Adapter<*>.getStatusWrapper(): CachedStatusWrapper {
    return takeIsInstance() ?: throw NullPointerException("there is no CachedStatusWrapper")
}

fun RecyclerView.Adapter<*>.getMultiWrapper(): CachedMultipleChoiceWrapper {
    return takeIsInstance() ?: throw NullPointerException("there is no CachedMultipleChoiceWrapper")
}

fun RecyclerView.Adapter<*>.getHeaderAndFooterWrapper(): CachedHeaderAndFooterWrapper {
    return takeIsInstance() ?: throw NullPointerException("there is no CachedHeaderAndFooterWrapper")
}

fun <E> RecyclerView.Adapter<*>.getAutoRefreshAdapter(): AutoRefreshAdapter<CacheViewHolder, E> {
    return takeIsInstance() ?: throw NullPointerException("there is no AutoRefreshAdapter")
}

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

/**********************ViewHolder************************/
inline fun <reified V : View> RecyclerView.ViewHolder.childView(@IdRes id: Int): V? {
    return itemView.findViewById(id)
}


open class CacheViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

typealias CachedAutoRefreshAdapter<E> = AutoRefreshAdapter<CacheViewHolder, E>
typealias CachedStatusWrapper = StatusWrapper<CacheViewHolder>
typealias CachedHeaderAndFooterWrapper = HeaderAndFooterWrapper<CacheViewHolder>
typealias CachedMultipleChoiceWrapper = MultipleChoiceWrapper<CacheViewHolder>
