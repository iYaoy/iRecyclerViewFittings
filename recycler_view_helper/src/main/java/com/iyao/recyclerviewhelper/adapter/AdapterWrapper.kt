package com.iyao.recyclerviewhelper.adapter

import androidx.recyclerview.widget.RecyclerView


interface AdapterWrapper {
    /**
     * @return 返回被当前Wrapper包装的wrappedAdapter
     */
    fun getWrappedAdapter(): RecyclerView.Adapter<out RecyclerView.ViewHolder>
    /**
     * 返回当前包装类的wrapperPosition对应的WrappedAdapter的position
     * @param wrapperPosition 当前WrapperAdapter中的position
     * @return client Adapter中的position
     */
    fun getWrappedPosition(wrapperPosition: Int): Int

    /**
     * 返回wrappedPosition在当前包装类的position
     * @param wrappedPosition  WrappedAdapter中的position
     * @return 当前Wrapper中的position
     */
    fun getWrapperAdapterPosition(wrappedPosition: Int): Int

}





