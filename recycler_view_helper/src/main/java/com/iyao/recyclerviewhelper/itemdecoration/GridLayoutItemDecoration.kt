package com.iyao.recyclerviewhelper.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iyao.recyclerviewhelper.adapter.AbsAdapterWrapper.Companion.CHANGE_NONE_CONTENT
import kotlin.math.min

open class GridLayoutItemDecoration : RecyclerView.ItemDecoration() {
    private companion object {
        const val KEY_OFFSET = 1
        const val KEY_FRLL = 2
        const val KEY_REGISTER_OBSERVER = 3
    }
    var startAndEndDecoration = 0
    var topAndBottomDecoration = 0
    var horizontalMiddleDecoration = 0
    var verticalMiddleDecoration = 0

    var decorateFullItem = false



    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {


        (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition.also {
            (parent.layoutManager as? GridLayoutManager)?.run {
                when(spanCount) {
                    1 -> outRect.apply {
                        left = startAndEndDecoration
                        top = topAndBottomDecoration
                        right = startAndEndDecoration
                        bottom = topAndBottomDecoration
                    }
                    else -> {
                        outRect.apply {
                            registerObserverIfNeed(state, parent)
                            val spanIndex = spanSizeLookup.getSpanIndex(it, spanCount)
                            val groupIndex = spanSizeLookup.getSpanGroupIndex(it, spanCount)
                            var deltaOffset = state.get<Float>(KEY_OFFSET)
                            if (deltaOffset == null || deltaOffset == 0f) {
                                val budgetSpace = (spanCount - 1) * horizontalMiddleDecoration + startAndEndDecoration * 2
                                val firstRightAndLastLeft = budgetSpace / spanCount - startAndEndDecoration
                                deltaOffset = ((startAndEndDecoration - firstRightAndLastLeft) / (spanCount - 1)).toFloat()
                                state.put(KEY_OFFSET, deltaOffset)
                                state.put(KEY_FRLL, firstRightAndLastLeft)
                            }
                            left = when {
                                !decorateFullItem && spanSizeLookup.getSpanSize(it) == spanCount -> 0
                                spanIndex == 0 -> startAndEndDecoration
                                else -> startAndEndDecoration - (spanIndex * deltaOffset).toInt()
                            }
                            top = if (groupIndex == 0) topAndBottomDecoration else 0
                            right = when{
                                !decorateFullItem && spanSizeLookup.getSpanSize(it) == spanCount -> 0
                                spanSizeLookup.getSpanSize(it) == spanCount || spanIndex == spanCount - 1 -> startAndEndDecoration
                                else -> state.get<Int>(KEY_FRLL) + (spanIndex * deltaOffset).toInt()
                            }
                            bottom = if (groupIndex == spanSizeLookup.getSpanGroupIndex(parent.adapter?.itemCount?:0 - 1, spanCount)) topAndBottomDecoration else verticalMiddleDecoration
                        }
                    }

                }
            }
        }
    }

    /**
     * note: only one observer will be register to the same RecyclerView, no matter what.
     * So, be careful with it.
     */
    private fun registerObserverIfNeed(state: RecyclerView.State, parent: RecyclerView) {
        if (state.get<Boolean?>(KEY_REGISTER_OBSERVER) != true) {
            state.put(KEY_REGISTER_OBSERVER, true)
            parent.adapter?.apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        val startPosition = positionStart + itemCount - 1
                        notifyItemRangeChanged(startPosition, getItemCount() - startPosition, CHANGE_NONE_CONTENT)
                    }

                    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                        notifyItemRangeChanged(min(fromPosition, toPosition), itemCount + 1, CHANGE_NONE_CONTENT)
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        notifyItemRangeChanged(positionStart, getItemCount() - positionStart + 1, CHANGE_NONE_CONTENT)
                    }
                })
            }
        }
    }
}
