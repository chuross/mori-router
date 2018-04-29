package com.github.chuross.morirouter.compiler.extension

import com.github.chuross.morirouter.compiler.PackageNames
import com.github.chuross.morirouter.compiler.ProcessorContext
import javax.lang.model.type.TypeMirror

fun TypeMirror.isSerializable(): Boolean {
    val context = ProcessorContext.getInstance()
    val serializableType = context.elementUtils.getTypeElement(PackageNames.SERIALIZABLE).asType()
    return context.typeUtils.isSubtype(this, serializableType)
}

fun TypeMirror.isParcelableType(): Boolean {
    val context = ProcessorContext.getInstance()
    val parcelableType = context.elementUtils.getTypeElement(PackageNames.PARCELABLE).asType()
    return context.typeUtils.isSubtype(this, parcelableType)
}
