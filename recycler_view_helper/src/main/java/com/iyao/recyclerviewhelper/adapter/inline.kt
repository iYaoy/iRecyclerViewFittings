package com.iyao.recyclerviewhelper.adapter

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/*********************Adapter***************************/

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withSingleChoice(checkableId: Int):  SingleChoiceWrapper<VH> {
    return if (this is SingleChoiceWrapper<VH>) this else withWrapper(SingleChoiceWrapper(checkableId))
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withMultiChoice(checkableId: Int):  MultipleChoiceWrapper<VH> {
    return if (this is MultipleChoiceWrapper<VH>) this else withWrapper(MultipleChoiceWrapper(checkableId))
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withStatusView(statusView: Pair<Int, VH>):  StatusWrapper<VH> {
    val statusWrapper = if (this is StatusWrapper<VH>) this else withWrapper<VH, StatusWrapper<VH>>(StatusWrapper())
    statusWrapper.addStatusView(statusView.first, statusView.second)
    return statusWrapper
}

fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withHeader(header: Pair<Int, VH>): HeaderAndFooterWrapper<VH> {
    val headerAndFooterWrapper = if (this is HeaderAndFooterWrapper<VH>) this else withWrapper<VH,HeaderAndFooterWrapper<VH>>(HeaderAndFooterWrapper())
    headerAndFooterWrapper.addHeader(header.first, header.second)
    return headerAndFooterWrapper
}


fun <VH: RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.withFooter(footer: Pair<Int, VH>): HeaderAndFooterWrapper<VH> {
    val headerAndFooterWrapper = if (this is HeaderAndFooterWrapper<VH>) this else withWrapper<VH,HeaderAndFooterWrapper<VH>>(HeaderAndFooterWrapper())
    headerAndFooterWrapper.addFooter(footer.first, footer.second)
    return headerAndFooterWrapper
}

fun <VH: RecyclerView.ViewHolder, Adapter: AbsAdapterWrapper<VH>> RecyclerView.Adapter<VH>.withWrapper(wrapper: Adapter): Adapter {
    return wrapper.also { it.client = this }
}

fun RecyclerView.getStatusWrapper(index: Int = 0): CachedStatusWrapper {
    return takeAdapterInstance(index)
}

fun RecyclerView.getSingleWrapper(index: Int = 0): CachedSingleChoiceWrapper {
    return takeAdapterInstance(index)
}

fun RecyclerView.getMultiWrapper(index: Int = 0): CachedMultipleChoiceWrapper {
    return takeAdapterInstance(index)
}

fun RecyclerView.getHeaderFooterWrapper(index: Int = 0): CachedHeaderAndFooterWrapper {
    return takeAdapterInstance(index)
}

inline fun <reified E> RecyclerView.getAutoRefreshAdapter(index: Int = 0): AutoRefreshAdapter<CacheViewHolder, E> {
    return takeAdapterInstance(index)
}

fun RecyclerView.requireAdapter(): RecyclerView.Adapter<*> {
    return adapter ?: throw IllegalStateException("adapter not attach")
}

inline fun <reified R> RecyclerView.takeAdapterInstance(index: Int = 0): R {
    return adapter?.takeInstance<R>(index) ?: throw IllegalStateException("no adapter attached")
}

inline fun <reified R> RecyclerView.Adapter<*>.takeInstance(index: Int = 0): R {
    var adapter : RecyclerView.Adapter<*>? = this
    var count = -1
    while (index > count && adapter is AbsAdapterWrapper<*>) {
        if (adapter is R && ++count == index) {
            break
        }
        adapter = adapter.getWrappedAdapter()
    }
    return adapter as? R ?: throw IllegalStateException("there are less than ${index+1} ${R::class} instance")
}

tailrec fun RecyclerView.Adapter<*>.getWrappedPosition(wrappedAdapter: RecyclerView.Adapter<*>, outerPosition: Int): Int {
    return when {
        wrappedAdapter == this -> outerPosition
        this is AdapterWrapper -> getWrappedAdapter().getWrappedPosition(wrappedAdapter, getWrappedPosition(outerPosition))
        else -> -1
    }
}

fun RecyclerView.Adapter<*>.getPositionOrNull(wrappedAdapter: RecyclerView.Adapter<*>, outerPosition: Int): Int? {
    return getWrappedPosition(wrappedAdapter, outerPosition).takeIf { it >= 0 }
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
typealias CachedSingleChoiceWrapper = SingleChoiceWrapper<CacheViewHolder>
