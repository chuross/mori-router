package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterUriParam
import com.github.chuross.morirouter.databinding.FragmentSecondBinding

@RouterPath(
        name = "second",
        uris = [
            "morirouter://second/{second_id}/contents/{content_id}",
            "https://www.hoge.com/second/{second_id}/contents/{content_id}"
        ]
)
class SecondScreenFragment : BaseFragment<FragmentSecondBinding>() {

    @RouterUriParam(name = "second_id")
    var id: String? = null

    @RouterUriParam
    var contentId: Int? = null

    override val layoutResourceId: Int = R.layout.fragment_second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondScreenBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TransitionNameHelper.setIconImage(binding?.appIconImage)

        binding?.text?.text = "format\nmorirouter://second/{second_id}/contents/{content_id}\n\nid=$id, contentId=$contentId"

        binding?.screenButton?.setOnClickListener {
            router?.thirdOuie()?.iconImage(binding?.appIconImage)?.launch()
        }

        binding?.popButton?.setOnClickListener {
            router?.pop()
        }
    }
}