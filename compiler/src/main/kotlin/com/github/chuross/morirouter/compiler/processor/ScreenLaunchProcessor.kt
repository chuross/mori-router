package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.enterTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.exitTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.isRequiredArgument
import com.github.chuross.morirouter.compiler.extension.allArgumentElements
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.argumentElements
import com.github.chuross.morirouter.compiler.extension.uriArgumentElements
import com.github.chuross.morirouter.compiler.extension.transitionNames
import com.github.chuross.morirouter.core.MoriRouterOptions
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
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
                .addField(optionsField())
                .addField(transitionNamesField())
                .addFields(paramFields(element))
                .addMethod(constructorMethod(element))
                .addMethods(optionalParameterMethods(element))
                .addMethod(transitionNameParameterMethod(element))
                .addMethod(launchMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun validate(element: Element) {
        val requiredParamElement = element.argumentElements.find { it.isRequiredArgument }
        val pathParamElement = element.uriArgumentElements.firstOrNull()

        if (requiredParamElement != null && pathParamElement != null) {
            throw IllegalStateException("Argument 'required' can use no UriArgument only")
        }
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

    private fun transitionNamesField(): FieldSpec {
        val listClassName = ClassName.get("java.util", "List")
        return FieldSpec.builder(ParameterizedTypeName.get(listClassName, ClassName.bestGuess(PackageNames.VIEW)), "sharedElements")
                .addModifiers(Modifier.PRIVATE)
                .initializer("new ${PackageNames.ARRAY_LIST}<>()")
                .build()
    }

    private fun paramFields(element: Element): Iterable<FieldSpec> {
        return element.allArgumentElements.map {
            FieldSpec.builder(TypeName.get(it.asType()), it.paramName.normalize())
                    .addModifiers(Modifier.PRIVATE)
                    .build()
        }
    }

    private fun constructorMethod(element: Element): MethodSpec {
        val requiredRouterParamElements = element.argumentElements.filter { it.isRequiredArgument }

        return MethodSpec.constructorBuilder().also { builder ->
            builder.addParameter(ClassName.bestGuess(PackageNames.SUPPORT_FRAGMENT_MANAGER), "fm")
            builder.addParameter(MoriRouterOptions::class.java, "options")
            builder.addStatement("this.fm = fm")
            builder.addStatement("this.options = options")
            requiredRouterParamElements.forEach {
                val name = it.paramName.normalize()
                builder.addParameter(TypeName.get(it.asType()), name)
                builder.addStatement("this.$name = $name")
            }
        }.build()
    }

    private fun optionalParameterMethods(element: Element): Iterable<MethodSpec> {
        return element.allArgumentElements
                .filter { !it.isRequiredArgument }
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

    private fun transitionNameParameterMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("addSharedElement")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(PackageNames.VIEW), "view")
                .addStatement("this.sharedElements.add(view)")
                .addStatement("return this")
                .returns(ClassName.bestGuess(getGeneratedTypeName(element)))
                .build()
    }

    private fun launchMethod(element: Element): MethodSpec {
        val fragmentClassName = ClassName.get(element.asType())
        val routerParamElements = element.argumentElements
        val routerPathParamElements = element.uriArgumentElements
        val binderTypeName = BindingProcessor.getGeneratedTypeName(element)

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addModifiers(Modifier.PUBLIC)
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.BUNDLE} arguments = new ${PackageNames.BUNDLE}()")
            routerParamElements.plus(routerPathParamElements).forEach {
                val name = it.paramName.normalize()
                builder.addStatement("arguments.putSerializable($binderTypeName.${it.argumentKeyName}, $name)")
            }
            builder.addStatement("fragment.setArguments(arguments)")
            builder.addStatement("if (options.getEnterTransition() != null) fragment.setEnterTransition(options.getEnterTransition())")
            builder.addStatement("if (options.getExitTransition() != null) fragment.setExitTransition(options.getExitTransition())")
            builder.addComment("Optional TransitionSet, if use TransitionFactory.")
            element.enterTransitionFactoryName?.also {
                builder.addStatement("Object enterTransitionSet = new $it().create()")
                builder.addStatement("if (enterTransitionSet != null) fragment.setSharedElementEnterTransition(enterTransitionSet)")
            }
            element.exitTransitionFactoryName?.also {
                builder.addStatement("Object exitTransitionSet = new $it().create()")
                builder.addStatement("if (exitTransitionSet != null) fragment.setSharedElementReturnTransition(exitTransitionSet)")
            }
            builder.addStatement("${PackageNames.SUPPORT_FRAGMENT_TRANSACTION} transaction = fm.beginTransaction()")
            builder.beginControlFlow("for (View view : sharedElements)")
            builder.addStatement("transaction.addSharedElement(view, ${PackageNames.VIEW_COMPAT}.getTransitionName(view))")
            builder.endControlFlow()
            builder.addStatement("transaction.replace(options.getContainerId(), fragment)")
            builder.addStatement("if (fm.findFragmentById(options.getContainerId()) != null) transaction.addToBackStack(null)")
            builder.addStatement("transaction.commit()")
            builder.addStatement("fm.executePendingTransactions()")
        }.build()

    }
}