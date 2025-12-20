package com.niikelion.ic10_language.documentation.ui

import com.intellij.lang.documentation.DocumentationMarkup
import com.niikelion.ic10_language.ui.html.HtmlBuilder
import com.niikelion.ic10_language.ui.html.HtmlBuilder.Element

val HtmlBuilder.sectionContent get() = Element(this, DocumentationMarkup.SECTION_CONTENT_CELL)
val HtmlBuilder.sectionHeader get() = Element(this, DocumentationMarkup.SECTION_HEADER_CELL)
val HtmlBuilder.sectionsTable get() = Element(this, DocumentationMarkup.SECTIONS_TABLE)
val HtmlBuilder.content get() = Element(this, DocumentationMarkup.CONTENT_ELEMENT)
val HtmlBuilder.definition get() = Element(this, DocumentationMarkup.DEFINITION_ELEMENT)
val HtmlBuilder.top get() = Element(this, DocumentationMarkup.TOP_ELEMENT)
val HtmlBuilder.bottom get() = Element(this, DocumentationMarkup.BOTTOM_ELEMENT)
val HtmlBuilder.pre get() = Element(this, DocumentationMarkup.PRE_ELEMENT)
val HtmlBuilder.grayed get() = Element(this, DocumentationMarkup.GRAYED_ELEMENT)
val HtmlBuilder.centered get() = Element(this, DocumentationMarkup.CENTERED_ELEMENT)