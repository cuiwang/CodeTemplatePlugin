package com.cuiwang.codetemplate.actions

import com.cuiwang.codetemplate.ui.InsertTemplatePopup
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAware

class OpenInsertPopupAction : AnAction(), DumbAware {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val hasEditor = e.getData(CommonDataKeys.EDITOR) != null
        e.presentation.isEnabled = hasEditor
        e.presentation.isVisible = hasEditor || e.place == ActionPlaces.ACTION_SEARCH
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        InsertTemplatePopup.show(project, editor)
    }
}
