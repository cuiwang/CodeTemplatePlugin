package com.cuiwang.codetemplate.actions

import com.cuiwang.codetemplate.model.CodeTemplateEntry
import com.cuiwang.codetemplate.state.CodeTemplateSettingsService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.Messages

class CreateTemplateFromSelectionAction : AnAction(), DumbAware {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor?.selectionModel?.hasSelection() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showWarningDialog(project, "请先选中要保存的代码片段。", "Code Template")
            return
        }

        val templateName = Messages.showInputDialog(
            project,
            "请输入模板名称：",
            "Create Code Template",
            Messages.getQuestionIcon(),
        )?.trim() ?: return

        if (templateName.isBlank()) {
            Messages.showWarningDialog(project, "模板名称不能为空。", "Code Template")
            return
        }

        CodeTemplateSettingsService.getInstance().addTemplate(
            CodeTemplateEntry(
                name = templateName,
                content = selectedText,
                enabled = true,
            ),
        )
    }
}
