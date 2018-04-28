package com.github.chuross.morirouter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.Fade
import com.github.chuross.morirouter.core.DefaultTransitionFactory
import com.github.chuross.morirouter.core.MoriRouterOptions

class MainActivity : AppCompatActivity() {

    lateinit var router: MoriRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = MoriRouterOptions.Builder(R.id.container)
                .setEnterTransitionFactory(DefaultTransitionFactory { Fade() })
                .setExitTransitionFactory(DefaultTransitionFactory { Fade() })
                .build()

        router = MoriRouter(supportFragmentManager, options)
        router.main().launch()
    }

}
