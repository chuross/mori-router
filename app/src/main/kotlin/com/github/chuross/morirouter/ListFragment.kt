package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.WithArguments
import com.github.chuross.morirouter.databinding.FragmentListBinding
import com.github.chuross.morirouter.databinding.ViewListItemBinding
import com.github.chuross.recyclerviewadapters.databinding.BindingViewHolder

@WithArguments
class ListFragment : BaseFragment<FragmentListBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_list
    @Argument
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ListFragmentBinder.bind(this)

        // this argument for demo
        println(name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = ListItemAdapter(context).also {
            val imageUrls = arrayListOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzYOp3yPAai29yvQV91Gf5P4bsnzaiwU7B3mdHg2dVpRHLDZOK",
                    "http://yamura-yasuke.club/yamura/wp-content/uploads/2016/07/himawari20160719.jpg"
            )
            it.onBindThumbnailTransitionName = { position, imageView ->
                ViewCompat.setTransitionName(imageView, "shared_view_${hashCode()}_$position")
            }
            it.addAll(imageUrls.toList())
            it.setOnItemClickListener { holder, _, url ->
                holder.let { it as? BindingViewHolder<*> }
                        ?.let { it.binding as? ViewListItemBinding}
                        ?.also {
                            router?.detail(url, imageUrls)
                                    ?.addSharedElement(it.thumbnailImage)
                                    ?.launch()
                        }
            }
        }
    }
}