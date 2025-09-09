package com.spallaplayer

import android.content.Context
import android.widget.FrameLayout
import com.spalla.sdk.android.core.player.view.SpallaPlayerView
import android.view.ViewGroup;

class SpallaPlayerContainerView(context: Context) : ViewGroup(context) {
  val spallaPlayerView: SpallaPlayerView = SpallaPlayerView(context)

  init {
    addView(spallaPlayerView) // Add SpallaPlayerView as a child of this container
    // You might need to set layout params for spallaPlayerView here
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.layout(0, 0, width, height)
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
