package com.github.chuross.morirouter.compiler

import com.github.chuross.morirouter.annotation.RouterParam
import com.github.chuross.morirouter.annotation.RouterPath
import com.github.chuross.morirouter.annotation.RouterPathParam
import com.google.auto.service.AutoService
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
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

            processRouter(context, elements)

            true
        } catch (e: Throwable) {
            messager.printMessage(Diagnostic.Kind.ERROR, "MoriRouter:generate:failed:$e")
            false
        }
    }

    private fun processRouter(context: ProcessorContext, elements: Set<Element>) {
        val routerTypeSpec = TypeSpec.classBuilder("MoriRouter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .addField(fragmentManagerField())
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }

    private fun fragmentManagerField(): FieldSpec {
        return FieldSpec.builder(Class.forName(PackageNames.supportFragmentManager), "fm")
                .addModifiers(Modifier.PRIVATE)
                .build()
    }
}