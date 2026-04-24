package com.cuiwang.codetemplate.core

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

object TemplateInsertUtil {
    fun insertAtCaret(project: Project, editor: Editor, content: String) {
        if (content.isEmpty()) {
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            val offset = editor.caretModel.offset
            editor.document.insertString(offset, content)
            editor.caretModel.moveToOffset(offset + content.length)
        }
    }
}
