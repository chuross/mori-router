package com.github.chuross.morirouter

import android.support.v4.app.Fragment
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterParam

@RouterPath(
        name = "main",
        uri = "morirouter://main"
)
class MainFragment : Fragment() {

    @RouterParam(required = true)
    lateinit var param1: String
}