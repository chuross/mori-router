package com.github.chuross.morirouter.compiler.util

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterUriParam
import javax.lang.model.element.Element

object RouterUtils {

    fun getRouterParamName(element: Element): String {
        val annotation = element.getAnnotation(RouterParam::class.java) ?: throw IllegalArgumentException("Element has no RouterParam")
        return annotation.name.takeIf { it.isNotBlank() } ?: element.simpleName.toString()
    }

    fun getRouterPathParamName(element: Element): String {
        val annotation = element.getAnnotation(RouterUriParam::class.java) ?: throw IllegalArgumentException("Element has no RouterParam")
        return annotation.name.takeIf { it.isNotBlank() } ?: element.simpleName.toString()
    }

    fun getArgumentKeyName(name: String): String {
        return "ARGUMENT_KEY_${name.toUpperCase()}"
    }

    fun getRouterParamElements(element: Element): List<Element> {
        return element.enclosedElements.filter { it.getAnnotation(RouterParam::class.java) != null }
    }

    fun getRouterPathParamElements(element: Element): List<Element> {
        return element.enclosedElements.filter { it.getAnnotation(RouterUriParam::class.java) != null }
    }

    fun isRequiredRouterParam(element: Element): Boolean {
        return element.getAnnotation(RouterParam::class.java)?.required ?: false
    }
}