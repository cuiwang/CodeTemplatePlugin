package com.cuiwang.codetemplate.actions

import com.cuiwang.codetemplate.state.CodeTemplateSettingsService
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAware

class InsertTemplatesActionGroup : ActionGroup(), DumbAware {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val hasEditor = e.getData(CommonDataKeys.EDITOR) != null
        e.presentation.isEnabledAndVisible = hasEditor
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val templates = CodeTemplateSettingsService.getInstance().getEnabledTemplates()
        if (templates.isEmpty()) {
            return arrayOf(NoTemplateAction())
        }
        return templates.mapIndexed { index, template ->
            InsertTemplateAction(index + 1, template)
        }.toTypedArray()
    }

    private class NoTemplateAction : AnAction("(暂无启用模板)") {
        override fun actionPerformed(e: AnActionEvent) {
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = false
        }
    }
}
