package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.FragmentPagerAdapter
import com.github.chuross.morirouter.ImageFragmentBuilder
import com.github.chuross.morirouter.ImageViewPagerScreenFragmentBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentImageViewpagerScreenBinding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory

@RouterPath(
        name = "imageViewPager",
        sharedEnterTransitionFactory = ImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ImageSharedTransitionFactory::class
)
class ImageViewPagerScreenFragment : BaseFragment<FragmentImageViewpagerScreenBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_image_viewpager_screen
    private var adapter: FragmentPagerAdapter? = null
    @Argument
    lateinit var imageUrls: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageViewPagerScreenFragmentBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FragmentPagerAdapter(childFragmentManager, imageUrls.mapIndexed { index, url ->
            Pair(index.toString(), { ImageFragmentBuilder(url).build() })
        })
    }

}