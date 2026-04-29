package com.cuiwang.codetemplate.startup

import com.cuiwang.codetemplate.actions.MenuIconCompatibilityUtil
import com.cuiwang.codetemplate.settings.ShortcutManager
import com.cuiwang.codetemplate.state.CodeTemplateSettingsService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class CodeTemplateStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        val shortcut = CodeTemplateSettingsService.getInstance().getQuickInsertShortcut()
        ShortcutManager.applyShortcut(shortcut)

        ApplicationManager.getApplication().invokeLater {
            MenuIconCompatibilityUtil.ensureIdeMenuIconsEnabled()
        }
    }
}
