package ru.snowmaze.expandablerecyclerviewexample

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

internal class ListViewAdapter(private val callback: ListAdapterCallback, private val groupPosition: Int, var childrenCount: Int) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =  callback.getView(groupPosition, position , parent)

    override fun getItem(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = childrenCount

    interface ListAdapterCallback {

        fun getView(groupPosition: Int, childPosition: Int, parent: ViewGroup): View?

    }

}
