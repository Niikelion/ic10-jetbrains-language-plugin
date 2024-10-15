package com.niikelion.ic10_language

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.psi.Ic10JumpTarget
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10NameSourceLineMarkerProvider: RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element.elementType == Ic10Types.NAME && element.findParentOfType<Ic10JumpTarget>() != null) {
            val targets = Ic10PsiUtils.resolveLabels(element)

            if (targets.isEmpty()) return

            val builder = NavigationGutterIconBuilder.create(Ic10Icons.Lookup)
                .setTargets(targets)
                .setTooltipText("Navigate to ${element.text}")

            result.add(builder.createLineMarkerInfo(element))
            return
        }
    }
}