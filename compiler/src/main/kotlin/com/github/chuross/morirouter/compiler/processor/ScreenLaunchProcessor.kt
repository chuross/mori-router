package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
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

    fun process(context: ProcessorContext, element: Element) {
        val routerTypeSpec = TypeSpec.classBuilder(getGeneratedTypeName(context, element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addMethod(constructorMethod(context, element))
                .build()

        JavaFile.builder(context.getPackageName(element), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun constructorMethod(context: ProcessorContext, element: Element): MethodSpec {
        val requiredRouterParamElements = element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
                .filter { it.getAnnotation(RouterParam::class.java).required }

        return MethodSpec.constructorBuilder().also { builder ->
            requiredRouterParamElements.forEach {
                val routerParamAnnotation = it.getAnnotation(RouterParam::class.java)
                val name = routerParamAnnotation.name.takeIf { it.isNotBlank() } ?: it.simpleName.toString()
                builder.addParameter(TypeName.get(it.asType()), name)
            }
        }.build()
    }
}