package com.iyao.recyclerviewhelper.touchevent

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addOnItemClickListener(runnable: RecyclerView.(RecyclerView.ViewHolder)->Unit) : RecyclerView.OnItemTouchListener {
    return object : RecyclerView.OnItemTouchListener {
        val listener = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return findChildViewUnder(e.x, e.y)?.apply {
                    runnable.invoke(this.parent as RecyclerView, getChildViewHolder(this))
                } != null
            }
        })
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            listener.onTouchEvent(e)
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return false.apply {
                listener.onTouchEvent(e)
            }
        }
    }.apply {
        addOnItemTouchListener(this)
    }
}

fun RecyclerView.addOnItemLongClickListener(runnable: RecyclerView.(RecyclerView.ViewHolder)->Unit) : RecyclerView.OnItemTouchListener{
    return object : RecyclerView.OnItemTouchListener {
        val listener = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                findChildViewUnder(e.x, e.y)?.apply {
                    runnable.invoke(this.parent as RecyclerView, getChildViewHolder(this))
                }
            }
        })
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            listener.onTouchEvent(e)
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            listener.onTouchEvent(e)
            return false
        }
    }.also {
        addOnItemTouchListener(it)
    }
}