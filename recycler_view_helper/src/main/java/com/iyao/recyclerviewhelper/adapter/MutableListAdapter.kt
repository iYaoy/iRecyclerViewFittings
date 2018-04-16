package com.iyao.recyclerviewhelper.adapter


import android.support.v7.widget.RecyclerView

abstract class MutableListAdapter<VH : RecyclerView.ViewHolder, E> : RecyclerView.Adapter<VH>(), MutableList<E> {

    protected var data : MutableList<E> = ArrayList()

    override val size: Int
        get() = data.size

    override fun contains(element: E): Boolean {
        return data.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return data.containsAll(elements)
    }

    override fun get(index: Int): E {
        return data[index]
    }

    override fun indexOf(element: E): Int {
        return data.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun iterator(): MutableIterator<E> {
        return data.iterator()
    }

    override fun lastIndexOf(element: E): Int {
        return data.lastIndexOf(element)
    }

    override fun add(element: E): Boolean {
        return data.add(element)
    }

    override fun add(index: Int, element: E) {
        data.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return data.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return data.addAll(elements)
    }

    override fun clear() {
        data.clear()
    }

    override fun listIterator(): MutableListIterator<E> {
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return data.listIterator(index)
    }

    override fun remove(element: E): Boolean {
        return data.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return data.removeAll(elements)
    }

    override fun removeAt(index: Int): E {
        return data.removeAt(index)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return data.retainAll(elements)
    }

    override fun set(index: Int, element: E): E {
        return data.set(index, element)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return data.subList(fromIndex, toIndex)
    }

    override fun getItemCount(): Int {
        return size
    }
}