package com.kaizm.food_app.ultils

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeGesture(val context: Context) : ItemTouchHelper(object : ItemTouchHelper.Callback() {
    private val limitScrollX = 100F.dipToPx(context)
    private var currentScrollX = 0

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = START or END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    override fun clearView(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder.itemView.scrollX > limitScrollX) {
            viewHolder.itemView.scrollTo(limitScrollX, 0)
        } else if (viewHolder.itemView.scrollX < 0) {
            viewHolder.itemView.scrollTo(0, 0)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (isCurrentlyActive) {
            var scrollOffset = currentScrollX + (-dX).toInt()
            if (scrollOffset > limitScrollX) {
                scrollOffset = limitScrollX
            } else if (scrollOffset < 0) {
                scrollOffset = 0
            }
            viewHolder.itemView.scrollTo(scrollOffset, 0)
        }
//                super.onChildDraw(
//                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
//                )
    }
})