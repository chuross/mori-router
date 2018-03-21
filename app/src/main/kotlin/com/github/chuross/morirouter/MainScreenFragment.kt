package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterParam

@RouterPath(
        name = "main",
        uri = "morirouter://main"
)
class MainScreenFragment : Fragment() {

    @RouterParam
    lateinit var param1: String

    @RouterParam(name = "ieei")
    lateinit var param2: String

    @RouterParam(required = false)
    lateinit var param3: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScreenBinder.bind(this)
    }
}