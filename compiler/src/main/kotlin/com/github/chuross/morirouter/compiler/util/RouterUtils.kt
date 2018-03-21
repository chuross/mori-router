package com.github.chuross.morirouter.compiler.util

import com.github.chuross.morirouter.annotation.RouterParam
import javax.lang.model.element.Element

object RouterUtils {

    fun getRouterParamName(element: Element): String {
        return element.getAnnotation(RouterParam::class.java)?.name?.takeIf { it.isNotBlank() } ?: element.simpleName.toString()
    }

    fun getArgumentKeyName(name: String): String {
        return "ARGUMENT_KEY_${name.toUpperCase()}"
    }
}