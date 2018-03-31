package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.WithArguments
import com.github.chuross.morirouter.router.ListFragmentBinder

@WithArguments
class ListFragment : Fragment() {

    @Argument
    lateinit var name: String

    @Argument(required = false)
    var index: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ListFragmentBinder.bind(this)
    }
}