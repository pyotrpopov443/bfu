package ru.snowmaze.expandablerecyclerviewexample

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpandableRecyclerView: RecyclerView, ExpandableRecyclerViewAdapter.ExpandableAdapterCallback {

    private val adapter = ExpandableRecyclerViewAdapter()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setExpandableAdapter(expandableListAdapter: ExpandableListAdapter) {
        layoutManager = LinearLayoutManager(context)
        adapter.setExpandableAdapter(expandableListAdapter)
        super.setAdapter(adapter)
    }

    override fun setAdapter(adapter: Adapter<*>?) {}

    override fun clearViews() {
        removeAllViewsInLayout()
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }
}