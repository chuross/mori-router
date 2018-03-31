package com.github.chuross.morirouter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class FragmentPagerAdapter(fragmentManager: FragmentManager, private val contents: List<Pair<String, () -> Fragment>>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = contents[position].second.invoke()

    override fun getCount(): Int = contents.size
}