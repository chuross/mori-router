package com.github.chuross.morirouter

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.databinding.FragmentMainBinding
import com.github.chuross.morirouter.router.MainScreenBinder

@RouterPath(
        name = "main"
)
class MainScreenFragment : BaseFragment<FragmentMainBinding>() {

    @RouterParam
    lateinit var param1: String

    @RouterParam(name = "iee_i")
    lateinit var param2: String

    @RouterParam(name = "tekitou_list", required = false)
    var param3: ArrayList<String> = arrayListOf()

    override val layoutResourceId: Int = R.layout.fragment_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScreenBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.button?.setOnClickListener {
             router?.dispatch(Uri.parse("morirouter://second/2/contents/123"))
        }
    }

}