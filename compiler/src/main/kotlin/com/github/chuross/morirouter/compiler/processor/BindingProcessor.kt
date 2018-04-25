package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.allArgumentElements
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.isRouterPath
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.paramName
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

    const val SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT: String = "shared_view_%d"

    fun getGeneratedTypeName(element: Element): String {
        return "${element.simpleName}Binder"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.allArgumentElements.isEmpty() && !element.isRouterPath) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element)).also {
            it.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            it.addJavadoc("This class is auto generated.")
            it.addFields(bundleKeyStaticFields(element))
            it.addMethod(constructorMethod())
            if (element.allArgumentElements.isNotEmpty()) it.addMethod(bindStaticMethod(element))
            if (element.isRouterPath) {
                it.addMethod(getSharedTransitionNameMethod(element))
                it.addMethod(bindElementStaticMethod(element))
            }
        }.build()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun bundleKeyStaticFields(element: Element): Iterable<FieldSpec> {
        return element.allArgumentElements.map {
            FieldSpec.builder(String::class.java, it.argumentKeyName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"argument_key_${it.paramName.toLowerCase()}\"")
                    .build()
        }
    }

    private fun constructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun getSharedTransitionNameMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("getSharedTransitionName").also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.returns(String::class.java)
            builder.addParameter(TypeName.get(element.asType()), "fragment")
            builder.addParameter(TypeName.INT, "resourceId")

            builder.addStatement("${PackageNames.BUNDLE} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return null")

            builder.addStatement("return bundle.getString(String.format(\"$SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT\", resourceId))")
        }.build()
    }

    private fun bindStaticMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("bind").also { builder ->
            builder.addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"unchecked\"").build())
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.addParameter(TypeName.get(element.asType()), "fragment")

            builder.addStatement("${PackageNames.BUNDLE} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return")

            element.allArgumentElements.forEach {
                val setterMethodName = "set${it.simpleName.toString().capitalize()}"
                val setterMethod = element.enclosedElements.find {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == setterMethodName
                }

                val valueName = "${it.paramName.normalize()}Value"
                builder.addStatement("${PackageNames.SERIALIZABLE} $valueName = bundle.getSerializable(${it.argumentKeyName})")

                builder.addStatement(if (setterMethod == null) {
                    "if ($valueName != null) fragment.${it.simpleName} = (${it.asType()}) $valueName"
                } else {
                    "if ($valueName != null) fragment.$setterMethodName((${it.asType()}) $valueName)"
                })
            }
        }.build()
    }

    private fun bindElementStaticMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("bindElement").also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.addParameter(TypeName.get(element.asType()), "fragment")
            builder.addParameter(TypeName.INT, "resourceId")

            builder.addStatement("if (fragment.getView() == null) throw new ${PackageNames.ILLEGAL_STATE_EXCEPTION}(\"you must call onViewCreated\")")

            builder.addStatement("String transitionName = getSharedTransitionName(fragment, resourceId)")
            builder.addStatement("if (transitionName == null) return")

            builder.addStatement("${PackageNames.VIEW} targetView = fragment.getView().findViewById(resourceId)")
            builder.addStatement("if (targetView == null) throw new ${PackageNames.ILLEGAL_ARGUMENT_EXCEPTION}(\"target view not found\")")
            builder.addStatement("${PackageNames.VIEW_COMPAT}.setTransitionName(targetView, transitionName)")
        }.build()
    }

}