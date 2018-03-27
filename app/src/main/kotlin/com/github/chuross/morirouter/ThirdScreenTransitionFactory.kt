package com.github.chuross.morirouter

import android.support.transition.ChangeBounds
import android.support.transition.ChangeImageTransform
import android.support.transition.ChangeTransform
import android.support.transition.TransitionSet
import com.github.chuross.morirouter.core.TransitionFactory

class ThirdScreenTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(ChangeBounds())
        it.addTransition(ChangeTransform())
        it.addTransition(ChangeImageTransform())
    }
}