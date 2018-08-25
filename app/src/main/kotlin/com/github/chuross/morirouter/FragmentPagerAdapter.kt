package com.github.chuross.morirouter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentPagerAdapter(fragmentManager: FragmentManager, private val contents: List<Pair<String, () -> Fragment>>) : FragmentPagerAdapter(fragmentManager) {

    override fun getPageTitle(position: Int): CharSequence? = contents[position].first

    override fun getItem(position: Int): Fragment = contents[position].second.invoke()

    override fun getCount(): Int = contents.size

    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        return super.instantiateItem(container, position) as Fragment
    }
}