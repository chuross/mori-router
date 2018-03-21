package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.chuross.morirouter.annotation.RouterPath

@RouterPath(
        name = "second",
        uri = "morirouter://second/{id}"
)
class SecondScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            (activity as? MainActivity)?.router?.pop()
        }
    }
}