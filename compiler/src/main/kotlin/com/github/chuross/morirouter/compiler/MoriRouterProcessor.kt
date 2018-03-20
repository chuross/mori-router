package com.github.chuross.morirouter.compiler

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterPathParam
import com.github.chuross.morirouter.compiler.processor.RouterProcessor
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class MoriRouterProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elements: Elements

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
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
            val context = ProcessorContext(filer, elements)

            roundEnv.getElementsAnnotatedWith(RouterPath::class.java)
                    .forEach {
                        if (it.kind != ElementKind.CLASS) throw IllegalArgumentException("RouterPath only support ClassType")

                        RouterProcessor.process(context, it)
                    }
            true
        } catch (e: Throwable) {
            messager.printMessage(Diagnostic.Kind.ERROR, "MoriRouter:generate:failed:$e")
            false
        }
    }
}