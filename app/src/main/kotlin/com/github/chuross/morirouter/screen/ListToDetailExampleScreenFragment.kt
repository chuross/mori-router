package com.github.chuross.morirouter.screen

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.chuross.morirouter.BaseFragment
import com.github.chuross.morirouter.ListItemAdapter
import com.github.chuross.morirouter.R
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.databinding.FragmentListToDetailExampleBinding
import com.github.chuross.morirouter.util.Data

@RouterPath(
        name = "listToDetailExample"
)
class ListToDetailExampleScreenFragment : BaseFragment<FragmentListToDetailExampleBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_list_to_detail_example

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = ListItemAdapter(context).also {
            val imageUrls = Data.LIST_DATA
            it.onBindThumbnailTransitionName = { "shared_image_$it" }
            it.addAll(imageUrls.toList())
            it.setOnItemClickListener { holder, _, url ->
                router?.detail(url)
                        ?.addSharedElement(holder.itemView.findViewById(R.id.thumbnail_image))
                        ?.launch()
            }
        }
    }
}