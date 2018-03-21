package com.github.chuross.morirouter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    lateinit var router: MoriRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router = MoriRouter(supportFragmentManager, R.id.container)
        router.main("requiredValue1", "requiredValue2").launch()
    }

}
