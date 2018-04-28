package com.github.chuross.morirouter.compiler

import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

class ProcessorContext(
        val filer: Filer,
        val messager: Messager,
        val elementUtils: Elements,
        val typeUtils: Types
) {

    companion object {
        private var INSTANCE: ProcessorContext? = null

        @Synchronized
        fun setup(filer: Filer, messager: Messager, elementUtils: Elements, typeUtils: Types) {
            INSTANCE = ProcessorContext(filer, messager, elementUtils, typeUtils)
        }

        @Synchronized
        fun getInstance(): ProcessorContext = INSTANCE ?: throw IllegalStateException("nothing setup")

        fun log(message: Any?) = message?.let { getInstance().messager.printMessage(Diagnostic.Kind.NOTE, it.toString()) }
    }

    fun getPackageName(): String {
        return PackageNames.MORI_ROUTER
    }
}
