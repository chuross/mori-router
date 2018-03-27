package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.transitionNames
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object TransitionNameHelperProcessor {

    const val TYPE_NAME: String = "TransitionNameHelper"

    fun process(context: ProcessorContext, elements: Set<Element>) {
        if (elements.isEmpty()) return

        val transitionNamePaths = elements.filter { it.transitionNames?.isNotEmpty() ?: false }
        if (transitionNamePaths.isEmpty()) return

        val typeSpec = TypeSpec.classBuilder(TYPE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addMethods(transitionNamesStaticMethods(elements))
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun transitionNamesStaticMethods(elements: Set<Element>): Iterable<MethodSpec> {
        return elements.map {
            it.transitionNames?.map {
                MethodSpec.methodBuilder("set${it.capitalize().normalize()}")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .addParameter(ClassName.bestGuess(PackageNames.VIEW), "view")
                        .addStatement("${PackageNames.VIEW_COMPAT}.setTransitionName(view, \"$it\")")
                        .build()
            } ?: emptyList()
        }.flatten()
    }
}