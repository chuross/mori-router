package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.UriArgument
import com.github.chuross.morirouter.databinding.FragmentSecondBinding

@RouterPath(
        name = "second",
        uris = [
            "morirouter://second/{second_id}/contents/{content_id}",
            "https://www.hoge.com/second/{second_id}/contents/{content_id}"
        ]
)
class SecondScreenFragment : BaseFragment<FragmentSecondBinding>() {

    @UriArgument(name = "second_id")
    var id: String? = null

    @UriArgument
    var contentId: Int? = null

    override val layoutResourceId: Int = R.layout.fragment_second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MoriBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.text.text = "format\nmorirouter://second/{second_id}/contents/{content_id}\n\nid=$id, contentId=$contentId"

        binding.screenButton.setOnClickListener {
            router?.thirdOuie()?.addSharedElement(binding.appIconImage)?.launch()
        }

        binding.popButton.setOnClickListener {
            router?.pop()
        }
    }
}