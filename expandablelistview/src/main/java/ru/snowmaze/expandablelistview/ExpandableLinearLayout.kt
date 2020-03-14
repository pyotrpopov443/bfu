package ru.snowmaze.expandablelistview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

internal class ExpandableLinearLayout: LinearLayout {

    private var adapter: ExpandableListAdapter  ? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setAdapter(adapter: ExpandableListAdapter) {
        orientation = VERTICAL
        this.adapter = adapter
        adapter.onAttachedToExpandableListView(this)
    }

}