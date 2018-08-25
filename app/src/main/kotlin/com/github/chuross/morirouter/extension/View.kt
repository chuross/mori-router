package com.github.chuross.morirouter.extension

import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["ext_transitionName"])
fun View.extSetTransitionName(transitionName: String?) {
    transitionName?.let { ViewCompat.setTransitionName(this, it) }
}