package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterPathParam
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

object ScreenLaunchProcessor {

    fun getGeneratedTypeName(element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${routerPathAnnotation.name.capitalize()}ScreenLauncher"
    }

    fun process(context: ProcessorContext, element: Element) {
        validate(element)

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(fragmentManagerField())
                .addField(containerIdField())
                .addFields(paramFields(element))
                .addMethod(constructorMethod(element))
                .addMethods(optionalParameterMethods(element))
                .addMethod(launchMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun validate(element: Element) {
        val requiredParamElement = element.enclosedElements.find { it.getAnnotation(RouterParam::class.java)?.required ?: false }
        val pathParamElement = element.enclosedElements.find { it.getAnnotation(RouterPathParam::class.java) != null }

        if (requiredParamElement != null && pathParamElement != null) {
            throw IllegalStateException("required RouterParam can use only no RouterPathParam")
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
        val requiredRouterParamElements = RouterUtils.getRouterParamElements(element)
                .filter { RouterUtils.isRequiredRouterParam(it) }

        return MethodSpec.constructorBuilder().also { builder ->
            builder.addParameter(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
            builder.addParameter(TypeName.INT, "containerId")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.containerId = containerId")
            requiredRouterParamElements.forEach {
                val name = RouterUtils.getRouterParamName(it)
                builder.addParameter(TypeName.get(it.asType()), name)
                builder.addStatement("this.$name = $name")
            }
        }.build()
    }

    private fun optionalParameterMethods(element: Element): Iterable<MethodSpec> {
        return RouterUtils.getRouterParamElements(element)
                .filter { !RouterUtils.isRequiredRouterParam(it) }
                .map {
                    val name = RouterUtils.getRouterParamName(it)
                    MethodSpec.methodBuilder(RouterUtils.getRouterParamName(it))
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeName.get(it.asType()), name)
                            .addStatement("this.$name = $name")
                            .addStatement("return this")
                            .returns(ClassName.bestGuess(getGeneratedTypeName(element)))
                            .build()
                }
    }

    private fun launchMethod(element: Element): MethodSpec {
        val fragmentClassName = ClassName.get(element.asType())
        val routerParamElements = RouterUtils.getRouterParamElements(element)
        val binderTypeName = BindingProcessor.getGeneratedTypeName(element)

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.bundle} arguments = new ${PackageNames.bundle}()")
            routerParamElements.forEach {
                val name = RouterUtils.getRouterParamName(it)
                builder.addStatement("arguments.putSerializable($binderTypeName.${RouterUtils.getArgumentKeyName(name)}, $name)")
            }
            builder.addStatement("fragment.setArguments(arguments)")
            builder.addStatement("${PackageNames.supportFragmentTransaction} transaction = fm.beginTransaction()")
            builder.addStatement("transaction.replace(containerId, fragment)")
            builder.addStatement("if (fm.findFragmentById(containerId) != null) transaction.addToBackStack(null)")
            builder.addStatement("transaction.commit()")
            builder.addStatement("fm.executePendingTransactions()")
        }.build()

    }
}