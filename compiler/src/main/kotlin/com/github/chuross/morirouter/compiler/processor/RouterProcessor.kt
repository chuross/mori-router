package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.util.RouterUtils
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object RouterProcessor {

    fun process(context: ProcessorContext, elements: Set<Element>) {
        val typeSpec = TypeSpec.classBuilder("MoriRouter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(fragmentManagerField())
                .addField(containerIdField())
                .addField(dispatcherField())
                .addMethod(constructorMethod())
                .addMethods(screenLaunchMethods(context, elements).also {
                    // ScreenLauncherを一通り作った後に作る
                    UriDispatcherProcessor.process(context, elements)
                })
                .addMethod(dispatchMethod())
                .addMethod(popMethod())
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun fragmentManagerField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun containerIdField(): FieldSpec {
        return FieldSpec.builder(Int::class.java, "containerId")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun dispatcherField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(UriDispatcherProcessor.TYPE_NAME), "dispatcher")
                .addModifiers(Modifier.PRIVATE)
                .build()

    }

    private fun constructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
                .addParameter(Int::class.java, "containerId")
                .addStatement("this.fm = fm")
                .addStatement("this.containerId = containerId")
                .addStatement("dispatcher = new ${UriDispatcherProcessor.TYPE_NAME}(this)")
                .build()
    }

    private fun screenLaunchMethods(context: ProcessorContext, elements: Set<Element>): Iterable<MethodSpec> {
        return elements.map {
            ScreenLaunchProcessor.process(context, it)
            UriLauncherProcessor.process(context, it)
            BindingProcessor.process(context, it)

            val routerPathAnnotation = it.getAnnotation(RouterPath::class.java)
            val routerParamElements = RouterUtils.getRouterParamElements(it)
            val requiredRouterParamElements = routerParamElements.filter { RouterUtils.isRequiredRouterParam(it) }

            MethodSpec.methodBuilder(routerPathAnnotation.name).also { builder ->
                builder.addModifiers(Modifier.PUBLIC)
                requiredRouterParamElements.forEach {
                    builder.addParameter(TypeName.get(it.asType()), RouterUtils.getRouterParamName(it))
                }
                val arguments = listOf("fm", "containerId").plus(requiredRouterParamElements.map { RouterUtils.getRouterParamName(it) }).joinToString(", ")
                builder.addStatement("return new ${ScreenLaunchProcessor.getGeneratedTypeName(it)}($arguments)")
                builder.returns(ClassName.bestGuess(ScreenLaunchProcessor.getGeneratedTypeName(it)))
            }.build()
        }
    }

    private fun dispatchMethod(): MethodSpec {
        return MethodSpec.methodBuilder("dispatch")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
                .addStatement("dispatcher.dispatch(uri)")
                .build()
    }

    private fun popMethod(): MethodSpec {
        return MethodSpec.methodBuilder("pop")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("fm.popBackStackImmediate()")
                .build()
    }
}