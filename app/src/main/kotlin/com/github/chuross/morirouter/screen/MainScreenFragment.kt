package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentMainBinding

@RouterPath(name = "main")
class MainScreenFragment : BaseFragment<FragmentMainBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.basicSampleButton.setOnClickListener { router?.basicExample("requiredParam1", "requiredParam2")?.launch() }
        binding.transitionAnimationSampleButton.setOnClickListener { router?.transitionOverrideExample()?.launch() }
        binding.listToDetailSampleButton.setOnClickListener { router?.listToDetailExample()?.launch() }
    }
}