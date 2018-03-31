package com.github.chuross.morirouter

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentProductionSampleBinding

@RouterPath(
        name = "productionSample"
)
class ProductionSampleScreenFragment : BaseFragment<FragmentProductionSampleBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_production_sample

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}