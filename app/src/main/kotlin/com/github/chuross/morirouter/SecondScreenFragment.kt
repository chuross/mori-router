package com.github.chuross.morirouter

import android.support.v4.app.Fragment
import com.github.chuross.morirouter.annotation.RouterPath

@RouterPath(
        name = "second",
        uri = "morirouter://second/{id}"
)
class SecondScreenFragment : Fragment() {
}