package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.isRequiredRouterParam
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.routerParamElements
import com.github.chuross.morirouter.compiler.extension.routerUriParamElements
import com.github.chuross.morirouter.core.MoriRouterOptions
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object RouterProcessor {

    const val TYPE_NAME: String = "MoriRouter"

    fun process(context: ProcessorContext, elements: Set<Element>) {
        if (elements.isEmpty()) return

        val typeSpec = TypeSpec.classBuilder(TYPE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(fragmentManagerField())
                .addField(optionsField())
                .addField(dispatcherField())
                .addMethod(constructorMethod(elements))
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
        return FieldSpec.builder(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT_MANAGER), "fm")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun optionsField(): FieldSpec {
        return FieldSpec.builder(MoriRouterOptions::class.java, "options")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun dispatcherField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(UriDispatcherProcessor.TYPE_NAME), "dispatcher")
                .addModifiers(Modifier.PRIVATE)
                .build()

    }

    private fun constructorMethod(elements: Set<Element>): MethodSpec {
        return MethodSpec.constructorBuilder().also { builder ->
            builder.addModifiers(Modifier.PUBLIC)
            builder.addParameter(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT_MANAGER), "fm")
            builder.addParameter(MoriRouterOptions::class.java, "options")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.options = options")
            if (elements.any { it.routerUriParamElements.isNotEmpty() }) {
                builder.addStatement("dispatcher = new ${UriDispatcherProcessor.TYPE_NAME}(this)")
            }
        }.build()
    }

    private fun screenLaunchMethods(context: ProcessorContext, elements: Set<Element>): Iterable<MethodSpec> {
        return elements.map {
            ScreenLaunchProcessor.process(context, it)
            UriLauncherProcessor.process(context, it)
            BindingProcessor.process(context, it)

            val requiredRouterParamElements = it.routerParamElements.filter { it.isRequiredRouterParam }

            MethodSpec.methodBuilder(it.pathName?.normalize()).also { builder ->
                builder.addModifiers(Modifier.PUBLIC)
                requiredRouterParamElements.forEach {
                    builder.addParameter(TypeName.get(it.asType()), it.paramName.normalize())
                }
                val arguments = listOf("fm", "options").plus(requiredRouterParamElements.map { it.paramName.normalize() }).joinToString(", ")
                builder.addStatement("return new ${ScreenLaunchProcessor.getGeneratedTypeName(it)}($arguments)")
                builder.returns(ClassName.bestGuess(ScreenLaunchProcessor.getGeneratedTypeName(it)))
            }.build()
        }
    }

    private fun dispatchMethod(): MethodSpec {
        return MethodSpec.methodBuilder("dispatch")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(ClassName.bestGuess(PackageNames.URI), "uri")
                .addStatement("return dispatcher.dispatch(uri)")
                .build()
    }

    private fun popMethod(): MethodSpec {
        return MethodSpec.methodBuilder("pop")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("fm.popBackStackImmediate()")
                .build()
    }
}