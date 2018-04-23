package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.DetailScreenFragmentBinder
import com.github.chuross.morirouter.FragmentPagerAdapter
import com.github.chuross.morirouter.ImageFragment
import com.github.chuross.morirouter.ImageFragmentBuilder
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
    private var adapter: FragmentPagerAdapter? = null
    @Argument
    lateinit var imageUrl: String
    @Argument
    lateinit var imageUrls: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DetailScreenFragmentBinder.bind(this)

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)

                val currentPosition = imageUrls.indexOf(imageUrl)
                val currentImageFragment = adapter?.instantiateItem(binding.viewPager, currentPosition) as? ImageFragment ?: return

                val targetName = names?.firstOrNull() ?: return
                currentImageFragment.view ?: return
                ViewCompat.setTransitionName(currentImageFragment.imageView, targetName)
                sharedElements?.put(targetName, currentImageFragment.imageView)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { router?.pop() }

        adapter = FragmentPagerAdapter(childFragmentManager, imageUrls.map {
            Pair("", {
                ImageFragmentBuilder(it).build()
            })
        })

        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(imageUrls.indexOf(imageUrl), false)
    }
}