package com.vertice.teepop.mangaapp.util

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by VerDev06 on 1/31/2018.
 */
class RecyclerItemClickListener(context: Context, private val onItemClickListener: (view: View, position: Int) -> Unit) : RecyclerView.OnItemTouchListener {

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
    })

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
        val childView = rv?.findChildViewUnder(e?.x ?: 0.0F, e?.y ?: 0.0F)
        childView?.let {
            if (gestureDetector.onTouchEvent(e))
                onItemClickListener.invoke(it, rv.getChildAdapterPosition(it))
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}