package com.github.chuross.morirouter

import android.content.Context
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.chuross.morirouter.databinding.ViewListItemBinding
import com.github.chuross.recyclerviewadapters.ItemAdapter
import com.github.chuross.recyclerviewadapters.databinding.BindingViewHolder

class ListItemAdapter(context: Context, private val identity: String) : ItemAdapter<String, BindingViewHolder<ViewListItemBinding>>(context) {

    override fun getAdapterId(): Int = R.layout.view_list_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewListItemBinding> {
        return BindingViewHolder(ViewListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewListItemBinding>, position: Int) {
        holder.binding.thumbnailImage.also {
            ViewCompat.setTransitionName(it, String.format("shared_view_%s_%d_%d", identity, position, it.id))
        }

        holder.binding.imageUrl = get(position)
        holder.binding.index = position
        holder.binding.executePendingBindings()
    }

}