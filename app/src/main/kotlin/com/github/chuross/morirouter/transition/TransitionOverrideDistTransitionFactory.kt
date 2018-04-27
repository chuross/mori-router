package com.github.chuross.morirouter.transition

import android.support.transition.Fade
import android.support.transition.TransitionSet
import com.github.chuross.morirouter.core.TransitionFactory

class TransitionOverrideDistTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(Fade())
    }
}