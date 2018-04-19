package com.github.chuross.morirouter.transition

import android.support.transition.Fade
import android.support.transition.Slide
import android.support.transition.TransitionSet
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.core.TransitionFactory

class DetailScreenTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(Fade().also {
            it.excludeTarget(R.id.next_button, true)
        })
        it.addTransition(Slide().also {
            it.addTarget(R.id.next_button)
        })
    }
}