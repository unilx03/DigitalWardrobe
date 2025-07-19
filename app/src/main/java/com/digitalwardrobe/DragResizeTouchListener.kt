package com.digitalwardrobe

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class DragResizeTouchListener(
    private val onClick: (() -> Unit)? = null
) : View.OnTouchListener {

    private var mode = NONE
    private var dX = 0f
    private var dY = 0f
    private val clickThreshold = 10f
    private var initialDistance = 0f
    private var initialScale = 1f

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                dX = event.rawX - view.x
                dY = event.rawY - view.y

                onClick?.invoke()

                mode = DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    initialDistance = spacing(event)
                    initialScale = view.scaleX
                    mode = ZOOM
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when (mode) {
                    DRAG -> {
                        view.x = event.rawX - dX
                        view.y = event.rawY - dY
                    }

                    ZOOM -> {
                        if (event.pointerCount == 2) {
                            val newDistance = spacing(event)
                            if (initialDistance != 0f) {
                                val scaleFactor = newDistance / initialDistance
                                val newScale = initialScale * scaleFactor

                                // Prevent scaling too small or large
                                view.scaleX = min(5f, max(0.3f, newScale))
                                view.scaleY = min(5f, max(0.3f, newScale))
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }
        }

        return true
    }

    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }
}