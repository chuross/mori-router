package com.github.chuross.morirouter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.Fade
import com.github.chuross.morirouter.core.MoriRouterOptions

class MainActivity : AppCompatActivity() {

    lateinit var router: MoriRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router = MoriRouter(supportFragmentManager, MoriRouterOptions.Builder(R.id.container)
                .setEnterTransition(Fade())
                .setExitTransition(Fade())
                .build())
        router.basic("requiredValue1", "requiredValue2").launch()
    }

}
