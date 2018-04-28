package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentTransitionOverrideDist2Binding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory

@RouterPath(
        name = "transitionOverrideDist2",
        sharedEnterTransitionFactory = ImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ImageSharedTransitionFactory::class
)
class TransitionOverrideDist2ScreenFragment : BaseFragment<FragmentTransitionOverrideDist2Binding>() {

    override val layoutResourceId: Int = R.layout.fragment_transition_override_dist2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MoriBinder.bindElement(this, R.id.image)
        MoriBinder.bindElement(this, R.id.title_text)
    }
}