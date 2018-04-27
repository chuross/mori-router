package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.GlideApp
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentDetailBinding
import com.github.chuross.morirouter.transition.DetailScreenSharedTransitionFactory

@RouterPath(
        name = "detail",
        sharedEnterTransitionFactory = DetailScreenSharedTransitionFactory::class,
        sharedExitTransitionFactory = DetailScreenSharedTransitionFactory::class
)
class DetailScreenFragment : BaseFragment<FragmentDetailBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_detail
    @Argument
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MoriBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MoriBinder.bindElement(this, R.id.thumbnail_image)

        binding.toolbar.setNavigationOnClickListener { router?.pop() }

        GlideApp.with(this)
                .load(imageUrl)
                .dontAnimate()
                .fitCenter()
                .centerInside()
                .into(binding.thumbnailImage)
    }
}