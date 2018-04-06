package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentThirdBinding
import com.github.chuross.morirouter.transition.ThirdScreenTransitionFactory

@RouterPath(
        name = "third_ouie",
        enterTransitionFactory = ThirdScreenTransitionFactory::class,
        exitTransitionFactory = ThirdScreenTransitionFactory::class
)
class ThirdScreenFragment : BaseFragment<FragmentThirdBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_third

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.screenButton?.setOnClickListener {
            router?.productionSample()?.launch()
        }
    }
}