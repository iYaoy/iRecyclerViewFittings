package com.iyao.recyclerviewhelper.adapter

import android.support.v7.widget.RecyclerView

interface AdapterWrapper {
    /**
     * @return 返回被当前Wrapper包装的clientAdapter
     */
    fun getWrappedAdapter(): RecyclerView.Adapter<out RecyclerView.ViewHolder>
    /**
     * 返回当前包装类的wrapperPosition对应的client Adapter的position
     * @param wrapperPosition 当前Wrapper中的position
     * @return client Adapter中的position
     */
    fun getWrappedPosition(wrapperPosition: Int): Int

    /**
     * 返回client wrappedPosition在当前包装类的AdapterPosition
     * @param wrappedPosition client Adapter中的position
     * @return 当前Wrapper中的position
     */
    fun getWrapperAdapterPosition(wrappedPosition: Int): Int

}





