package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.DetailScreenFragmentBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentDetailBinding
import com.github.chuross.morirouter.transition.DetailScreenSharedTransitionFactory
import com.github.chuross.morirouter.transition.DetailScreenTransitionFactory
import com.squareup.picasso.Picasso

@RouterPath(
        name = "detail",
        overrideEnterTransitionFactory = DetailScreenTransitionFactory::class,
        overrideExitTransitionFactory = DetailScreenTransitionFactory::class,
        sharedEnterTransitionFactory = DetailScreenSharedTransitionFactory::class
)
class DetailScreenFragment : BaseFragment<FragmentDetailBinding>() {

    @Argument
    lateinit var imageUrl: String

    @Argument
    lateinit var imageUrls: ArrayList<String>

    override val layoutResourceId: Int = R.layout.fragment_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DetailScreenFragmentBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DetailScreenFragmentBinder.bindElement(this, R.id.thumbnail_image)

        binding.toolbar.setNavigationOnClickListener { router?.pop() }

        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .centerInside()
                .into(binding.thumbnailImage)

        binding.nextButton.setOnClickListener {
            router?.imageViewPager(imageUrls)?.launch()
        }
    }
}