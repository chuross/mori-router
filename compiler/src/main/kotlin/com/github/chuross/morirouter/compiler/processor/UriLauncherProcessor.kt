package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.util.regex.Pattern
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object UriLauncherProcessor {

    private const val URI_REGEX_FIELD_NAME = "URI_REGEX"

    fun getGeneratedTypeName(element: Element): String {
        val routerPathAnnotation = element.getAnnotation(RouterPath::class.java)
        if (routerPathAnnotation.name.isBlank()) {
            throw IllegalStateException("RouterPath name must be not empty")
        }
        return "${routerPathAnnotation.name.capitalize()}UriLauncher"
    }

    fun process(context: ProcessorContext, element: Element) {
        if (element.getAnnotation(RouterPath::class.java).uri.isBlank()) return

        val typeSpec = TypeSpec.classBuilder(getGeneratedTypeName(element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(uriRegexStaticField(element))
                .addField(routerField())
                .build()

        JavaFile.builder(context.getPackageName(element), typeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun uriRegexStaticField(element: Element): FieldSpec {
        val format = element.getAnnotation(RouterPath::class.java)?.uri!!
        val patternStr = format
                .replace("/", """\\/""")
                .replace("""\{[a-zA-Z0-9_\-]+\}""".toRegex(), """([^\\\\/]+)""")
                .plus("""\\/?$$""")

        return FieldSpec.builder(Pattern::class.java, URI_REGEX_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("${PackageNames.pattern}.compile(\"$patternStr\")")
                .build()
    }

    private fun routerField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess("MoriRouter"), "router")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }
}