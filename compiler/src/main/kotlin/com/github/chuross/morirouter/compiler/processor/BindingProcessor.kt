package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.util.RouterUtils
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

object BindingProcessor {

    fun getGeneratedTypeName(context: ProcessorContext, element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            context.messager.printMessage(Diagnostic.Kind.ERROR, "RouterPath name must be not empty")
            return ""
        }
        return "${routerPathAnnotation.name.capitalize()}ScreenBinder"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.enclosedElements.find { it.getAnnotation(RouterParam::class.java) != null } == null) return

        val routerTypeSpec = TypeSpec.classBuilder(getGeneratedTypeName(context, element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addFields(bundleKeyStaticFields(element))
                .addMethod(bindStaticMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun bundleKeyStaticFields(element: Element): Iterable<FieldSpec> {
        return RouterUtils.getRouterParamElements(element)
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    FieldSpec.builder(String::class.java, RouterUtils.getArgumentKeyName(name))
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"argument_key_$name\"")
                            .build()
                }
    }

    private fun bindStaticMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("bind").also {
            it.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            it.addParameter(TypeName.get(element.asType()), "fragment")
            it.addStatement("${PackageNames.bundle} bundle = fragment.getArguments()")
        }.build()
    }
}