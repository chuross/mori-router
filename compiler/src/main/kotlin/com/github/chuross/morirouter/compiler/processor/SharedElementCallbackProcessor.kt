package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.manualSharedViewNames
import com.github.chuross.morirouter.compiler.extension.normalize
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
        return "${element.simpleName}SharedElementCallBack"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.manualSharedViewNames?.isEmpty() ?: true) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(PackageNames.SHARED_ELEMENT_CALLBACK))
                .addJavadoc("This class is auto generated.")
                .addMethods(addSharedElementMethod(element))
                .addField(sharedMappingField())
                .addMethod(onMapSharedElementsMethod())
                .build()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
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
                    .addParameter(callableClassType, "callable")
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
                .addStatement("sharedElements.put(entry.getKey(), entry.getValue().call())")
                .endControlFlow()
                .nextControlFlow("catch (${PackageNames.EXCEPTION} e)")
                .addComment("do nothing")
                .endControlFlow()
                .build()
    }
}