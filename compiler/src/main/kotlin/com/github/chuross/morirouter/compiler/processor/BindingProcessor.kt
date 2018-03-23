package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.paramElements
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.pathName
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
        if (element.pathName.isNullOrBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${element.pathName?.normalize()?.capitalize()}ScreenBinder"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.paramElements.isEmpty()) return

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
        return element.paramElements.map {
            FieldSpec.builder(String::class.java, it.argumentKeyName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"argument_key_${it.paramName}\"")
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
            builder.addStatement("if (bundle == null) return")

            element.paramElements.forEach {
                val setterMethodName = "set${it.simpleName.toString().capitalize()}"
                val setterMethod = element.enclosedElements.find {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == setterMethodName
                }

                val valueName = "${it.paramName.normalize()}Value"
                builder.addStatement("${PackageNames.serializable} $valueName = bundle.getSerializable(${it.argumentKeyName})")

                builder.addStatement(if (setterMethod == null) {
                    "if ($valueName != null) fragment.${it.simpleName} = (${it.asType()}) $valueName"
                } else {
                    "if ($valueName != null) fragment.$setterMethodName((${it.asType()}) $valueName)"
                })
            }
        }.build()
    }

}