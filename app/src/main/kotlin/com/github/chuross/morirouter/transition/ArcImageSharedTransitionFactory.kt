package com.github.chuross.morirouter.transition

import androidx.transition.*
import com.github.chuross.morirouter.core.TransitionFactory

class ArcImageSharedTransitionFactory : TransitionFactory {

    override fun create(): Any = TransitionSet().also {
        it.ordering = TransitionSet.ORDERING_TOGETHER
        it.addTransition(ChangeBounds())
        it.addTransition(ChangeTransform())
        it.addTransition(ChangeImageTransform())
        it.setPathMotion(ArcMotion())
    }
}