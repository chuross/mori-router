package com.github.chuross.morirouter.compiler.processor

import com.github.chuross.morirouter.compiler.ProcessorContext
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

object RouterProcessor {

    fun process(context: ProcessorContext, element: Element) {
        val routerTypeSpec = TypeSpec.classBuilder("MoriRouter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build()

        JavaFile.builder(context.getPackageName(element), routerTypeSpec)
                .build()
                .writeTo(context.filer)
    }
}