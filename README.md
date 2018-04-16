# iRecyclerViewFittings
This is an simple but useful kit for RecyclerView in kotlin. It has an simple structure, similar interfaces an can help you realize the commonly used functions with RecyclerView, rathor than any breach in your data adapter.
# Adapters
1, MutableListAdapter<VH : RecyclerView.ViewHolder, E>, an abstract adapter extend from RecyclerView.Adapter<VH> and implament all functions in MutableList<E>;
```
fun add(element: E): Boolean
fun add(index: Int, element: E)
fun addAll(index: Int, elements: Collection<E>): Boolean
fun remove(element: E): Boolean
fun removeAll(elements: Collection<E>): Boolean
fun removeAt(index: Int): E
...
```
2, AutoRefreshAdapter<VH : RecyclerView.ViewHolder, E>, witch extend from MutableListAdapter<VH : RecyclerView.ViewHolder, E> and will auto call notifyXXXX methods after data modify methods called.
```
override fun set(index: Int, element: E): E {
    return super.set(index, element).also {
        notifyItemChanged(index, element)
    }
}
```
3, Multiple choice, headers and footers, status views(such as an empty placeholder or a network error display), all of that realized with wrapper. What's more, an observer is registered by wrapper to observe data change happend in client adapter. So you shouldn't need to take care of item view invalidate when data changed. Of course, you can let a wrapper wrapped by any other wrapper.
```
recycler_view.apply {
    adapter = CachedStatusWrapper().apply {
        client = CachedHeaderAndFooterWrapper().apply {
            client = CachedMultipleChoiceWrapper().apply {
                client = object : CachedAutoRefreshAdapter<String>() {
                
                    override fun getItemId(position: Int) = if (position in 0 until itemCount) position.toLong() 
                            else -1
                            
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
                        return layoutInflater
                                .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
                                .let {
                                    it.setBackgroundColor(Color.WHITE)      
                                    CacheViewHolder(it)
                                }
                    }

                    override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {
                        holder.childView<TextView>(android.R.id.text1)?.text = get(position)
                    }
                }
                
                setHasStableIds(true)
            }
       
            addHeader(android.R.layout.simple_list_item_multiple_choice + 1, 
                    CacheViewHolder(layoutInflater
                            .inflate(android.R.layout.simple_list_item_multiple_choice,
                                    recycler_view,
                                    false))
                            .apply {
                                    itemView.setBackgroundColor(Color.WHITE)
                                    childView<TextView>(android.R.id.text1)?.text = "Header: 菲利普亲王入院"
                            })
            addHeader(android.R.layout.simple_list_item_multiple_choice + 2,
                    CacheViewHolder(layoutInflater
                            .inflate(android.R.layout.simple_list_item_multiple_choice,
                                    recycler_view,
                                    false))
                            .apply {
                                    itemView.setBackgroundColor(Color.WHITE)
                                    childView<TextView>(android.R.id.text1)?.text = "Header: 美国公布征税清单"
                            })
            addFooter(android.R.layout.simple_list_item_multiple_choice + 3,
                    CacheViewHolder(layoutInflater
                            .inflate(android.R.layout.simple_list_item_multiple_choice,
                                    recycler_view,
                                    false))
                            .apply {
                                    itemView.setBackgroundColor(Color.WHITE)
                                    childView<TextView>(android.R.id.text1)?.text = "Footer: 女孩感冒右腿截肢"
                            })
        }
        
        addStatusView(-2, CacheViewHolder(layoutInflater.inflate(R.layout.layout_data_empty, recycler_view,
                false)))
        setCurrentStatusIf(-2, { takeIsInstance<CachedAutoRefreshAdapter<String>>()?.itemCount == 0 })
    }
}
```
and you can get a wrapped adapter or wrapped position by :
```
val statusWrapper: CachedStatusWrappr? = adapter.takeIsInstance<CachedStatusWrapper>()
val data: MutableList<String>? = adapter.takeIsInstance<MutableList<String>>()
//click to set item checked status
addOnItemClickListener { _, viewHolder ->
    adapter.takeIsInstance<CachedMultipleChoiceWrapper>()?.run {
        adapter.getWrappedPosition(this, viewHolder.adapterPosition).run {
            setItemChecked(this, !isItemChecked(this))
        }
    }
}
```
4, ViewHodler in all adapters abide by the Dependency-Injection, so you can replace build-in ViewHolder with your own ViewHolder easily.
```
inline fun <reified V : View> RecyclerView.ViewHolder.childView(@IdRes id: Int): V? {
    return itemView.findViewById(id)
}


open class CacheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val children : SparseArray<View> = SparseArray()
}


inline fun <reified V : View> CacheViewHolder.childView(@IdRes id: Int): V? {
    return children[id, itemView.findViewById<V>(id)?.also{
        children.put(id, it)
    }] as V?
}

typealias CachedAutoRefreshAdapter<E> = AutoRefreshAdapter<CacheViewHolder, E>
typealias CachedStatusWrapper = StatusWrapper<CacheViewHolder>
typealias CachedHeaderAndFooterWrapper = HeaderAndFooterWrapper<CacheViewHolder>
typealias CachedMultipleChoiceWrapper = MultipleChoiceWrapper<CacheViewHolder>
```
