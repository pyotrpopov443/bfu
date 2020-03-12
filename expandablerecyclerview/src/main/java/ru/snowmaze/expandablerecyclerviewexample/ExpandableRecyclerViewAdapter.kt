package ru.snowmaze.expandablerecyclerviewexample

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*

internal class ExpandableRecyclerViewAdapter :
    RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ViewHolder>(),
    ExpandableListAdapter.ExpandableListAdapterCallback, ListViewAdapter.ListAdapterCallback {

    private var groupCount = 0
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var callback: ExpandableAdapterCallback? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView is ExpandableAdapterCallback) {
            callback = recyclerView
        }
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val item = itemView.item

        val list = itemView.list

        private val arrow = itemView.arrow

        private var expanded = false

        private fun setAnimationListenerAndStartAnim(v: View, animation: Animation, endVisibility: Int, duration: Long) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    v.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    v.visibility = endVisibility
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            animation.duration = duration
            v.startAnimation(animation)
        }

        private fun animateView(v: View, animResId: Int, endVisibility: Int, duration: Long) {
            setAnimationListenerAndStartAnim(v, AnimationUtils.loadAnimation(v.context, animResId), endVisibility, duration)
        }

        init {
            list.visibility = View.GONE
            var first = true
            item.setOnClickListener {
                val animationType = expandableListAdapter!!.getListAnimationType()
                val animationDuration = expandableListAdapter!!.getListAnimationDuration()
                if(animationType != ExpandableListAdapter.NO_ANIMATION) {
                    if (expanded) {
                        if (animationType == ExpandableListAdapter.SCALE) {
                            animateView(list, R.anim.scale_down, View.GONE, animationDuration)
                        } else if (animationType == ExpandableListAdapter.ALPHA) {
                            setAnimationListenerAndStartAnim(list, AlphaAnimation(1F,0F), View.GONE, animationDuration)
                        }
                    } else {
                        if (animationType == ExpandableListAdapter.SCALE) {
                            animateView(list, R.anim.scale_up, View.VISIBLE, animationDuration)
                        } else if (animationType == ExpandableListAdapter.ALPHA) {
                            setAnimationListenerAndStartAnim(list, AlphaAnimation(0F,1F), View.VISIBLE, animationDuration)
                        }
                    }
                }
                else {
                    if (expanded) {
                        list.visibility = View.GONE
                    } else {
                        list.visibility = View.VISIBLE
                    }
                }
                if(expanded) {
                    ObjectAnimator.ofFloat(arrow, View.ROTATION, 180F, 360F).setDuration(animationDuration).start()
                }
                else {
                    if(first) {
                        first = false
                        list.invalidateViews()
                    }
                    ObjectAnimator.ofFloat(arrow, View.ROTATION, 0F, 180F).setDuration(animationDuration).start()
                }
                expanded = !expanded
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item, parent, false
        )
    )

    override fun getItemCount() = groupCount

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = expandableListAdapter?.getGroupView(position, holder.item)
        view?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.item.group_layout.addView(view)
        holder.item.arrow.setColorFilter(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.gray
            )
        )
        holder.list.adapter = ListViewAdapter(this, position, expandableListAdapter!!.getChildrenCount(position))
    }

    private fun notifyChanged() {
        if (expandableListAdapter == null) {
            return
        }
        groupCount = expandableListAdapter!!.getGroupCount()
        callback?.clearViews()
        notifyDataSetChanged()
    }

    fun setExpandableAdapter(expandableListAdapter: ExpandableListAdapter) {
        this.expandableListAdapter = expandableListAdapter
        expandableListAdapter.onAttachedToExpandableRecyclerViewAdapter(this)
        groupCount = expandableListAdapter.getGroupCount()
    }

    override fun onDataSetChanged() {
        notifyChanged()
    }

    override fun getView(groupPosition: Int, childPosition: Int, parent: ViewGroup): View? {
        return expandableListAdapter?.getChildView(
            groupPosition,
            childPosition,
            expandableListAdapter!!.getChildrenCount(groupPosition) == childPosition + 1,
            parent
        )
    }

    interface ExpandableAdapterCallback {

        fun clearViews()

        fun scrollToPosition(position: Int)

    }

}