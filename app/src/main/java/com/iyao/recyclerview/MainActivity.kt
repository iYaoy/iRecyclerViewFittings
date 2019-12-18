package com.iyao.recyclerview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyao.recyclerviewhelper.adapter.*
import com.iyao.recyclerviewhelper.itemdecoration.GridLayoutItemDecoration
import com.iyao.recyclerviewhelper.touchevent.MutableListAdapterImpl
import com.iyao.recyclerviewhelper.touchevent.addOnItemClickListener
import com.iyao.recyclerviewhelper.touchevent.addOnItemLongClickListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val itemTouchHelper = ItemTouchHelper(MutableListAdapterImpl(
            ItemTouchHelper.LEFT or ItemTouchHelper.UP or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN,
            0))

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = object : CachedAutoRefreshAdapter<String>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
                    val itemView = layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
                    return CacheViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {
                    (holder.itemView as CheckedTextView).text = get(position)
                }
            }.withWrapper(CachedHeaderAndFooterWrapper())
                    .withWrapper(CachedStatusWrapper())
        }


    }

    override fun onResume() {
        super.onResume()
        val wrapper = recycler_view.getHeaderFooterWrapper()
        recycler_view.apply {
            wrapper.addHeader(1, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(2, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(3, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(4, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addFooter(5, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(6, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(7, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addFooter(8, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addFooter(9, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addFooter(10, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addFooter(11, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(1, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(2, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(3, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
            wrapper.addHeader(4, CacheViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, this, false)))
        }

        recycler_view.postDelayed({
            val autoRefreshAdapter = recycler_view.takeAdapterInstance<CachedAutoRefreshAdapter<String>>()
            autoRefreshAdapter.run {
                listOf("臧鸿飞曝婚内出轨", "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比",
                        "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比",
                        "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比",
                        "詹姆斯超越科比", "詹姆斯超越科比", "詹姆斯超越科比", "韩庚再聊退团经历",
                        "韩庚再聊退团经历", "韩庚再聊退团经历", "韩庚再聊退团经历", "韩庚再聊退团经历",
                        "女子在墓园摔伤").also {
                    refresh(it, null)
                }
            }
        }, 200)


    }
}