package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.isRequiredRouterParam
import com.github.chuross.morirouter.compiler.extension.paramElements
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.routerParamElements
import com.github.chuross.morirouter.compiler.extension.routerUriParamElements
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
        if (element.pathName.isNullOrBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${element.pathName?.normalize()?.capitalize()}ScreenLauncher"
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
        val requiredParamElement = element.routerParamElements.find { it.isRequiredRouterParam }
        val pathParamElement = element.routerUriParamElements.firstOrNull()

        if (requiredParamElement != null && pathParamElement != null) {
            throw IllegalStateException("RouterParam 'required' can use no RouterUriParam only")
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
        return element.paramElements.map {
            FieldSpec.builder(TypeName.get(it.asType()), it.paramName.normalize())
                    .addModifiers(Modifier.PRIVATE)
                    .build()
        }
    }

    private fun constructorMethod(element: Element): MethodSpec {
        val requiredRouterParamElements = element.routerParamElements.filter { it.isRequiredRouterParam }

        return MethodSpec.constructorBuilder().also { builder ->
            builder.addParameter(ClassName.bestGuess(PackageNames.supportFragmentManager), "fm")
            builder.addParameter(TypeName.INT, "containerId")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.containerId = containerId")
            requiredRouterParamElements.forEach {
                val name = it.paramName
                builder.addParameter(TypeName.get(it.asType()), name)
                builder.addStatement("this.$name = $name")
            }
        }.build()
    }

    private fun optionalParameterMethods(element: Element): Iterable<MethodSpec> {
        return element.paramElements
                .filter { !it.isRequiredRouterParam }
                .map {
                    val name = it.paramName.normalize()
                    MethodSpec.methodBuilder(name)
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
        val routerParamElements = element.routerParamElements
        val routerPathParamElements = element.routerUriParamElements
        val binderTypeName = BindingProcessor.getGeneratedTypeName(element)

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.bundle} arguments = new ${PackageNames.bundle}()")
            routerParamElements.plus(routerPathParamElements).forEach {
                val name = it.paramName
                builder.addStatement("arguments.putSerializable($binderTypeName.${it.argumentKeyName}, ${name.normalize()})")
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