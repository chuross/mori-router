package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.FragmentPagerAdapter
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentProductionSampleBinding
import com.github.chuross.morirouter.router.ListFragmentBuilder

@RouterPath(
        name = "productionSample"
)
class ProductionSampleScreenFragment : BaseFragment<FragmentProductionSampleBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_production_sample

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FragmentPagerAdapter(childFragmentManager, listOf(
                Pair("first", { ListFragmentBuilder("first").build() }),
                Pair("second", { ListFragmentBuilder("second").build() })
        ))

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}