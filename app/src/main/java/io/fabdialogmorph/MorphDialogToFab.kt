/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabdialogmorph

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v4.content.ContextCompat
import android.transition.ChangeBounds
import android.transition.TransitionValues
import android.view.ViewGroup
import android.view.animation.AnimationUtils

/**
 * A transition that morphs a rectangle into a circle, changing it's background color.
 */
class MorphDialogToFab : ChangeBounds() {

    override fun getTransitionProperties(): Array<String> {
        return TRANSITION_PROPERTIES
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        val view = transitionValues.view
        if (view.width <= 0 || view.height <= 0) {
            return
        }
        transitionValues.values[PROPERTY_COLOR] = ContextCompat.getColor(view.context, R.color.dialog_background_color)
        transitionValues.values[PROPERTY_CORNER_RADIUS] = view.resources
                .getDimensionPixelSize(R.dimen.dialog_corners)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        val view = transitionValues.view
        if (view.width <= 0 || view.height <= 0) {
            return
        }
        transitionValues.values[PROPERTY_COLOR] = view.context.getAccentColor()
        transitionValues.values[PROPERTY_CORNER_RADIUS] = view.height / 2
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        val changeBounds = super.createAnimator(sceneRoot, startValues, endValues)
        if (startValues == null || endValues == null || changeBounds == null) {
            return null
        }

        val startColor = startValues.values[PROPERTY_COLOR] as Int?
        val startCornerRadius = startValues.values[PROPERTY_CORNER_RADIUS] as Int?
        val endColor = endValues.values[PROPERTY_COLOR] as Int?
        val endCornerRadius = endValues.values[PROPERTY_CORNER_RADIUS] as Int?

        if (startColor == null || startCornerRadius == null || endColor == null || endCornerRadius == null) {
            return null
        }

        val background = MorphDrawable(startColor, startCornerRadius.toFloat())
        endValues.view.background = background

        val color = ObjectAnimator.ofArgb<MorphDrawable>(background, MorphDrawable.COLOR, endColor)
        val corners = ObjectAnimator.ofFloat<MorphDrawable>(background, MorphDrawable.CORNER_RADIUS, endCornerRadius.toFloat())

        // hide child views (offset down & fade out)
        if (endValues.view is ViewGroup) {
            val vg = endValues.view as ViewGroup
            for (i in 0 until vg.childCount) {
                val v = vg.getChildAt(i)
                v.animate().alpha(0f).translationY((v.height / 3).toFloat()).setStartDelay(0L).setDuration(50L)
                        .setInterpolator(AnimationUtils.loadInterpolator(vg.context, android.R.interpolator.fast_out_linear_in))
                        .start()
            }
        }

        val transition = AnimatorSet()
        transition.playTogether(changeBounds, corners, color)
        transition.interpolator = AnimationUtils.loadInterpolator(sceneRoot.context, android.R.interpolator.fast_out_slow_in)
        transition.duration = 300
        return transition
    }

    companion object {

        private const val PROPERTY_COLOR = "rectMorph:color"
        private const val PROPERTY_CORNER_RADIUS = "rectMorph:cornerRadius"
        private val TRANSITION_PROPERTIES = arrayOf(PROPERTY_COLOR, PROPERTY_CORNER_RADIUS)
    }
}
