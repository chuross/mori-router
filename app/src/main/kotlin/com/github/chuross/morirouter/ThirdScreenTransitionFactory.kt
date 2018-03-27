package com.github.chuross.morirouter

import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import com.github.chuross.morirouter.core.TransitionFactory

class ThirdScreenTransitionFactory : TransitionFactory {

    override fun create(): Any = ThirdScreenTransitionSet()

    private class ThirdScreenTransitionSet : TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }
    }
}