package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentThirdBinding
import com.github.chuross.morirouter.transition.ThirdScreenSharedTransitionFactory

@RouterPath(
        name = "third_ouie",
        sharedEnterTransitionFactory = ThirdScreenSharedTransitionFactory::class,
        sharedExitTransitionFactory = ThirdScreenSharedTransitionFactory::class
)
class ThirdScreenFragment : BaseFragment<FragmentThirdBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_third

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MoriBinder.bindElement(this, R.id.app_icon_image)

    }
}