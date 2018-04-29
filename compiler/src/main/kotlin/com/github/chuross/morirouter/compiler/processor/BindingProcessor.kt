package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.Parameters
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.allArgumentElements
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.isParcelableType
import com.github.chuross.morirouter.compiler.extension.isRouterPath
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.paramName
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier

object BindingProcessor {

    const val AUTO_BINDER_TYPE_NAME: String = "MoriBinder"
    const val SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT: String = "shared_view_%d"

    fun getGeneratedTypeName(element: Element): String {
        return "${element.simpleName}Binder"
    }

    fun processAutoBinder(elements: Set<Element>) {
        if (elements.all { it.allArgumentElements.isEmpty() && !it.isRouterPath }) return

        val typeSpec =  TypeSpec.classBuilder(AUTO_BINDER_TYPE_NAME)
                .addJavadoc("This class is auto generated.")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getSharedTransitionNameMethod())
                .addMethod(autoBindStaticMethod(elements.filter { it.allArgumentElements.isNotEmpty() }.toSet()))
                .addMethod(autoBindElementStaticMethod(elements.filter { it.isRouterPath }.toSet()))
                .build()

        val context = ProcessorContext.getInstance()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun getSharedTransitionNameMethod(): MethodSpec {
        return MethodSpec.methodBuilder("getSharedTransitionName").also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.returns(String::class.java)
            builder.addParameter(Parameters.nonNull(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT), "fragment"))
            builder.addParameter(Parameters.resId(TypeName.INT, "resourceId"))

            builder.addStatement("${PackageNames.BUNDLE} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return null")

            builder.addStatement("return bundle.getString(String.format(\"$SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT\", resourceId))")
        }.build()
    }

    private fun autoBindStaticMethod(elements: Set<Element>): MethodSpec {
        return MethodSpec.methodBuilder("bind").also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.addParameter(Parameters.nonNull(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT), "fragment"))

            builder.addStatement("if (fragment == null) throw new ${PackageNames.ILLEGAL_ARGUMENT_EXCEPTION}(\"fragment must be not null\")")

            elements.forEach {
                builder.beginControlFlow("if (${it.asType()}.class.equals(fragment.getClass()))")
                builder.addStatement("${getGeneratedTypeName(it)}.bind((${it.asType()}) fragment)")
                builder.endControlFlow()
            }
        }.build()
    }

    private fun autoBindElementStaticMethod(elements: Set<Element>): MethodSpec {
        return MethodSpec.methodBuilder("bindElement").also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            builder.addParameter(Parameters.nonNull(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT), "fragment"))
            builder.addParameter(Parameters.resId(TypeName.INT, "resourceId"))

            builder.addStatement("if (fragment == null) throw new ${PackageNames.ILLEGAL_ARGUMENT_EXCEPTION}(\"fragment must be not null\")")

            elements.forEach {
                builder.beginControlFlow("if (${it.asType()}.class.equals(fragment.getClass()))")
                builder.addStatement("${getGeneratedTypeName(it)}.bindElement((${it.asType()}) fragment, resourceId)")
                builder.endControlFlow()
            }
        }.build()
    }

    fun process(element: Element) {
        if (element.allArgumentElements.isEmpty() && !element.isRouterPath) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element)).also {
            it.addModifiers(Modifier.FINAL)
            it.addJavadoc("This class is auto generated.")
            it.addFields(bundleKeyStaticFields(element))
            it.addMethod(constructorMethod())
            if (element.allArgumentElements.isNotEmpty()) it.addMethod(bindStaticMethod(element))
            if (element.isRouterPath) {
                it.addMethod(bindElementStaticMethod(element))
            }
        }.build()

        val context = ProcessorContext.getInstance()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun bundleKeyStaticFields(element: Element): Iterable<FieldSpec> {
        return element.allArgumentElements.map {
            FieldSpec.builder(String::class.java, it.argumentKeyName)
                    .addModifiers(Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"argument_key_${it.paramName.toLowerCase()}\"")
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
            builder.addParameter(Parameters.nonNull(TypeName.get(element.asType()), "fragment"))

            builder.addStatement("${PackageNames.BUNDLE} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return")

            element.allArgumentElements.forEach {
                val setterMethodName = "set${it.simpleName.toString().capitalize()}"
                val setterMethod = element.enclosedElements.find {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == setterMethodName
                }

                val valueName = "${it.paramName.normalize()}Value"
                if (it.asType().isParcelableType()) {
                    builder.addStatement("${PackageNames.PARCELABLE} $valueName = bundle.getParcelable(${it.argumentKeyName})")
                } else {
                    builder.addStatement("${PackageNames.SERIALIZABLE} $valueName = bundle.getSerializable(${it.argumentKeyName})")
                }

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
            builder.addParameter(Parameters.nonNull(TypeName.get(element.asType()), "fragment"))
            builder.addParameter(Parameters.resId(TypeName.INT, "resourceId"))

            builder.addStatement("if (fragment.getView() == null) throw new ${PackageNames.ILLEGAL_STATE_EXCEPTION}(\"you must call onViewCreated\")")

            builder.addStatement("${PackageNames.BUNDLE} bundle = fragment.getArguments()")
            builder.addStatement("if (bundle == null) return")

            builder.addStatement("String transitionName = bundle.getString(String.format(\"$SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT\", resourceId))")
            builder.addStatement("if (transitionName == null) return")

            builder.addStatement("${PackageNames.VIEW} targetView = fragment.getView().findViewById(resourceId)")
            builder.addStatement("if (targetView == null) throw new ${PackageNames.ILLEGAL_ARGUMENT_EXCEPTION}(\"target view not found\")")
            builder.addStatement("${PackageNames.VIEW_COMPAT}.setTransitionName(targetView, transitionName)")
        }.build()
    }

}