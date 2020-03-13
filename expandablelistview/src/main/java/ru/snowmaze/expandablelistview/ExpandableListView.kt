package ru.snowmaze.expandablelistview

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class ExpandableListView: NestedScrollView {

    private var expandableLinearLayout: ExpandableLinearLayout? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setAdapter(adapter: ExpandableListAdapter) {
        if(expandableLinearLayout == null) {
            expandableLinearLayout = ExpandableLinearLayout(context)
            addView(expandableLinearLayout)
        }
        expandableLinearLayout!!.setAdapter(adapter)
    }

}