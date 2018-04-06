package com.github.chuross.morirouter.extension

import android.databinding.BindingAdapter
import android.support.v4.view.ViewCompat
import android.view.View

@BindingAdapter(value = ["ext_transitionName"])
fun View.extSetTransitionName(transitionName: String?) {
    transitionName?.let { ViewCompat.setTransitionName(this, it) }
}