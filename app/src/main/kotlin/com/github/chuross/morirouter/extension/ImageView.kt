package com.github.chuross.morirouter.extension

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.github.chuross.morirouter.GlideApp

@BindingAdapter(value = ["ext_imageUrl"])
fun ImageView.extLoadImage(imageUrl: String) {
    GlideApp.with(this)
            .load(imageUrl)
            .fitCenter()
            .centerInside()
            .into(this)
}