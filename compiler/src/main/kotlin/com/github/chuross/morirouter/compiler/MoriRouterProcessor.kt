package com.github.chuross.morirouter.compiler

import com.github.chuross.morirouter.annotation.Argument
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.UriArgument
import com.github.chuross.morirouter.annotation.WithArguments
import com.github.chuross.morirouter.compiler.processor.BindingProcessor
import com.github.chuross.morirouter.compiler.processor.FragmentBuilderProcessor
import com.github.chuross.morirouter.compiler.processor.RouterProcessor
import com.github.chuross.morirouter.compiler.processor.UriLauncherProcessor
import com.google.auto.service.AutoService
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
class MoriRouterProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elementUtils: Elements
    lateinit var typeUtils: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return hashSetOf(
                RouterPath::class.java.canonicalName,
                UriArgument::class.java.canonicalName,
                Argument::class.java.canonicalName,
                WithArguments::class.java.canonicalName
        )
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        return try {
            ProcessorContext.setup(filer, messager, elementUtils, typeUtils)

            val routerPaths = roundEnv.getElementsAnnotatedWith(RouterPath::class.java)
            val fragmentBuilders = roundEnv.getElementsAnnotatedWith(WithArguments::class.java)

            routerPaths.also {
                UriLauncherProcessor.processInterface(it)
                RouterProcessor.process(it)
            }

            fragmentBuilders.forEach {
                FragmentBuilderProcessor.process(it)
            }

            BindingProcessor.processAutoBinder(routerPaths.plus(fragmentBuilders))

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