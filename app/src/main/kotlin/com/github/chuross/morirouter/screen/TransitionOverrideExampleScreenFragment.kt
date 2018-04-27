package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.GlideApp
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentTransitionOverrideBinding
import com.github.chuross.morirouter.util.Data

@RouterPath(
        name = "transitionOverrideExample"
)
class TransitionOverrideExampleScreenFragment : BaseFragment<FragmentTransitionOverrideBinding>(){

    override val layoutResourceId: Int = R.layout.fragment_transition_override

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlideApp.with(this)
                .load(Data.LIST_DATA.first())
                .fitCenter()
                .centerInside()
                .into(binding.image)

        binding.animationStartButton.setOnClickListener {
            router?.transitionOverrideDist()
                    ?.addSharedElement(binding.image)
                    ?.launch()
        }
    }
}