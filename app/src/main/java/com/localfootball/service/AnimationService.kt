package com.localfootball.service

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.localfootball.R

const val INFINITE_MEDIUM_ROTATION_XML = R.anim.infinite_medium_rotation
const val INFINITE_SLOW_ROTATION_XML = R.anim.infinite_slow_rotation

class AnimationService {

    fun startInfiniteMediumVelocityRotating(context: Context, component: View) {
        component.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                INFINITE_MEDIUM_ROTATION_XML
            )
        )
    }

    fun startInfiniteSlowVelocityRotating(context: Context, component: View) {
        component.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                INFINITE_SLOW_ROTATION_XML
            )
        )
    }

    fun rightToLeftSideSlideWithFadeInAnimation(duration: Long, component: View, fromAlpha: Float, toAlpha: Float) {
        val tx = ValueAnimator.ofFloat(2000f, 0f)
        tx.duration = duration
        tx.addUpdateListener { animation ->
            component.translationX = animation.animatedValue as Float
        }
        tx.start()
        fadeInAnimation(duration, component, fromAlpha, toAlpha)
    }

    fun leftToRightSideSlideWithFadeInAnimation(duration: Long, component: View, fromAlpha: Float, toAlpha: Float) {
        val tx = ValueAnimator.ofFloat(-2000f, 0f)
        tx.duration = duration
        tx.addUpdateListener { animation ->
            component.translationX = animation.animatedValue as Float
        }
        tx.start()
        fadeInAnimation(duration, component, fromAlpha, toAlpha)
    }

    fun fadeInAnimation(duration: Long, component: View, fromAlpha: Float, toAlpha: Float) {
        val fade = ObjectAnimator.ofFloat(component, View.ALPHA, fromAlpha, toAlpha)
        fade.duration = duration
        fade.start()
    }
}