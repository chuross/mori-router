package com.github.chuross.morirouter.compiler.extension

import com.github.chuross.morirouter.annotation.RouterParam
import javax.lang.model.element.Element

fun Element.routerParamName(): String? {
    val annotation = getAnnotation(RouterParam::class.java) ?: return null
    return annotation.name.takeIf { it.isNotBlank() } ?: simpleName.toString()
}