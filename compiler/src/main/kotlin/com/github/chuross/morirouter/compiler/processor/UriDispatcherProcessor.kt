package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.Parameters
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.isUriArgument
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object UriDispatcherProcessor {

    const val TYPE_NAME = "UriDispatcher"

    fun process(elements: Set<Element>) {
        val typeSpec = TypeSpec.classBuilder(TYPE_NAME)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(launchersField())
                .addMethod(constructorMethod(elements))
                .addMethod(dispatchMethod())
                .build()

        val context = ProcessorContext.getInstance()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun launchersField(): FieldSpec {
        return FieldSpec.builder(ArrayTypeName.of(ClassName.bestGuess(UriLauncherProcessor.INTERFACE_CLASS_NAME)), "launchers")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun constructorMethod(elements: Set<Element>): MethodSpec {
        val uriLauncherNames = elements
                .filter { it.enclosedElements.any { it.isUriArgument } }
                .map { UriLauncherProcessor.getGeneratedTypeName(it) }

        val initializeStatement = uriLauncherNames.map { "new $it(router)" }.joinToString(", ")

        return MethodSpec.constructorBuilder()
                .addParameter(Parameters.nonNull(ClassName.bestGuess(RouterProcessor.TYPE_NAME), "router"))
                .addStatement("this.launchers = new ${UriLauncherProcessor.INTERFACE_CLASS_NAME}[] { $initializeStatement }")
                .build()
    }

    private fun dispatchMethod(): MethodSpec {
        return MethodSpec.methodBuilder("dispatch")
                .addParameter(Parameters.nullable(ClassName.bestGuess(PackageNames.URI), "uri"))
                .returns(TypeName.BOOLEAN)
                .beginControlFlow("for (${UriLauncherProcessor.INTERFACE_CLASS_NAME} launcher : launchers)")
                .addStatement("if (!launcher.isAvailable(uri)) continue")
                .addStatement("launcher.launch(uri)")
                .addStatement("return true")
                .endControlFlow()
                .addStatement("return false")
                .build()
    }

}