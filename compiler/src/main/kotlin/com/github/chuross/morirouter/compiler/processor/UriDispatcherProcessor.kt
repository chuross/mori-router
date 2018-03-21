package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object UriDispatcherProcessor {

    fun process(context: ProcessorContext, elements: Set<Element>) {
        if (elements.isEmpty()) return

        val typeSpec = TypeSpec.classBuilder("UriDispatcher")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class is auto generated.")
                .build()

        JavaFile.builder(context.getPackageName(elements.first()), typeSpec)
                .build()
                .writeTo(context.filer)
    }
}