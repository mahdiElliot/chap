package com.example.chap.application.internal

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.transition.AutoTransition
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.animation.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

object AnimationHandler {
    enum class Transition {
        AUTO,
        OVER_SHOOT,
        BOUNCE,
        FAST_OUT_SLOW_IN
    }

    fun avdAnimationHandler(view: View) {
        val viewDrawable = when (view) {
            is ImageView -> view.drawable
            is ImageButton -> view.drawable
            else -> null
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val animation = viewDrawable as AnimatedVectorDrawable
            animation.start()
        } else {
            val animation = viewDrawable as AnimatedVectorDrawableCompat
            animation.start()
        }


    }

    fun objectAnimationHandler(
        view: View,
        property: String,
        from: Float,
        to: Float,
        duration: Long
    ) {
        val animation = ObjectAnimator.ofFloat(view, property, to)
        animation.duration = duration
        animation.start()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun animationOperations(
        context: Context,
        fromConstraintLayout: ConstraintLayout,
        toConstraintLayoutId: Int,
        transition: Transition,
        duration: Long
    ) {
        val constraint = ConstraintSet()
        constraint.clone(context, toConstraintLayoutId)
        constraint.applyTo(fromConstraintLayout)
        when (transition) {
            AnimationHandler.Transition.BOUNCE -> {
                val mTransition = ChangeBounds()
                mTransition.duration = duration
                mTransition.interpolator = BounceInterpolator()
                TransitionManager.beginDelayedTransition(fromConstraintLayout, mTransition)
            }
            AnimationHandler.Transition.OVER_SHOOT -> {
                val mTransition = ChangeBounds()
                mTransition.duration = duration
                mTransition.interpolator = OvershootInterpolator()
                TransitionManager.beginDelayedTransition(fromConstraintLayout, mTransition)
            }
            AnimationHandler.Transition.AUTO -> {
                val mTransition = AutoTransition()
                mTransition.duration = duration
                TransitionManager.beginDelayedTransition(fromConstraintLayout, mTransition)
            }
            AnimationHandler.Transition.FAST_OUT_SLOW_IN -> {
                val mTransition = ChangeBounds()
                mTransition.duration = duration
                mTransition.interpolator = FastOutSlowInInterpolator()
                TransitionManager.beginDelayedTransition(fromConstraintLayout, mTransition)
            }
        }

    }

    fun fadeAnimationHandler(isIn: Boolean, view: View?, duration: Long): Animation {
        val fade = if (isIn) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
        fade.duration = duration
        view?.startAnimation(fade)
        return fade
    }

    fun rotateAnimationHandler(
        view: View?,
        fromDegree: Float,
        toDergree: Float,
        duration: Long
    ): Animation {
        val rotate = RotateAnimation(
            fromDegree,
            toDergree,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = duration
        //   rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        view?.startAnimation(rotate)
        return rotate
    }

}