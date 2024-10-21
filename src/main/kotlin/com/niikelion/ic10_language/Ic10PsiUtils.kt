package com.niikelion.ic10_language

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.psi.*
import javax.swing.Icon

enum class Ic10SymbolType {
    Constant {
        override val isValue = true
    }, Variable {
        override val isValue = true
    }, Device {
        override val isDevice = true
    }, Unknown;

    open val isValue: Boolean = false
    open val isDevice: Boolean = false
}

data class Ic10Symbol(val name: String, val element: Ic10NamedElement?, val type: Ic10SymbolType) {
    companion object {
        fun isValidRegister(name: String) = registers.contains(name)
        fun isValidDevice(name: String) = devices.contains(name)

        fun from(label: Ic10Label) = Ic10Symbol(label.name!!, label, Ic10SymbolType.Constant)
        fun from(referenceName: Ic10ReferenceName, type: Ic10SymbolType) = Ic10Symbol(referenceName.name!!, referenceName, type)
        fun from(referenceName: Ic10ReferenceName): Ic10Symbol {
            val name = referenceName.name!!

            val constant = Constants.get(name)
            if (constant != null) return Ic10Symbol(name, referenceName, Ic10SymbolType.Constant)

            if (isValidRegister(name)) return Ic10Symbol(name, referenceName, Ic10SymbolType.Variable)

            if (isValidDevice(name)) return Ic10Symbol(name, referenceName, Ic10SymbolType.Device)

            return Ic10Symbol(name, referenceName, Ic10SymbolType.Unknown)
        }
        fun from(element: Ic10Operation): Ic10Symbol? {
            val opName = element.operationName.text

            if (element.valueList.size != 2) return null

            val definition = element.valueList[0] ?: return null
            val referenceName = definition.referenceName ?: return null

            val value = element.valueList[1] ?: return null

            return when (opName) {
                "define" -> Ic10Symbol(referenceName.name!!, referenceName, Ic10SymbolType.Constant)
                "alias" -> {
                    if (value.referenceName == null) return Ic10Symbol(referenceName.name!!, referenceName, Ic10SymbolType.Variable)

                    Ic10Symbol(referenceName.name!!, referenceName, Ic10SymbolType.Variable)
                }
                else -> null
            }
        }

        private val registers = (List(18) { "r$it" } + listOf("sp", "ra")).toSet()
        private val devices = (List(6) { "d$it" } + listOf("db")).toSet()
    }
}

object Ic10PsiUtils {
    fun findLabelsInFile(file: PsiFile): Collection<Ic10Label> = PsiTreeUtil.findChildrenOfType(file, Ic10Label::class.java)
    fun findLabelsInFile(file: PsiFile, name: String) = findLabelsInFile(file).filter { it.name == name }

    private fun findReferencesInFile(file: PsiFile) = PsiTreeUtil.findChildrenOfType(file, Ic10ReferenceName::class.java)
    fun findReferencesInFile(file: PsiFile, name: String) = findReferencesInFile(file).filter { it.name == name }

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

    fun isValidRegister(element: Ic10ReferenceName) = Ic10Symbol.isValidRegister(element.name!!)
    fun isValidDevice(element: Ic10ReferenceName) = Ic10Symbol.isValidDevice(element.name!!)

    fun findSymbols(file: PsiFile, name: String): Collection<Ic10Symbol> {
        val labels = findLabelsInFile(file, name).map { Ic10Symbol(name, it, Ic10SymbolType.Constant) }
        val aliases =
            PsiTreeUtil.findChildrenOfType(file, Ic10Operation::class.java).filter { getDeclaredName(it) == name }
                .map { it.valueList.first().referenceName!! }.map { Ic10Symbol(it) }

        return labels + aliases
    }
    fun findAndResolveSymbol(file: PsiFile, name: String) {

    }
}