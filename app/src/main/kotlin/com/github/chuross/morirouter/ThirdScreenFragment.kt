package com.github.chuross.morirouter

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentThirdBinding

@RouterPath(
        name = "third_ouie",
        transitionNames = [
            "icon_image"
        ],
        enterTransitionFactory = ThirdScreenTransitionFactory::class,
        exitTransitionFactory = ThirdScreenTransitionFactory::class
)
class ThirdScreenFragment : BaseFragment<FragmentThirdBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_third

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TransitionNameHelper.setIconImage(binding?.appIconImage)
    }
}