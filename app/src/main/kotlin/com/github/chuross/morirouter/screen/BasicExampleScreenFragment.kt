package com.github.chuross.morirouter.screen

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.databinding.FragmentBasicExampleBinding

@RouterPath(
        name = "basicExample"
)
class BasicExampleScreenFragment : BaseFragment<FragmentBasicExampleBinding>() {

    @Argument
    lateinit var param1: String

    @Argument(name = "iee_i")
    lateinit var param2: String

    @Argument(name = "tekitou_list", required = false)
    var param3: ArrayList<String> = arrayListOf()

    @Argument(name = "parcelable_field", required = false)
    var parcelableField: TextView.SavedState? = null

    override val layoutResourceId: Int = R.layout.fragment_basic_example

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MoriBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button?.setOnClickListener {
             router?.dispatch(Uri.parse("morirouter://second/2/contents/123"))
        }
    }

}