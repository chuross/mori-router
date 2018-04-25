package com.github.chuross.morirouter.compiler.extension

import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.UriArgument
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException

val Element.isRouterPath: Boolean get() = getAnnotation(RouterPath::class.java) != null

val Element.isArgument: Boolean get() = getAnnotation(Argument::class.java) != null

val Element.isRequiredArgument: Boolean get() = getAnnotation(Argument::class.java)?.required ?: false

val Element.isUriArgument: Boolean get() = getAnnotation(UriArgument::class.java) != null

val Element.pathName: String? get() = getAnnotation(RouterPath::class.java)?.name

val Element.pathUris: Array<String>? get() = getAnnotation(RouterPath::class.java)?.uris

val Element.needManualSharedMapping: Boolean get() = getAnnotation(RouterPath::class.java)?.needManualSharedMapping ?: false

val Element.sharedEnterTransitionFactoryName: String? get() = try { getAnnotation(RouterPath::class.java)?.sharedEnterTransitionFactory?.qualifiedName } catch (e: MirroredTypeException) { e.typeMirror?.toString() }

val Element.sharedExitTransitionFactoryName: String? get() = try { getAnnotation(RouterPath::class.java)?.sharedExitTransitionFactory?.qualifiedName } catch (e: MirroredTypeException) { e.typeMirror?.toString() }

val Element.overrideEnterTransitionFactoryName: String? get() = try { getAnnotation(RouterPath::class.java)?.overrideEnterTransitionFactory?.qualifiedName } catch (e: MirroredTypeException) { e.typeMirror?.toString() }

val Element.overrideExitTransitionFactoryName: String? get() = try { getAnnotation(RouterPath::class.java)?.overrideExitTransitionFactory?.qualifiedName } catch (e: MirroredTypeException) { e.typeMirror?.toString() }

val Element.paramName: String get() {
    return argumentName ?: uriArgumentName ?: throw IllegalStateException("This element has no Argument and UriArgument")
}

private val Element.argumentName: String? get() {
    val annotation = getAnnotation(Argument::class.java) ?: return null
    return annotation.name.takeIf { it.isNotBlank() } ?: simpleName.toString()
}

private val Element.uriArgumentName: String? get() {
    val annotation = getAnnotation(UriArgument::class.java) ?: return null
    return annotation.name.takeIf { it.isNotBlank() } ?: simpleName.toString()
}

val Element.argumentKeyName: String get() = "ARGUMENT_KEY_${paramName.toUpperCase()}"

val Element.allArgumentElements: List<Element> get() = argumentElements.plus(uriArgumentElements)

val Element.argumentElements: List<Element> get() = enclosedElements.filter { it.getAnnotation(Argument::class.java) != null }

val Element.uriArgumentElements: List<Element> get() = enclosedElements.filter { it.getAnnotation(UriArgument::class.java) != null }