package com.niikelion.ic10_language.utils

private val tagRegex = Regex("<[^>]+>")

fun String.stripHtml() = replace(tagRegex, "")