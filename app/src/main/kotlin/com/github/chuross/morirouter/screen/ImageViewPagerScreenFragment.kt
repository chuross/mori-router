package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.FragmentPagerAdapter
import com.github.chuross.morirouter.ImageFragment
import com.github.chuross.morirouter.ImageFragmentBuilder
import com.github.chuross.morirouter.ImageViewPagerScreenFragmentBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentImageViewpagerScreenBinding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory

@RouterPath(
        name = "imageViewPager",
        isReorderingAllowed = true,
        sharedEnterTransitionFactory = ImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ImageSharedTransitionFactory::class
)
class ImageViewPagerScreenFragment : BaseFragment<FragmentImageViewpagerScreenBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_image_viewpager_screen
    private var adapter: FragmentPagerAdapter? = null
    @Argument
    lateinit var imageUrls: ArrayList<String>
    @Argument
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageViewPagerScreenFragmentBinder.bind(this)

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)

                println("onMapSharedElements")

                val currentPosition = imageUrls.indexOf(imageUrl)
                val currentImageFragment = adapter?.instantiateItem(binding.viewPager, currentPosition) as? ImageFragment ?: return

                val targetName = names?.firstOrNull() ?: return
                ViewCompat.setTransitionName(currentImageFragment.imageView, targetName)

                sharedElements?.put(targetName, currentImageFragment.imageView)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FragmentPagerAdapter(childFragmentManager, imageUrls.mapIndexed { index, url ->
            Pair(index.toString(), { ImageFragmentBuilder(url).build() })
        })

        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(imageUrls.indexOf(imageUrl), false)

        postponeEnterTransition()
    }

}