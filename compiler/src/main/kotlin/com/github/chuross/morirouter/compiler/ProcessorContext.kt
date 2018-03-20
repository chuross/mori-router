package com.github.chuross.morirouter.compiler

import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

class ProcessorContext(
        val filer: Filer,
        val elementUtils: Elements,
        val messager: Messager
) {

    fun getPackageName(element: Element): String {
        return elementUtils.getPackageOf(element).qualifiedName.toString()
    }
}
