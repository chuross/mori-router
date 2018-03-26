package com.github.chuross.morirouter.compiler.extension

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterUriParam
import javax.lang.model.element.Element

val Element.isRouterParam: Boolean get() = getAnnotation(RouterParam::class.java) != null

val Element.isRequiredRouterParam: Boolean get() = getAnnotation(RouterParam::class.java)?.required ?: false

val Element.isRouterUriParam: Boolean get() = getAnnotation(RouterUriParam::class.java) != null

val Element.pathName: String? get() = getAnnotation(RouterPath::class.java)?.name

val Element.pathUris: Array<String>? get() = getAnnotation(RouterPath::class.java)?.uris

val Element.transitionNames: Array<String>? get() = getAnnotation(RouterPath::class.java).transitionNames

val Element.paramName: String get() {
    return routerParamName ?: routerUriParamName ?: throw IllegalStateException("This element has no RouterParam and RouterUriParam")
}

private val Element.routerParamName: String? get() {
    val annotation = getAnnotation(RouterParam::class.java) ?: return null
    return annotation.name.takeIf { it.isNotBlank() } ?: simpleName.toString()
}

private val Element.routerUriParamName: String? get() {
    val annotation = getAnnotation(RouterUriParam::class.java) ?: return null
    return annotation.name.takeIf { it.isNotBlank() } ?: simpleName.toString()
}

val Element.argumentKeyName: String get() = "ARGUMENT_KEY_${paramName.toUpperCase()}"

val Element.paramElements: List<Element> get() = routerParamElements.plus(routerUriParamElements)

val Element.routerParamElements: List<Element> get() = enclosedElements.filter { it.getAnnotation(RouterParam::class.java) != null }

val Element.routerUriParamElements: List<Element> get() = enclosedElements.filter { it.getAnnotation(RouterUriParam::class.java) != null }