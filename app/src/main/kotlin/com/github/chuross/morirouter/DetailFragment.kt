package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.View
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentDetailBinding
import com.github.chuross.morirouter.router.DetailFragmentBinder
import com.squareup.picasso.Picasso

@RouterPath(
        name = "detail"
)
class DetailFragment : BaseFragment<FragmentDetailBinding>() {

    @Argument
    lateinit var imageUrl: String

    override val layoutResourceId: Int = R.layout.fragment_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DetailFragmentBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .centerInside()
                .into(binding.itemImage)
    }
}