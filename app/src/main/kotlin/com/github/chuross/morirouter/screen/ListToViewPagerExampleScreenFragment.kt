package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.ListItemAdapter
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.ViewPagerDetailSharedElementCallBack
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentListToDetailExampleBinding
import com.github.chuross.morirouter.databinding.ViewListItemBinding
import com.github.chuross.morirouter.util.Data
import com.github.chuross.recyclerviewadapters.databinding.BindingViewHolder
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

@RouterPath(
        name = "listToViewPagerExample"
)
class ListToViewPagerExampleScreenFragment : BaseFragment<FragmentListToDetailExampleBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_list_to_detail_example
    private val selectedPosition: AtomicInteger = AtomicInteger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setExitSharedElementCallback(ViewPagerDetailSharedElementCallBack()
                .sharedViewImage({
                    val position = selectedPosition.get()
                    val viewHolder = binding.list.findViewHolderForAdapterPosition(position) as? BindingViewHolder<*>
                    (viewHolder?.binding as? ViewListItemBinding)?.thumbnailImage
                }))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = ListItemAdapter(context).also {
            it.onBindThumbnailTransitionName = { "shared_image_$it" }
            it.addAll(Data.LIST_DATA)
            it.setOnItemClickListener { _, position, _ ->
                selectedPosition.set(position)

                router?.viewPagerDetail(position, ArrayList(Data.LIST_DATA), "shared_image")
                        ?.relayedPosition(selectedPosition)
                        ?.manualSharedMapping(context)
                        ?.launch()
            }
        }
    }
}