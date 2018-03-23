package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterPathParam
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object UriDispatcherProcessor {

    fun process(context: ProcessorContext, elements: Set<Element>) {
        if (elements.isEmpty()) return

        val typeSpec = TypeSpec.classBuilder("UriDispatcher")
                .addModifiers(Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(launchersField())
                .addMethod(constructorMethod(elements))
                .addMethod(dispatchMethod())
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun launchersField(): FieldSpec {
        return FieldSpec.builder(ArrayTypeName.of(ClassName.bestGuess(UriLauncherProcessor.INTERFACE_CLASS_NAME)), "launchers")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun constructorMethod(elements: Set<Element>): MethodSpec {
        val uriLauncherNames = filterRouterPathParamExists(elements).map { UriLauncherProcessor.getGeneratedTypeName(it) }
        val initializeStatement = uriLauncherNames.map { "new $it(router)" }.joinToString(", ")

        return MethodSpec.constructorBuilder()
                .addParameter(ClassName.bestGuess("MoriRouter"), "router")
                .addStatement("this.launchers = new ${UriLauncherProcessor.INTERFACE_CLASS_NAME}[] { $initializeStatement }")
                .build()
    }

    private fun dispatchMethod(): MethodSpec {
        return MethodSpec.methodBuilder("dispatch")
                .addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
                .beginControlFlow("for (${UriLauncherProcessor.INTERFACE_CLASS_NAME} launcher : launchers)")
                .addStatement("if (!launcher.isAvailable(uri)) continue")
                .addStatement("launcher.launch(uri)")
                .addStatement("return")
                .endControlFlow()
                .build()
    }

    private fun filterRouterPathParamExists(elements: Set<Element>): List<Element> {
        return elements.filter {
            it.enclosedElements.find {
                it.getAnnotation(RouterPathParam::class.java) != null
            } != null
        }
    }
}