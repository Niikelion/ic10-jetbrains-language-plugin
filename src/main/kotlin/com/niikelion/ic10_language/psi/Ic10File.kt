package com.niikelion.ic10_language.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10Language
import com.niikelion.ic10_language.cache
import com.niikelion.ic10_language.dependentOnThis
import com.niikelion.ic10_language.logic.DeviceReferenceValue
import com.niikelion.ic10_language.logic.Entity
import com.niikelion.ic10_language.logic.RegisterReferenceValue
import com.niikelion.ic10_language.psi.elements.Ic10EntitySourceElement
import kotlinx.collections.immutable.toImmutableMap

class Ic10File(viewProvider: FileViewProvider): PsiFileBase(viewProvider, Ic10Language.Instance) {
    override fun getFileType(): FileType = Ic10FileType.Instance
    override fun toString(): String = "Ic10 File"

    val linesCount: Int get() = linesCountCache.value
    val bytesCount: Int get() = bytesCountCache.value

    fun findEntity(name: String) = symbolsCache.value[name]

    private val linesCountCache = cache {
        dependentOnThis(text.count { it == '\n' } + 1)
    }
    private val bytesCountCache = cache {
        dependentOnThis(text.length)
    }
    private val symbolsCache = cache {
        val sourceEntities = PsiTreeUtil.findChildrenOfType(this, Ic10EntitySourceElement::class.java)
            .mapNotNull { it.entity }

        val entityLookup = listOf(
            Entity.builtinEntities,
            sourceEntities
        ).flatten().associateBy { it.name }.toMutableMap()

        fun resolve(entity: Entity, stack: Set<Entity>): Entity? {
            if (entity.resolved) return entity
            if (stack.contains(entity)) return null

            val target = entity.references?.referenceName?.name ?: return null
            val targetEntity = entityLookup[target]
                ?: RegisterReferenceValue.fromString(target)?.let { entity.resolve(it) }
                ?: DeviceReferenceValue.fromString(target)?.let { entity.resolve(it) }
                ?: return null
            val resolvedTarget = resolve(targetEntity, stack + entity) ?: return null
            return entity.resolve(resolvedTarget.getValue()).also { entityLookup[entity.name] = it }
        }

        sourceEntities.forEach { resolve(it, emptySet()) }

        dependentOnThis(entityLookup.toImmutableMap())
    }
}