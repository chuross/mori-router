package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterUriParam
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.github.chuross.morirouter.compiler.extension.pathName
import com.github.chuross.morirouter.compiler.extension.normalize
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.util.regex.Pattern
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object UriLauncherProcessor {

    const val INTERFACE_CLASS_NAME = "UriLauncher"
    private const val URI_REGEX_FIELD_NAME = "URI_REGEX"
    private val PATH_PARAMETER_REGEX = """\{([a-zA-Z0-9_\-]+)\}""".toRegex()

    fun getGeneratedTypeName(element: Element): String {
        if (element.pathName.isNullOrBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${element.pathName?.normalize()?.capitalize()}UriLauncher"
    }

    fun processInterface(context: ProcessorContext, elements: Set<Element>) {
        if (elements.isEmpty()) return

        val typeSpec = TypeSpec.interfaceBuilder(INTERFACE_CLASS_NAME)
                .addJavadoc("This class is auto generated.")
                .addMethod(isAvailableInterfaceMethod())
                .addMethod(launchInterfaceMethod())
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun isAvailableInterfaceMethod(): MethodSpec {
        return MethodSpec.methodBuilder("isAvailable")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
                .returns(TypeName.BOOLEAN)
                .build()
    }

    private fun launchInterfaceMethod(): MethodSpec {
        return MethodSpec.methodBuilder("launch")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
                .build()
    }


    fun process(context: ProcessorContext, element: Element) {
        if (element.getAnnotation(RouterPath::class.java).uri.isBlank()) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addSuperinterface(ClassName.bestGuess(INTERFACE_CLASS_NAME))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(uriRegexStaticField(element))
                .addField(routerField())
                .addMethod(constructorMethod())
                .addMethod(isAvailableMethod())
                .addMethod(launchMethod(element))
                .build()

        JavaFile.builder(context.getPackageName(element), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun uriRegexStaticField(element: Element): FieldSpec {
        val format = element.getAnnotation(RouterPath::class.java)?.uri!!
        val patternStr = "^" + format
                .replace("/", """\\/""")
                .replace(PATH_PARAMETER_REGEX, """([^\\\\/]+)""")
                .let {
                    val suffix = if (it.endsWith("/")) "?$$" else """\\/?$$"""
                    it.plus(suffix)
                }

        return FieldSpec.builder(Pattern::class.java, URI_REGEX_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("${PackageNames.pattern}.compile(\"$patternStr\")")
                .build()
    }

    private fun routerField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(RouterProcessor.TYPE_NAME), "router")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }

    private fun constructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addParameter(ClassName.bestGuess(RouterProcessor.TYPE_NAME), "router")
                .addStatement("this.router = router")
                .build()
    }

    private fun isAvailableMethod(): MethodSpec {
        return MethodSpec.methodBuilder("isAvailable")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
                .addStatement("return $URI_REGEX_FIELD_NAME.matcher(uri.toString()).matches()")
                .returns(TypeName.BOOLEAN)
                .build()
    }

    private fun launchMethod(element: Element): MethodSpec {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)!!
        val routerPathName = routerPathAnnotation.name
        val format = routerPathAnnotation.uri

        val pathParameterNames = PATH_PARAMETER_REGEX
                .findAll(format)
                .map { it.groupValues }
                .filter { it.size > 1 }
                .map { it.subList(1, it.size) }
                .flatten()

        return MethodSpec.methodBuilder("launch").also { builder ->
            builder.addModifiers(Modifier.PUBLIC)
            builder.addAnnotation(Override::class.java)
            builder.addParameter(ClassName.bestGuess(PackageNames.uri), "uri")
            builder.addStatement("${PackageNames.matcher} matcher = $URI_REGEX_FIELD_NAME.matcher(uri.toString())")
            builder.addStatement("if (!matcher.matches()) throw new ${PackageNames.illegalState}(\"invalid uri format\")")
            builder.addStatement("${ScreenLaunchProcessor.getGeneratedTypeName(element)} launcher = router.$routerPathName()")
            pathParameterNames.forEachIndexed { index, name ->
                element.enclosedElements
                        .find {
                            val annotation = it.getAnnotation(RouterUriParam::class.java)
                            annotation?.name == name || it.simpleName.toString() == name
                        }
                        ?: throw IllegalStateException("Target RouterUriParam element not found: ${element.simpleName}#$name")

                builder.addStatement("launcher.${name.normalize()}(matcher.group(${index.inc()}))")
            }
            builder.addStatement("launcher.launch()")
        }.build()
    }
}