package ru.snowmaze.expandablerecyclerviewexample

import android.view.View
import android.view.ViewGroup

abstract class ExpandableListAdapter {

    private var callback: ExpandableListAdapterCallback? = null

    abstract fun getGroupCount(): Int

    abstract fun getChildrenCount(groupPosition: Int): Int

    abstract fun getGroupView(groupPosition: Int, parent: ViewGroup): View?

    abstract fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, parent: ViewGroup) : View?

    open fun getListAnimationType() = SCALE

    open fun notifyDataSetChanged() = callback?.onDataSetChanged()

    open fun getListAnimationDuration(): Long = 75

    internal fun onAttachedToExpandableRecyclerViewAdapter(expandableRecyclerViewAdapter: ExpandableRecyclerViewAdapter) { callback = expandableRecyclerViewAdapter }


    interface ExpandableListAdapterCallback {

        fun onDataSetChanged()

    }

    companion object {

        const val NO_ANIMATION = 0

        const val SCALE = 1

        const val ALPHA = 2

    }

}