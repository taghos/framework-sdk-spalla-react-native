package com.spallaplayer

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.spalla.sdk.android.core.player.view.SpallaPlayerView
import android.view.ViewGroup;
import androidx.core.view.isNotEmpty

class SpallaPlayerContainerView(context: Context) : ViewGroup(context) {
    val spallaPlayerView: SpallaPlayerView = SpallaPlayerView(context)
    private var isAttachedToWindow = false

    init {
        addView(spallaPlayerView)
        requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val containerWidth = r - l
        val containerHeight = b - t

        Log.d("SpallaPlayerContainerView", "Layout: container=${containerWidth}x${containerHeight}")

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(0, 0, containerWidth, containerHeight)

            if (child is SpallaPlayerView) {
                // Schedule multiple layout passes to catch the async initialization
                repeat(3) { index ->
                    child.post {
                        child.requestLayout()
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttachedToWindow = true
        // Ensure proper initialization when attached to window
        post {
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(width, height)
    }

}
