package icons

import com.intellij.openapi.util.IconLoader

object MyIcons {
    @JvmField
    val logo = IconLoader.getIcon("/META-INF/pluginIcon.svg", javaClass)
}