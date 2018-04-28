package com.github.chuross.morirouter.compiler

import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ProcessorContext(
        val filer: Filer,
        val elementUtils: Elements,
        val typeUtils: Types
) {

    companion object {
        private var INSTANCE: ProcessorContext? = null

        @Synchronized
        fun setup(filer: Filer, elementUtils: Elements, typeUtils: Types) {
            INSTANCE = ProcessorContext(filer, elementUtils, typeUtils)
        }

        @Synchronized
        fun getInstance(): ProcessorContext = INSTANCE ?: throw IllegalStateException("nothing setup")
    }

    fun getPackageName(): String {
        return PackageNames.MORI_ROUTER
    }
}
