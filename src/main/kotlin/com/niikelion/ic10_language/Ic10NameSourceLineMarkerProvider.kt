package com.niikelion.ic10_language

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.psi.Ic10ReferenceName
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10NameSourceLineMarkerProvider: RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element.elementType == Ic10Types.NAME) {
            val referenceParent = element.findParentOfType<Ic10ReferenceName>() ?: return

            val targets = Ic10PsiUtils.findLabelsInFile(element.containingFile, referenceParent.name!!)

            if (targets.isEmpty()) return

            val builder = NavigationGutterIconBuilder.create(Ic10Icons.Lookup)
                .setTargets(targets)
                .setTooltipText("Navigate to ${element.text}")

            result.add(builder.createLineMarkerInfo(element))
        }
    }
}