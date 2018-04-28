package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.allArgumentElements
import com.github.chuross.morirouter.compiler.extension.argumentElements
import com.github.chuross.morirouter.compiler.extension.argumentKeyName
import com.github.chuross.morirouter.compiler.extension.isRequiredArgument
import com.github.chuross.morirouter.compiler.extension.manualSharedViewNames
import com.github.chuross.morirouter.compiler.extension.normalize
import com.github.chuross.morirouter.compiler.extension.overrideEnterTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.overrideExitTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.paramName
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.sharedEnterTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.sharedExitTransitionFactoryName
import com.github.chuross.morirouter.compiler.extension.uriArgumentElements
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

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element)).also { builder ->
            builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            builder.addJavadoc("This class is auto generated.")
            builder.addField(fragmentManagerField())
            builder.addField(optionsField())
            builder.addField(sharedElementsField())
            builder.addFields(paramFields(element))
            builder.addMethod(constructorMethod(element))
            builder.addMethods(optionalParameterMethods(element))
            if (element.manualSharedViewNames?.isNotEmpty() ?: false) {
                builder.addMethod(manualSharedMappingEnabledMethod(element))
            } else {
                builder.addMethod(sharedElementAddMethod(element))
            }
            builder.addMethod(launchMethod(element))
        }.build()

        JavaFile.builder(context.getPackageName(), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun validate(element: Element) {
        val argumentElements = element.argumentElements
        val uriArgumentElements = element.uriArgumentElements

        val hasRequiredElement = argumentElements.any { it.isRequiredArgument }
        val hasUriArgumentElement = uriArgumentElements.firstOrNull() != null
        if (hasRequiredElement && hasUriArgumentElement) {
            throw IllegalStateException("'required' Argument can use no UriArgument only: ${element.simpleName}")
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

    private fun sharedElementsField(): FieldSpec {
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

    private fun sharedElementAddMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("addSharedElement")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(PackageNames.VIEW), "view")
                .returns(ClassName.bestGuess(getGeneratedTypeName(element)))
                .addStatement("this.sharedElements.add(view)")
                .addStatement("return this")
                .build()
    }

    private fun manualSharedMappingEnabledMethod(element: Element): MethodSpec {
        return MethodSpec.methodBuilder("manualSharedMapping").also { builder ->
            builder.addModifiers(Modifier.PUBLIC)
            builder.addParameter(ClassName.bestGuess(PackageNames.CONTEXT), "context")
            builder.returns(ClassName.bestGuess(getGeneratedTypeName(element)))

            element.manualSharedViewNames?.forEach {
                builder.addStatement("${PackageNames.VIEW} view = new ${PackageNames.VIEW}(context)")
                builder.addStatement("view.setId(0)")
                builder.addStatement("${PackageNames.VIEW_COMPAT}.setTransitionName(view, \"$it\")")
                builder.addStatement("this.sharedElements.add(view)")
            }

            builder.addStatement("return this")
        }.build()
    }

    private fun launchMethod(element: Element): MethodSpec {
        val fragmentClassName = ClassName.get(element.asType())
        val routerParamElements = element.argumentElements
        val routerPathParamElements = element.uriArgumentElements
        val binderTypeName = BindingProcessor.getGeneratedTypeName(element)

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addModifiers(Modifier.PUBLIC)

            builder.addStatement("if (fm.isStateSaved()) return")
            builder.addStatement("$fragmentClassName fragment = new $fragmentClassName()")
            builder.addStatement("${PackageNames.BUNDLE} arguments = new ${PackageNames.BUNDLE}()")
            routerParamElements.plus(routerPathParamElements).forEach {
                val name = it.paramName.normalize()
                builder.addStatement("arguments.putSerializable($binderTypeName.${it.argumentKeyName}, $name)")
            }
            builder.addStatement("fragment.setArguments(arguments)")

            element.overrideEnterTransitionFactoryName?.also {
                builder.addStatement("Object overrideEnterTransitionSet = new $it().create()")
                builder.addStatement("Object enterTransitionSet = overrideEnterTransitionSet != null ? overrideEnterTransitionSet : options.getEnterTransition()")
                builder.addStatement("if (enterTransitionSet != null) fragment.setEnterTransition(enterTransitionSet)")
            }
                    ?: builder.addStatement("if (options.getEnterTransition() != null) fragment.setEnterTransition(options.getEnterTransition())")

            element.overrideExitTransitionFactoryName?.also {
                builder.addStatement("Object overrideExitTransitionSet = new $it().create()")
                builder.addStatement("Object exitTransitionSet = overrideExitTransitionSet != null ? overrideExitTransitionSet : options.getExitTransition()")
                builder.addStatement("if (exitTransitionSet != null) fragment.setExitTransition(exitTransitionSet)")
            }
                    ?: builder.addStatement("if (options.getExitTransition() != null) fragment.setExitTransition(options.getExitTransition())")

            element.sharedEnterTransitionFactoryName?.also {
                builder.addStatement("Object sharedEnterTransitionSet = new $it().create()")
                builder.addStatement("if (sharedEnterTransitionSet != null) fragment.setSharedElementEnterTransition(sharedEnterTransitionSet)")
            }

            element.sharedExitTransitionFactoryName?.also {
                builder.addStatement("Object sharedExitTransitionSet = new $it().create()")
                builder.addStatement("if (sharedExitTransitionSet != null) fragment.setSharedElementReturnTransition(sharedExitTransitionSet)")
            }

            builder.addStatement("${PackageNames.SUPPORT_FRAGMENT_TRANSACTION} transaction = fm.beginTransaction()")
            builder.beginControlFlow("for (View view : sharedElements)")
            builder.addStatement("if (view.getId() < 0) throw new ${PackageNames.ILLEGAL_STATE_EXCEPTION}(\"view must have id!\")")
            builder.addStatement("String sharedArgumentKey = String.format(\"${BindingProcessor.SHARED_ELEMENT_ARGUMENT_KEY_NAME_FORMAT}\", view.getId())")
            builder.addStatement("String transitionName = ${PackageNames.VIEW_COMPAT}.getTransitionName(view)")
            builder.addStatement("arguments.putString(sharedArgumentKey, transitionName)")
            builder.addStatement("transaction.addSharedElement(view, transitionName)")
            builder.endControlFlow()
            builder.addStatement("transaction.replace(options.getContainerId(), fragment)")
            builder.addStatement("if (fm.findFragmentById(options.getContainerId()) != null) transaction.addToBackStack(null)")
            builder.addStatement("transaction.commit()")
            builder.addStatement("fm.executePendingTransactions()")
        }.build()

    }
}