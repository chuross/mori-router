package com.github.chuross.morirouter.extension

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

@BindingAdapter(value = ["ext_imageUrl"])
fun ImageView.extLoadImage(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .fit()
            .centerInside()
            .into(this)
}