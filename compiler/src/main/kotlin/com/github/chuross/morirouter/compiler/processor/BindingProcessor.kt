package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
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

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(context, element))
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
        return RouterUtils.getRouterParamElements(element)
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    FieldSpec.builder(String::class.java, RouterUtils.getArgumentKeyName(name))
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"argument_key_$name\"")
                            .build()
                }
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
            RouterUtils.getRouterParamElements(element).forEach {
                val setterMethodName = "set${it.simpleName.toString().capitalize()}"
                val setterMethod = element.enclosedElements.find {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == setterMethodName
                }

                val routerParamName = RouterUtils.getRouterParamName(it)
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