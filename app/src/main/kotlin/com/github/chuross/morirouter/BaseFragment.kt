package com.github.chuross.morirouter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    abstract val layoutResourceId: Int
    val router: MoriRouter? get() = (activity as? MainActivity)?.router
    lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(layoutResourceId, container, false).also {
            binding = DataBindingUtil.bind(it)!!
        }
    }
}