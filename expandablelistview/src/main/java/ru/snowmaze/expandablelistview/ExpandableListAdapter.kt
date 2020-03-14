package ru.snowmaze.expandablelistview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item.view.*

abstract class ExpandableListAdapter {

    private var expandableListView: ExpandableLinearLayout? = null
    private val viewHolders = mutableListOf<ViewHolder>()

    internal fun onAttachedToExpandableListView(expandableListView: ExpandableLinearLayout) {
        this.expandableListView = expandableListView
    }

    private fun getGroupAndShow(holder: ViewHolder, position: Int) {
        val view = getGroupView(position, holder.item)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.item.group_layout.addView(view)
    }

    private fun createSplitter(parent: ViewGroup): View {
        val splitter = LayoutInflater.from(parent.context).inflate(R.layout.splitter, parent, false)
        splitter.setBackgroundColor(getSplitterColor(parent.context))
        return splitter
    }

    private fun createChild(groupPosition: Int, childPosition: Int, parent: ViewGroup): View {
        val childLayout = LinearLayout(parent.context)
        childLayout.orientation = LinearLayout.VERTICAL
        val childView = getChildView(groupPosition, childPosition, getChildrenCount(groupPosition) == childPosition + 1, childLayout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(!childView.hasOnClickListeners()) {
                childView.setOnClickListener { }
            }
            childView.background = ContextCompat.getDrawable(childView.context, R.drawable.ripple)
        }
        childLayout.addView(childView, 0)
        childLayout.addView(createSplitter(childLayout))
        return childLayout
    }

    private fun getGroup(position: Int): View? {
        val holder = ViewHolder(LayoutInflater.from(expandableListView!!.context).inflate(R.layout.item, expandableListView, false))
        holder.item.addView(createSplitter(holder.item),1)
        viewHolders.add(position, holder)
        getGroupAndShow(holder, position)
        holder.arrow.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray))
        for(childPosition in 0 until getChildrenCount(position)) {
            holder.list.addView(createChild(position, childPosition, holder.list))
        }
        return holder.itemView
    }

    open fun notifyDataSetChanged() {
        if (expandableListView == null) {
            return
        }
        expandableListView?.removeAllViewsInLayout()
        for (i in 0 until getGroupCount()) {
            expandableListView?.addView(getGroup(i), i)
        }
    }

    open fun notifyGroupInserted(position: Int) {
        expandableListView?.addView(getGroup(position), position)
    }

    open fun notifyGroupDeleted(position: Int) {
        viewHolders.removeAt(position)
        expandableListView?.removeViewAt(position)
    }

    open fun notifyGroupViewChanged(position: Int) {
        val holder = viewHolders[position]
        holder.item.removeViewAt(0)
        getGroupAndShow(holder, position)
    }

     fun notifyChildChanged(groupPosition: Int, childPosition: Int) {
        onChildChanged(groupPosition, childPosition, (viewHolders[groupPosition].list.getChildAt(childPosition) as LinearLayout).getChildAt(0))
    }

    fun notifyChildDeleted(groupPosition: Int, childPosition: Int) {
        val holder = viewHolders[groupPosition]
        holder.list.getChildAt(childPosition).animate().setDuration(300).alpha(0F).setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                holder.list.removeViewAt(childPosition)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        }).start()
    }


    open fun notifyChildInserted(groupPosition: Int, childPosition: Int) {
        val holder = viewHolders[groupPosition]
        val child = createChild(groupPosition, childPosition, holder.list)
        holder.list.addView(child, childPosition)
        if(childAnimationsEnabled()) {
            val animation = AlphaAnimation(0F, 1F)
            animation.duration = 300
            child.startAnimation(animation)
        }
    }

    private inner class ViewHolder(val itemView: View) {

        var expanded = false

        val item  = itemView.item

        val arrow = itemView.arrow

        val list = itemView.list

        init {
            arrow.rotation = 180F
            list.visibility = View.GONE
            itemView.group_layout.setOnClickListener {
                val animationType = getListAnimationType()
                val animationDuration = getListAnimationDuration()
                if(animationType != NO_ANIMATION) {
                    if (expanded) {
                        if (animationType == SCALE) {
                            startListAnimation(ScaleAnimation(1F, 1F, 1F, 0F), View.GONE, animationDuration)
                        } else if (animationType == ALPHA) {
                            startListAnimation(AlphaAnimation(1F,0F), View.GONE, animationDuration)
                        }
                    } else {
                        if (animationType == SCALE) {
                            startListAnimation(ScaleAnimation(1F, 1F, 0F, 1F), View.VISIBLE, animationDuration)
                        } else if (animationType == ALPHA) {
                            startListAnimation(AlphaAnimation(0F,1F), View.VISIBLE, animationDuration)
                        }
                    }
                } else {
                    if (expanded) {
                        list.visibility = View.GONE
                    } else {
                        list.visibility = View.VISIBLE
                    }
                }
                if(arrowAnimationEnabled()) {
                    if (expanded) {
                        ObjectAnimator.ofFloat(item.arrow, View.ROTATION, 0F, 180F)
                            .setDuration(getArrowAnimationDuration()).start()
                    } else {
                        ObjectAnimator.ofFloat(item.arrow, View.ROTATION, 180F, 360F)
                            .setDuration(getArrowAnimationDuration()).start()
                    }
                }
                else {
                    if(expanded) {
                        arrow.rotation = 1800F
                    }
                    else {
                        arrow.rotation = 0F
                    }
                }
                expanded = !expanded
            }
        }
        private fun startListAnimation(animation: Animation, endVisibility: Int, duration: Long) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                }

                override fun onAnimationEnd(animation: Animation) {
                    list.visibility = endVisibility
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            list.visibility = View.VISIBLE
            animation.duration = duration
            list.startAnimation(animation)
        }
    }



    abstract fun getGroupCount(): Int

    abstract fun getChildrenCount(groupPosition: Int): Int

    abstract fun getGroupView(groupPosition: Int, parent: ViewGroup): View

    abstract fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, parent: ViewGroup) : View

    open fun getListAnimationType() = SCALE

    open fun getListAnimationDuration(): Long = 75

    open fun getArrowAnimationDuration(): Long = 200

    open fun arrowAnimationEnabled() = true

    open fun childAnimationsEnabled() = true

    open fun onChildChanged(groupPosition: Int, childPosition: Int, view: View) {}

    open fun getSplitterColor(context: Context) = ContextCompat.getColor(context, R.color.gray)

    companion object {

        const val NO_ANIMATION = 0

        const val SCALE = 1

        const val ALPHA = 2

    }

}