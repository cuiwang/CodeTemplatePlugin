package com.cuiwang.codetemplate.actions

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.util.Key
import javax.swing.Icon

object MenuIconCompatibilityUtil {
    private val hideIconKey: Key<Boolean>? = resolveMenuKey("HIDE_ICON")
    private val hideDropdownIconKey: Key<Boolean>? = resolveMenuKey("HIDE_DROPDOWN_ICON")

    fun applyMenuIconPresentation(presentation: Presentation, icon: Icon) {
        presentation.icon = icon
        presentation.selectedIcon = icon

        hideIconKey?.let { presentation.putClientProperty(it, false) }
        hideDropdownIconKey?.let { presentation.putClientProperty(it, false) }
    }

    fun ensureIdeMenuIconsEnabled() {
        runCatching {
            val uiSettingsClass = Class.forName("com.intellij.ide.ui.UISettings")
            val instance = uiSettingsClass.methods
                .firstOrNull { it.name == "getInstanceOrNull" && it.parameterCount == 0 }
                ?.invoke(null)
                ?: uiSettingsClass.methods
                    .firstOrNull { it.name == "getInstance" && it.parameterCount == 0 }
                    ?.invoke(null)
                ?: return

            val getter = findBooleanGetter(uiSettingsClass, instance) ?: return
            val setter = findBooleanSetter(uiSettingsClass) ?: return
            val changedNotifier = uiSettingsClass.methods.firstOrNull {
                it.name == "fireUISettingsChanged" && it.parameterCount == 0
            }

            val current = getter.invoke(instance) as? Boolean ?: return
            if (!current) {
                setter.invoke(instance, true)
                changedNotifier?.invoke(instance)
            }
        }
    }

    private fun findBooleanGetter(clazz: Class<*>, instance: Any): java.lang.reflect.Method? {
        val candidates = listOf(
            "getShowIconsInMenus",
            "getShowMainMenuIcons",
            "getShowIconsInMenu",
            "getShowMenuIcons",
        )

        return candidates.asSequence()
            .mapNotNull { methodName ->
                clazz.methods.firstOrNull {
                    it.name == methodName &&
                        it.parameterCount == 0 &&
                        (it.returnType == java.lang.Boolean.TYPE || it.returnType == java.lang.Boolean::class.java)
                }
            }
            .firstOrNull()
            ?: clazz.methods.firstOrNull {
                it.parameterCount == 0 &&
                    it.name.startsWith("getShow") &&
                    it.name.contains("Icon", ignoreCase = true) &&
                    (it.returnType == java.lang.Boolean.TYPE || it.returnType == java.lang.Boolean::class.java)
            }
    }

    private fun findBooleanSetter(clazz: Class<*>): java.lang.reflect.Method? {
        val candidates = listOf(
            "setShowIconsInMenus",
            "setShowMainMenuIcons",
            "setShowIconsInMenu",
            "setShowMenuIcons",
        )

        return candidates.asSequence()
            .mapNotNull { methodName ->
                clazz.methods.firstOrNull {
                    it.name == methodName &&
                        it.parameterCount == 1 &&
                        (it.parameterTypes.first() == java.lang.Boolean.TYPE ||
                            it.parameterTypes.first() == java.lang.Boolean::class.java)
                }
            }
            .firstOrNull()
            ?: clazz.methods.firstOrNull {
                it.name.startsWith("setShow") &&
                    it.name.contains("Icon", ignoreCase = true) &&
                    it.parameterCount == 1 &&
                    (it.parameterTypes.first() == java.lang.Boolean.TYPE ||
                        it.parameterTypes.first() == java.lang.Boolean::class.java)
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveMenuKey(fieldName: String): Key<Boolean>? {
        return runCatching {
            val clazz = Class.forName("com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory")
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(null) as? Key<Boolean>
        }.getOrNull()
    }
}
