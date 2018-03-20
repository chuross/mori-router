package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.lang.reflect.Type
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

object RouterProcessor {

    fun process(context: ProcessorContext, elements: Set<Element>) {
        val routerTypeSpec = TypeSpec.classBuilder("MoriRouter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(fragmentManagerField())
                .addField(containerIdField())
                .addMethod(constructorMethod())
                .addMethods(screenLaunchMethods(context, elements))
                .addMethod(popMethod())
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun fragmentManagerField(): FieldSpec {
        return FieldSpec.builder(Class.forName(PackageNames.supportFragmentManager), "fm")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun containerIdField(): FieldSpec {
        return FieldSpec.builder(Int::class.java, "containerId")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun constructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Class.forName(PackageNames.supportFragmentManager), "fm")
                .addParameter(Int::class.java, "containerId")
                .addStatement("this.fm = fm")
                .addStatement("this.containerId = containerId")
                .build()
    }

    private fun screenLaunchMethods(context: ProcessorContext, elements: Set<Element>): Iterable<MethodSpec> {
        return elements.map {
            ScreenLaunchProcessor.process(context, it)

            val routerPathAnnotation = it.getAnnotation(RouterPath::class.java)
            val routerParamElements = it.enclosedElements.filter { it.getAnnotation(RouterParam::class.java) != null }
            val requiredRouterParamElements = routerParamElements.filter { it.getAnnotation(RouterParam::class.java).required }

            MethodSpec.methodBuilder(routerPathAnnotation.name).also { builder ->
                builder.addModifiers(Modifier.PUBLIC)
                requiredRouterParamElements.forEach {
                    val annotation = it.getAnnotation(RouterParam::class.java)
                    val name = annotation.name.takeIf { it.isNotBlank() } ?: it.simpleName.toString()
                    builder.addParameter(TypeName.get(it.asType()), name)
                }
                builder.addStatement("new ${ScreenLaunchProcessor.getGeneratedTypeName(context, it)}()")
            }.build()
        }
    }

    private fun popMethod(): MethodSpec {
        return MethodSpec.methodBuilder("pop")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("fm.popBackStackImmediate()")
                .build()
    }
}