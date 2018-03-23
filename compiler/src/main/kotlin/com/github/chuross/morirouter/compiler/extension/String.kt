package com.github.chuross.morirouter.compiler.extension

fun String.routerCapitalizedName(): String {
    return replace("-", "_")
            .split("_")
            .filter { it.isNotBlank() }
            .mapIndexed { index, s -> if (index == 0) s else s.capitalize() }
            .joinToString("")
}