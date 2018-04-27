package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentTransitionOverrideDistBinding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory
import com.github.chuross.morirouter.transition.TransitionOverrideDistTransitionFactory

@RouterPath(
        name = "transitionOverrideDist",
        overrideEnterTransitionFactory = TransitionOverrideDistTransitionFactory::class,
        overrideExitTransitionFactory = TransitionOverrideDistTransitionFactory::class,
        sharedEnterTransitionFactory = ImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ImageSharedTransitionFactory::class
)
class TransitionOverrideDistScreenFragment : BaseFragment<FragmentTransitionOverrideDistBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_transition_override_dist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MoriBinder.bindElement(this, R.id.image)

    }

}