package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

object ScreenLaunchProcessor {

    fun getGeneratedTypeName(context: ProcessorContext, element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            context.messager.printMessage(Diagnostic.Kind.ERROR, "RouterPath name must be not empty")
            return ""
        }
        return "${routerPathAnnotation.name.capitalize()}ScreenLauncher"
    }

    fun getGeneratedClassName(context: ProcessorContext, element: Element): String {
        return "${context.getPackageName(element)}.${getGeneratedTypeName(context, element)}"
    }

    fun process(context: ProcessorContext, element: Element) {
        val routerTypeSpec = TypeSpec.classBuilder(getGeneratedTypeName(context, element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .build()

        JavaFile.builder(context.getPackageName(element), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }
}