package com.github.chuross.morirouter

import android.support.v4.app.Fragment
import com.github.chuross.morirouter.annotation.RouterPath

@RouterPath(
        name = "main",
        uri = "morirouter://main"
)
class MainFragment : Fragment() {
}