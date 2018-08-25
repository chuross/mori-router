package com.github.chuross.morirouter.transition

import androidx.transition.Explode
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.core.TransitionFactory

class TransitionOverrideDistTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(Fade().also {
            it.addTarget(R.id.header_image_container)
            it.addTarget(R.id.divider)
        })
        it.addTransition(Explode().also {
            it.excludeTarget(R.id.header_image_container, true)
            it.excludeTarget(R.id.divider, true)
            it.excludeTarget(R.id.next_screen_button, true)
        })
        it.addTransition(Slide().also {
            it.addTarget(R.id.next_screen_button)
            it.startDelay = 100L
        })
    }
}