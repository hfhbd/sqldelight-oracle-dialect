configurations.configureEach {
    exclude(group = "com.jetbrains.rd")
    exclude(group = "com.github.jetbrains", module = "jetCheck")
    exclude(group = "com.jetbrains.intellij.platform", module = "statistics-devkit")
    exclude(group = "com.jetbrains.intellij.platform", module = "credential-store-ui")
    exclude(group = "com.jetbrains.intellij.platform", module = "elevation")
    exclude(group = "com.jetbrains.intellij.platform", module = "util-ui")
    exclude(group = "com.jetbrains.intellij.platform", module = "diagnostic")
    exclude(group = "com.jetbrains.intellij.platform", module = "execution-impl")
    exclude(group = "com.jetbrains.intellij.platform", module = "external-system-impl")
    exclude(group = "com.jetbrains.intellij.remoteDev", module = "remote-dev-util")
    exclude(group = "com.jetbrains.infra")

    exclude(group = "org.roaringbitmap")

    exclude(group = "ai.grazie.spell")
    exclude(group = "ai.grazie.model")
    exclude(group = "ai.grazie.utils")
    exclude(group = "ai.grazie.nlp")
}
