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

@RouterPath(
        name = "second",
        uris = [
            "morirouter://second/{second_id}/contents/{content_id}",
            "https://www.hoge.com/second/{second_id}/contents/{content_id}"
        ]
)
class SecondScreenFragment : Fragment() {

    @RouterUriParam(name = "second_id")
    var id: String? = null

    @RouterUriParam
    var contentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondScreenBinder.bind(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.findViewById<TextView>(R.id.text)?.text = "format\nmorirouter://second/{second_id}/contents/{content_id}\n\nid=$id, contentId=$contentId"

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            (activity as? MainActivity)?.router?.pop()
        }
    }
}