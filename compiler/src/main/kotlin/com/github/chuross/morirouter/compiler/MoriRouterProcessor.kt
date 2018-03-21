package com.github.chuross.morirouter.compiler

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterPathParam
import com.github.chuross.morirouter.compiler.processor.RouterProcessor
import com.google.auto.service.AutoService
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class MoriRouterProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elementUtils: Elements

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return hashSetOf(
                RouterPath::class.java.canonicalName,
                RouterPathParam::class.java.canonicalName,
                RouterParam::class.java.canonicalName
        )
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        return try {
            val context = ProcessorContext(filer, elementUtils)

            val elements = roundEnv.getElementsAnnotatedWith(RouterPath::class.java)
            if (elements.isEmpty()) return true

            RouterProcessor.process(context, elements)

            true
        } catch (e: Throwable) {
            val stacktrace = StringWriter().also {
                PrintWriter(it).also { e.printStackTrace(it) }.flush()
            }.toString()
            messager.printMessage(Diagnostic.Kind.ERROR, "MoriRouter:generate:failed:$stacktrace")
            false
        }
    }

}