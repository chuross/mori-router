package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
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
import javax.tools.Diagnostic

object ScreenLaunchProcessor {

    fun getGeneratedTypeName(context: ProcessorContext, element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            context.messager.printMessage(Diagnostic.Kind.ERROR, "RouterPath name must be not empty")
            return ""
        }
        return "${routerPathAnnotation.name.capitalize()}ScreenLauncher"
    }

    fun process(context: ProcessorContext, element: Element) {
        val routerTypeSpec = TypeSpec.classBuilder(getGeneratedTypeName(context, element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addFields(bundleKeyStaticFields(element))
                .addField(fragmentManagerField())
                .addField(containerIdField())
                .addFields(paramFields(element))
                .addMethod(constructorMethod(element))
                .addMethods(optionalParameterMethods(context, element))
                .addMethod(launchMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun bundleKeyStaticFields(element: Element): Iterable<FieldSpec> {
        return element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    FieldSpec.builder(TypeName.get(it.asType()), "ARGUMENT_KEY_${name.toUpperCase()}")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("\"argument_key_$name\"")
                            .build()
                }
    }

    private fun fragmentManagerField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun containerIdField(): FieldSpec {
        return FieldSpec.builder(TypeName.INT, "containerId")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun paramFields(element: Element): Iterable<FieldSpec> {
        return element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
                .map {
                    FieldSpec.builder(TypeName.get(it.asType()), RouterUtils.getRouterParamName(it))
                            .addModifiers(Modifier.PRIVATE)
                            .build()
                }
    }

    private fun constructorMethod(element: Element): MethodSpec {
        val requiredRouterParamElements = element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
                .filter { it.getAnnotation(RouterParam::class.java).required }

        return MethodSpec.constructorBuilder().also { builder ->
            builder.addParameter(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
            builder.addParameter(TypeName.INT, "containerId")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.containerId = containerId")
            requiredRouterParamElements.forEach {
                val routerParamAnnotation = it.getAnnotation(RouterParam::class.java)
                val name = routerParamAnnotation.name.takeIf { it.isNotBlank() }
                        ?: it.simpleName.toString()
                builder.addParameter(TypeName.get(it.asType()), name)
                builder.addStatement("this.$name = $name")
            }
        }.build()
    }

    private fun optionalParameterMethods(context: ProcessorContext, element: Element): Iterable<MethodSpec> {
        return element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
                .filter { !it.getAnnotation(RouterParam::class.java).required }
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    MethodSpec.methodBuilder(RouterUtils.getRouterParamName(it))
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("this.$name = $name")
                            .addStatement("return this")
                            .returns(ClassName.bestGuess(getGeneratedTypeName(context, element)))
                            .build()
                }
    }

    private fun launchMethod(element: Element): MethodSpec {
        val fragmentClassName = ClassName.get(element.asType())
        val routerParamElements = element.enclosedElements
                .filter { it.getAnnotation(RouterParam::class.java) != null }
        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.bundle} arguments = new ${PackageNames.bundle}()")
            routerParamElements.forEach {
                val name = RouterUtils.getRouterParamName(it)
                builder.addStatement("arguments.putSerializable(ARGUMENT_KEY_${name.toUpperCase()}, $name)")
            }
            builder.addStatement("fragment.setArguments(arguments)")
            builder.addStatement("fm.beginTransaction().replace(containerId, fragment).addToBackStack(null).commit()")
            builder.addStatement("fm.executePendingTransactions()")
        }.build()

    }
}