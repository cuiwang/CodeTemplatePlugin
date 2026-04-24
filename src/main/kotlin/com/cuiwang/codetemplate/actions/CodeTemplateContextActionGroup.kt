package com.cuiwang.codetemplate.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.IconLoader

class CodeTemplateContextActionGroup : DefaultActionGroup(), DumbAware {
    private val menuIcon = IconLoader.getIcon("/icons/codeTemplateMenu.svg", javaClass)

    init {
        templatePresentation.text = "Code Template"
        MenuIconCompatibilityUtil.applyMenuIconPresentation(templatePresentation, menuIcon)
        isPopup = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        super.update(e)
        MenuIconCompatibilityUtil.applyMenuIconPresentation(e.presentation, menuIcon)
    }
}
