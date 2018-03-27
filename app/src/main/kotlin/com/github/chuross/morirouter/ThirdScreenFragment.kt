package com.github.chuross.morirouter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.chuross.morirouter.annotation.RouterPath

@RouterPath(
        name = "third_ouie",
        transitionNames = [
            "icon_image"
        ],
        enterTransitionFactory = ThirdScreenTransitionFactory::class,
        exitTransitionFactory = ThirdScreenTransitionFactory::class
)
class ThirdScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTransitionName(view?.findViewById<ImageView>(R.id.app_icon_image), ThirdOuieScreenLauncher.ICON_IMAGE)
    }
}