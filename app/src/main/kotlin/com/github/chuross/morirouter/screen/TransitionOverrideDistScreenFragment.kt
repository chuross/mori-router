package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentTransitionOverrideDistBinding
import com.github.chuross.morirouter.transition.ArcImageSharedTransitionFactory
import com.github.chuross.morirouter.transition.TransitionOverrideDistTransitionFactory

@RouterPath(
        name = "transitionOverrideDist",
        overrideEnterTransitionFactory = TransitionOverrideDistTransitionFactory::class,
        overrideExitTransitionFactory = TransitionOverrideDistTransitionFactory::class,
        sharedEnterTransitionFactory = ArcImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ArcImageSharedTransitionFactory::class
)
class TransitionOverrideDistScreenFragment : BaseFragment<FragmentTransitionOverrideDistBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_transition_override_dist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MoriBinder.bindElement(this, R.id.image)

        binding.nextScreenButton.setOnClickListener {
            router?.transitionOverrideDist2()
                    ?.addSharedElement(binding.image)
                    ?.addSharedElement(binding.titleText)
                    ?.launch()
        }
    }

}