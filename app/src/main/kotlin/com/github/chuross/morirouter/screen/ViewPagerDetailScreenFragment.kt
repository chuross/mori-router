package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.support.v4.app.SharedElementCallback
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.FragmentPagerAdapter
import com.github.chuross.morirouter.ImageFragment
import com.github.chuross.morirouter.ImageFragmentBuilder
import com.github.chuross.morirouter.MoriBinder
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentViewpagerDetailBinding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory
import java.util.ArrayList

@RouterPath(
        name = "viewPagerDetail",
        sharedEnterTransitionFactory = ImageSharedTransitionFactory::class,
        sharedExitTransitionFactory = ImageSharedTransitionFactory::class
)
class ViewPagerDetailScreenFragment : BaseFragment<FragmentViewpagerDetailBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_viewpager_detail
    private var adapter: FragmentPagerAdapter? = null
    @Argument
    var startIndex: Int = 0
    @Argument
    lateinit var imageUrls: ArrayList<String>
    @Argument
    lateinit var transitionNamePrefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MoriBinder.bind(this)

        setEnterSharedElementCallback(object : SharedElementCallback() {

            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)

                val currentFragment = adapter?.instantiateItem(binding.viewpager, binding.viewpager.currentItem) as? ImageFragment ?: return
                val transitionName = MoriBinder.getSharedTransitionName(this@ViewPagerDetailScreenFragment, R.id.thumbnail_image) ?: return

                sharedElements?.put(transitionName, currentFragment.binding.thumbnailImage)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FragmentPagerAdapter(childFragmentManager, imageUrls.mapIndexed { index, url ->
            Pair("", { ImageFragmentBuilder(url).transitionName("${transitionNamePrefix}_$index").build() })
        })

        binding.viewpager.adapter = adapter
        binding.viewpager.setCurrentItem(startIndex, false)
    }
}