package com.github.chuross.morirouter.compiler

import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

class ProcessorContext(
        val filer: Filer,
        val elements: Elements
) {

    fun getPackageName(element: Element): String {
        return elements.getPackageOf(element).qualifiedName.toString()
    }
}
