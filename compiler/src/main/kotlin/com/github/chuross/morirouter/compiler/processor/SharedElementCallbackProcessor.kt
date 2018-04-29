package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.Parameters
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.isRouterPath
import com.github.chuross.morirouter.compiler.extension.manualSharedViewNames
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.pathName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object SharedElementCallbackProcessor {

    fun getGeneratedTypeName(element: Element): String {
        return "${element.pathName?.capitalize()}SharedElementCallBack"
    }

    fun process(element: Element) {
        if (!element.isRouterPath) return
        if (element.manualSharedViewNames?.isEmpty() ?: true) return

        validate(element)

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(PackageNames.SHARED_ELEMENT_CALLBACK))
                .addJavadoc("This class is auto generated.")
                .addMethods(addSharedElementMethod(element))
                .addField(sharedMappingField())
                .addMethod(onMapSharedElementsMethod())
                .build()

        val context = ProcessorContext.getInstance()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun validate(element: Element) {
        element.manualSharedViewNames?.forEach {
            if (it.isBlank()) {
                throw IllegalStateException("manualSharedViewName is null: ${element.simpleName}")
            }
        }
    }

    private fun sharedMappingField(): FieldSpec {
        val callableClassType = ParameterizedTypeName.get(ClassName.bestGuess(PackageNames.CALLABLE), ClassName.bestGuess(PackageNames.VIEW))
        val mapClassType = ParameterizedTypeName.get(ClassName.bestGuess(PackageNames.MAP), ClassName.get(String::class.java), callableClassType)
        return FieldSpec.builder(mapClassType, "sharedMapping")
                .addModifiers(Modifier.PRIVATE)
                .initializer("new ${PackageNames.HASH_MAP}<>()")
                .build()
    }

    private fun addSharedElementMethod(element: Element): Iterable<MethodSpec> {
        val callableClassType = ParameterizedTypeName.get(ClassName.bestGuess(PackageNames.CALLABLE), ClassName.bestGuess(PackageNames.VIEW))

        return element.manualSharedViewNames?.map {
            MethodSpec.methodBuilder(it.normalize())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.bestGuess(getGeneratedTypeName(element)))
                    .addParameter(Parameters.nonNull(callableClassType, "callable"))
                    .addStatement("sharedMapping.put(\"$it\", callable)")
                    .addStatement("return this")
                    .build()
        } ?: emptyList()
    }

    private fun onMapSharedElementsMethod(): MethodSpec {
        val listClassType = ParameterizedTypeName.get(ClassName.bestGuess(PackageNames.LIST), ClassName.get(String::class.java))
        val mapClassType = ParameterizedTypeName.get(ClassName.bestGuess(PackageNames.MAP), ClassName.get(String::class.java), ClassName.bestGuess(PackageNames.VIEW))

        return MethodSpec.methodBuilder("onMapSharedElements")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listClassType, "names")
                .addParameter(mapClassType, "sharedElements")
                .addStatement("sharedElements.clear()")
                .beginControlFlow("try")
                .beginControlFlow("for (${PackageNames.MAP}.Entry<${String::class.java.name}, ${PackageNames.CALLABLE}<${PackageNames.VIEW}>> entry : sharedMapping.entrySet())")
                .addStatement("${PackageNames.VIEW} view = entry.getValue().call()")
                .addStatement("if (view == null) continue")
                .addStatement("sharedElements.put(entry.getKey(), view)")
                .endControlFlow()
                .nextControlFlow("catch (${PackageNames.EXCEPTION} e)")
                .addStatement("throw new ${PackageNames.ILLEGAL_STATE_EXCEPTION}(\"shared element mapping failed\", e)")
                .endControlFlow()
                .build()
    }
}