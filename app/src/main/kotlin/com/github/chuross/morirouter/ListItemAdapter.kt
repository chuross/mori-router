package com.github.chuross.morirouter

import android.content.Context
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.chuross.morirouter.databinding.ViewListItemBinding
import com.github.chuross.recyclerviewadapters.ItemAdapter
import com.github.chuross.recyclerviewadapters.databinding.BindingViewHolder

class ListItemAdapter(context: Context) : ItemAdapter<String, BindingViewHolder<ViewListItemBinding>>(context) {

    var onBindThumbnailTransitionName: ((Int) -> String?)? = null

    override fun getAdapterId(): Int = R.layout.view_list_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewListItemBinding> {
        return BindingViewHolder(ViewListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewListItemBinding>, position: Int) {
        onBindThumbnailTransitionName?.invoke(position)?.let {
            ViewCompat.setTransitionName(holder.binding.thumbnailImage, it)
        }

        holder.binding.imageUrl = get(position)
        holder.binding.index = position
        holder.binding.executePendingBindings()
    }

}