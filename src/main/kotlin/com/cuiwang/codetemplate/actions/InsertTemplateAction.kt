package com.cuiwang.codetemplate.actions

import com.cuiwang.codetemplate.core.TemplateInsertUtil
import com.cuiwang.codetemplate.core.TemplatePreviewUtil
import com.cuiwang.codetemplate.model.CodeTemplateEntry
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class InsertTemplateAction(
    private val order: Int,
    private val template: CodeTemplateEntry,
) : DumbAwareAction(buildText(order, template)) {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        TemplateInsertUtil.insertAtCaret(project, editor, template.content)
    }

    companion object {
        private fun buildText(order: Int, template: CodeTemplateEntry): String {
            val preview = TemplatePreviewUtil.toInlinePreview(template.content)
            return "$order. ${template.name}  $preview"
        }
    }
}
