package com.github.chuross.morirouter

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.WithArguments
import com.github.chuross.morirouter.databinding.FragmentImageBinding

@WithArguments
class ImageFragment : BaseFragment<FragmentImageBinding>() {

    @Argument
    lateinit var imageUrl: String

    override val layoutResourceId: Int = R.layout.fragment_image

    val imageView: ImageView get() = binding.thumbnailImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageFragmentBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}