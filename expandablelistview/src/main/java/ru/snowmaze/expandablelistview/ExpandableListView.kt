package ru.snowmaze.expandablelistview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class ExpandableListView: LinearLayout {

    private var adapter: ExpandableListAdapter  ? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun setOrientation(orientation: Int) {}

    fun setAdapter(adapter: ExpandableListAdapter) {
        super.setOrientation(VERTICAL)
        this.adapter = adapter
        adapter.onAttachedToExpandableListView(this)
    }
}