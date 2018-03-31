package com.github.chuross.morirouter

import android.os.Bundle
import android.view.View
import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.WithArguments
import com.github.chuross.morirouter.databinding.FragmentListBinding
import com.github.chuross.morirouter.router.ListFragmentBinder

@WithArguments
class ListFragment : BaseFragment<FragmentListBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_list

    @Argument
    var backgroundResourceId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ListFragmentBinder.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backgroundResourceId?.let { binding.root.setBackgroundResource(it) }
    }
}