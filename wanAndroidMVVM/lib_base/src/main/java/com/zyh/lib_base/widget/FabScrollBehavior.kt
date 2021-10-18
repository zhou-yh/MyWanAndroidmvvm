package com.zyh.lib_base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * author: zhouyh
 * created on: 2021/9/27 3:47 下午
 * description:悬浮按钮联动
 *  // 因为需要在布局xml中引用，所以必须实现该构造方法
 */
class FabScrollBehavior(content:Context?,attrs : AttributeSet?)
    :FloatingActionButton.Behavior(content,attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int,
    ): Boolean {

        //确保滚动方向为垂直方向
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
    ) {
        super.onNestedScroll(coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed)

        if (dyConsumed > 0) { //向下滑动
            animateOut(child)
        } else if (dyConsumed < 0) {  //向上滑动
            animateIn(child)
        }
    }


    //fab 进入屏幕 (显示）
    private fun animateIn(fab: FloatingActionButton) {

        fab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(LinearInterpolator())
            .start()
        fab.visibility = View.VISIBLE

    }

    //fab 退出屏幕（隐藏）
    private fun animateOut(fab: FloatingActionButton) {
        fab.animate().scaleX(0f).scaleY(0f).setDuration(200).setInterpolator(LinearInterpolator())
            .start()
    }

}