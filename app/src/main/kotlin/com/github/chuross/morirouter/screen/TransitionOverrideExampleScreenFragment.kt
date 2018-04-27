package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentTransitionOverrideBinding
import com.github.chuross.morirouter.transition.TransitionOverrideExampleTransitionFactory

@RouterPath(
        name = "transitionOverrideExample",
        overrideEnterTransitionFactory = TransitionOverrideExampleTransitionFactory::class,
        overrideExitTransitionFactory = TransitionOverrideExampleTransitionFactory::class
)
class TransitionOverrideExampleScreenFragment : BaseFragment<FragmentTransitionOverrideBinding>(){

    override val layoutResourceId: Int = R.layout.fragment_transition_override

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.animationStartButton.setOnClickListener {
            router?.transitionOverrideDist()
                    ?.addSharedElement(binding.image)
                    ?.launch()
        }
    }
}