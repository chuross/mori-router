package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterPathParam
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.util.RouterUtils
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier

object BindingProcessor {

    fun getGeneratedTypeName(element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${routerPathAnnotation.name.capitalize()}ScreenBinder"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.enclosedElements.find {
                    it.getAnnotation(RouterParam::class.java) != null
                    || it.getAnnotation(RouterPathParam::class.java) != null
                } == null
        ) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addFields(bundleKeyStaticFields(element))
                .addMethod(constructorMethod())
                .addMethod(bindStaticMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun bundleKeyStaticFields(element: Element): Iterable<FieldSpec> {
        val routerParamFields = RouterUtils.getRouterParamElements(element)
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    FieldSpec.builder(String::class.java, RouterUtils.getArgumentKeyName(name))
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"argument_key_$name\"")
                            .build()
                }

        val routerPathParamFields = RouterUtils.getRouterPathParamElements(element)
                .map {
                    val name = RouterUtils.getRouterPathParamName(it)
                    FieldSpec.builder(String::class.java, RouterUtils.getArgumentKeyName(name))
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"argument_key_$name\"")
                            .build()
                }

        return routerParamFields.plus(routerPathParamFields)
    }

    private fun constructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun bindStaticMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("bind").also { builder ->
            builder.addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"unchecked\"").build())
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.addParameter(TypeName.get(element.asType()), "fragment")
            builder.addStatement("${PackageNames.bundle} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return")

            RouterUtils.getRouterParamElements(element).plus(RouterUtils.getRouterPathParamElements(element)).forEach {
                val setterMethodName = "set${it.simpleName.toString().capitalize()}"
                val setterMethod = element.enclosedElements.find {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == setterMethodName
                }

                val routerParamName = if (it.getAnnotation(RouterParam::class.java) != null) RouterUtils.getRouterParamName(it) else RouterUtils.getRouterPathParamName(it)
                val valueName = "${routerParamName}Value"
                builder.addStatement("${PackageNames.serializable} $valueName = bundle.getSerializable(${RouterUtils.getArgumentKeyName(routerParamName)})")

                builder.addStatement(if (setterMethod == null) {
                    "if ($valueName != null) fragment.${it.simpleName} = (${it.asType()}) $valueName"
                } else {
                    "if ($valueName != null) fragment.$setterMethodName((${it.asType()}) $valueName)"
                })
            }
        }.build()
    }

}