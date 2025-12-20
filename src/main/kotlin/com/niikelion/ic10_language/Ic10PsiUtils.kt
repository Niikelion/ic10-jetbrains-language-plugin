package com.niikelion.ic10_language

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.Ic10PsiUtils.getDeclaredName
import com.niikelion.ic10_language.logic.Constants
import com.niikelion.ic10_language.logic.Instruction
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.psi.*
import kotlinx.collections.immutable.toImmutableMap
import javax.swing.Icon

enum class Ic10SymbolType {
    Constant {
        override val isValue = true
    }, Variable {
        override val isValue = true
    }, Register {
        override val isValue = true
        override val isRegister = true
    }, Device {
        override val isDevice = true
    };

    open val isValue: Boolean = false
    open val isRegister: Boolean = false
    open val isDevice: Boolean = false
}

data class Ic10Symbol(
    val name: String,
    val definitionElement: Ic10NamedElement,
    val valueElement: PsiElement,
    val type: Ic10SymbolType,
    val unresolved: Boolean
) {
    companion object {
        fun isValidRegister(name: String) = registers.contains(name)
        fun isValidDevice(name: String) = devices.contains(name)

        fun from(definitionElement: Ic10NamedElement, valueElement: PsiElement, type: Ic10SymbolType, unresolved: Boolean) = Ic10Symbol(definitionElement.name!!, definitionElement, valueElement, type, unresolved)
        fun from(label: Ic10Label) = from(label, label, Ic10SymbolType.Constant, false)
        fun from(element: Ic10Operation): Ic10Symbol? {
            val opName = element.operationName.text

            if (element.valueList.size != 2) return null

            val definition = element.valueList[0] ?: return null
            val referenceName = definition.referenceName ?: return null

            val value = element.valueList[1] ?: return null

            return when (opName) {
                "define" -> {
                    val valueReferenceName = value.referenceName

                    if (valueReferenceName != null) return from(referenceName, value, Ic10SymbolType.Constant, true)

                    from(referenceName, value, Ic10SymbolType.Constant, false)
                }
                "alias" -> {
                    val valueReferenceName = value.referenceName ?: return null

                    val valueName = valueReferenceName.name!!

                    if (isValidRegister(valueName)) return from(referenceName, value, Ic10SymbolType.Register, false)
                    if (isValidDevice(valueName)) return from(referenceName, value, Ic10SymbolType.Device, false)

                    from(referenceName, value, Ic10SymbolType.Variable, true)
                }
                else -> null
            }
        }

        private val registers = (List(18) { "r$it" } + listOf("sp", "ra")).toSet()
        private val devices = (List(6) { "d$it" } + listOf("db")).toSet()
    }

    val isBuiltIn: Boolean get() = !unresolved && definitionElement.parent == valueElement
}

object Ic10PsiUtils {
    fun findLabelsInFile(file: PsiFile): Collection<Ic10Label> = PsiTreeUtil.findChildrenOfType(file, Ic10Label::class.java)
    fun findLabelsInFile(file: PsiFile, name: String) = findLabelsInFile(file).filter { it.name == name }

    fun isDeclaration(element: PsiElement): Boolean {
        return when (element) {
            is Ic10Label -> true
            is Ic10ReferenceName -> element.isDeclaration
            else -> false
        }
    }

    fun getDeclaredName(element: Ic10Operation): String? {
        val opName = element.operationName.text
        if (opName != "define" && opName != "alias") return null

        val value = element.valueList.firstOrNull() ?: return null

        val reference = value.referenceName ?: return null

        return reference.name
    }

    fun findDeclarations(file: PsiFile): Collection<Ic10NamedElement> {
        val labels = findLabelsInFile(file)
        val aliases = PsiTreeUtil
            .findChildrenOfType(file, Ic10Operation::class.java)
            .filter { getDeclaredName(it) != null }
            .map { it.valueList.first().referenceName!! }

        return labels + aliases
    }

    fun findDeclarations(file: PsiFile, name: String): Collection<Ic10NamedElement> {
        val labels = findLabelsInFile(file, name)
        val aliases =
            PsiTreeUtil.findChildrenOfType(file, Ic10Operation::class.java).filter { getDeclaredName(it) == name }
                .map { it.valueList.first().referenceName!! }

        return labels + aliases
    }

    fun getIcon(element: Ic10NamedElement): Icon? = when (element) {
        is Ic10Label -> Ic10Icons.Constant
        is Ic10ReferenceName -> Ic10Icons.Variable
        else -> null
    }

    fun getPresentation(element: Ic10NamedElement): ItemPresentation? {
        return when (element) {
            is Ic10Label -> getPresentation(element)
            is Ic10ReferenceName -> getPresentation(element)
            else -> null
        }
    }

    private fun getPresentation(element: Ic10Label): ItemPresentation {
        return object: ItemPresentation {
            override fun getPresentableText(): String? = element.name

            override fun getLocationString(): String? = element.containingFile?.name

            override fun getIcon(unused: Boolean): Icon = element.getIcon(0)
        }
    }

    private fun getPresentation(element: Ic10ReferenceName): ItemPresentation? {
        val operation = element.findParentOfType<Ic10Operation>() ?: return null

        if (getDeclaredName(operation) == null) return null

        if (operation.valueList[0].referenceName != element) return null

        return object: ItemPresentation {
            override fun getPresentableText(): String? = element.name

            override fun getLocationString(): String? = element.containingFile?.name

            override fun getIcon(unused: Boolean): Icon = element.getIcon(0)
        }
    }

    fun getProjectFiles(project: Project): List<Ic10File> {
        val virtualFiles = FileTypeIndex.getFiles(Ic10FileType.Instance, GlobalSearchScope.allScope(project))
        return virtualFiles.mapNotNull { file -> PsiManager.getInstance(project).findFile(file) as Ic10File? }
    }

    fun isValidRegister(element: Ic10NamedElement) = Ic10Symbol.isValidRegister(element.name!!)
    fun isValidDevice(element: Ic10NamedElement) = Ic10Symbol.isValidDevice(element.name!!)

    private fun findSymbols(file: PsiFile): Collection<Ic10Symbol> {
        val labels = findLabelsInFile(file).map { Ic10Symbol.from(it) }
        val aliases = PsiTreeUtil.findChildrenOfType(file, Ic10Operation::class.java).mapNotNull { Ic10Symbol.from(it) }

        return labels + aliases
    }
    private fun resolveSymbol(symbols: MutableMap<String, Ic10Symbol>, symbol: Ic10Symbol): Ic10Symbol {
        if (!symbol.unresolved) return symbol

        if (symbol.valueElement !is Ic10Value) return symbol

        val name = symbol.valueElement.referenceName?.name ?: symbol

        val resolvedTargetSymbol = symbols[name]?.let { resolveSymbol(symbols, it) } ?: return symbol

        return Ic10Symbol.from(symbol.definitionElement, resolvedTargetSymbol.valueElement, resolvedTargetSymbol.type, resolvedTargetSymbol.unresolved).also { symbols[symbol.name] = it }
    }
    private fun resolveSymbols(symbols: MutableMap<String, Ic10Symbol>) = symbols.values.forEach { resolveSymbol(symbols, it) }

    private fun findAndResolveSymbols(file: PsiFile): Map<String, Ic10Symbol> = findSymbols(file).associateBy { it.name }.toMutableMap().also { resolveSymbols(it) }.toImmutableMap()
    fun findAndResolveSymbol(element: Ic10ReferenceName): Ic10Symbol? {
        val symbol = findAndResolveSymbols(element.containingFile).let { it[element.name!!] }

        if (symbol != null) return symbol

        if (isValidDevice(element))
            return Ic10Symbol.from(element, element.parent, Ic10SymbolType.Device, false)

        if (isValidRegister(element))
            return Ic10Symbol.from(element, element.parent, Ic10SymbolType.Register, false)

        Constants.get(element.name!!) ?: return null

        // TODO: handle logic types and enums

        return Ic10Symbol.from(element, element.parent, Ic10SymbolType.Constant, false)
    }

    fun getLineNumber(element: PsiElement): Int = element.containingFile.viewProvider.document.getLineNumber(element.textOffset) + 1
}

val Ic10Operation.instruction get(): Instruction? = Instructions.get(operationName.text)
val Ic10NamedElement.isDeclaration: Boolean get() =
    findParentOfType<Ic10Operation>()?.let { getDeclaredName(it) } != null