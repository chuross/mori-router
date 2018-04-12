package com.github.chuross.morirouter.compiler

import javax.annotation.processing.Filer
import javax.lang.model.util.Elements

class ProcessorContext(
        val filer: Filer,
        val elementUtils: Elements
) {

    fun getPackageName(): String {
        return PackageNames.MORI_ROUTER
    }
}
