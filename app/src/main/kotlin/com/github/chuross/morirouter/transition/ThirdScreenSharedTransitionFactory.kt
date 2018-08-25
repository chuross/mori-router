package com.github.chuross.morirouter.transition

import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import com.github.chuross.morirouter.core.TransitionFactory

class ThirdScreenSharedTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(ChangeBounds())
        it.addTransition(ChangeTransform())
        it.addTransition(ChangeImageTransform())
    }
}