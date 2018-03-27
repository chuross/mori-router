package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.enterTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.exitTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.isRequiredRouterParam
import com.github.chuross.morirouter.compiler.extension.paramElements
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.routerParamElements
import com.github.chuross.morirouter.compiler.extension.routerUriParamElements
import com.github.chuross.morirouter.compiler.extension.transitionNames
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
                .addFields(transitionNameStaticFields(element))
                .addField(fragmentManagerField())
                .addField(containerIdField())
                .addFields(paramFields(element))
                .addFields(transitionNameFields(element))
                .addMethod(constructorMethod(element))
                .addMethods(optionalParameterMethods(element))
                .addMethods(transitionNameParameterMethod(element))
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

    private fun transitionNameStaticFields(element: Element): Iterable<FieldSpec> {
        return element.transitionNames?.map {
            FieldSpec.builder(String::class.java, it.toUpperCase())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"$it\"")
                    .build()
        } ?: listOf()
    }

    private fun fragmentManagerField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT_MANAGER), "fm")
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

    private fun transitionNameFields(element: Element): Iterable<FieldSpec> {
        return element.transitionNames?.map {
            FieldSpec.builder(ClassName.bestGuess(PackageNames.VIEW), it.normalize())
                    .addModifiers(Modifier.PRIVATE)
                    .build()
        } ?: listOf()
    }

    private fun constructorMethod(element: Element): MethodSpec {
        val requiredRouterParamElements = element.routerParamElements.filter { it.isRequiredRouterParam }

        return MethodSpec.constructorBuilder().also { builder ->
            builder.addParameter(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT_MANAGER), "fm")
            builder.addParameter(TypeName.INT, "containerId")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.containerId = containerId")
            requiredRouterParamElements.forEach {
                val name = it.paramName.normalize()
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

    private fun transitionNameParameterMethod(element: Element): Iterable<MethodSpec> {
        return element.transitionNames?.map {
            val name = it.normalize()
            MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(PackageNames.VIEW), name)
                    .addStatement("this.$name = $name")
                    .addStatement("return this")
                    .returns(ClassName.bestGuess(getGeneratedTypeName(element)))
                    .build()
        } ?: listOf()
    }

    private fun launchMethod(element: Element): MethodSpec {
        val fragmentClassName = ClassName.get(element.asType())
        val routerParamElements = element.routerParamElements
        val routerPathParamElements = element.routerUriParamElements
        val binderTypeName = BindingProcessor.getGeneratedTypeName(element)

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.BUNDLE} arguments = new ${PackageNames.BUNDLE}()")
            routerParamElements.plus(routerPathParamElements).forEach {
                val name = it.paramName.normalize()
                builder.addStatement("arguments.putSerializable($binderTypeName.${it.argumentKeyName}, $name)")
            }
            builder.addStatement("fragment.setArguments(arguments)")
            builder.beginControlFlow("if (${PackageNames.BUILD}.VERSION.SDK_INT >= ${PackageNames.BUILD}.VERSION_CODES.LOLLIPOP)")
            builder.addComment("Optional TransitionSet, if use TransitionFactory.")
            element.enterTransitionFactoryName?.let {
                builder.addStatement("Object enterTransitionSet = new $it().create()")
                builder.addStatement("if (enterTransitionSet != null) fragment.setSharedElementEnterTransition(enterTransitionSet)")
            }
            element.exitTransitionFactoryName?.let {
                builder.addStatement("Object exitTransitionSet = new $it().create()")
                builder.addStatement("if (exitTransitionSet != null) fragment.setSharedElementReturnTransition(exitTransitionSet)")
            }
            builder.endControlFlow()
            builder.addStatement("${PackageNames.SUPPORT_FRAGMENT_TRANSACTION} transaction = fm.beginTransaction()")
            element.transitionNames?.map {
                val variableName = it.normalize()
                val staticVariableName = it.toUpperCase()
                builder.addStatement("${PackageNames.VIEW_COMPAT}.setTransitionName($variableName, $staticVariableName)")
                builder.addStatement("transaction.addSharedElement($variableName, $staticVariableName)")
            }
            builder.addStatement("transaction.replace(containerId, fragment)")
            builder.addStatement("if (fm.findFragmentById(containerId) != null) transaction.addToBackStack(null)")
            builder.addStatement("transaction.commit()")
            builder.addStatement("fm.executePendingTransactions()")
        }.build()

    }
}