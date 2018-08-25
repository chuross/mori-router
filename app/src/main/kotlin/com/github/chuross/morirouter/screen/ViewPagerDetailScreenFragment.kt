package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.github.chuross.morirouter.*
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentViewpagerDetailBinding
import com.github.chuross.morirouter.transition.ImageSharedTransitionFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@RouterPath(
        name = "viewPagerDetail",
        manualSharedViewNames = ["shared_view_image"],
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
    @Argument(required = false)
    var relayedPosition: AtomicInteger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MoriBinder.bind(this)

        setEnterSharedElementCallback(ViewPagerDetailSharedElementCallBack()
                .sharedViewImage({
                    val currentFragment = adapter?.instantiateItem(binding.viewpager, binding.viewpager.currentItem) as? ImageFragment
                    currentFragment?.binding?.thumbnailImage
                }))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FragmentPagerAdapter(childFragmentManager, imageUrls.mapIndexed { index, url ->
            Pair("$index image", { ImageFragmentBuilder(url).transitionName("${transitionNamePrefix}_$index").build() })
        })

        binding.toolbar.setNavigationOnClickListener { router?.pop() }

        binding.tabLayout.setupWithViewPager(binding.viewpager)

        binding.viewpager.adapter = adapter
        binding.viewpager.setCurrentItem(startIndex, false)
        binding.viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                relayedPosition?.set(position)
            }
        })
    }
}