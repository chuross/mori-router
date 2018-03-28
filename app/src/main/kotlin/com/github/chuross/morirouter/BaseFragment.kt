package com.github.chuross.morirouter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chuross.morirouter.router.MoriRouter

abstract class BaseFragment<B: ViewDataBinding> : Fragment() {

    abstract val layoutResourceId: Int
    val router: MoriRouter? get() = (activity as? MainActivity)?.router
    var binding: B? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(layoutResourceId, container, false).also {
            binding = DataBindingUtil.bind(it)
        }
    }
}