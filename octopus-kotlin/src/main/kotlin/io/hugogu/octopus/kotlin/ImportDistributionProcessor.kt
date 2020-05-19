package io.hugogu.octopus.kotlin

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList

class ImportDistributionProcessor : FileProcessListener {
    lateinit var files: List<KtFile>

    override fun onStart(files: List<KtFile>) {
        this.files = files
    }

    override fun onProcess(file: KtFile) {
        val visitor = ImportsVisitor()
        file.accept(visitor)
        file.putUserData(numberOfSystemImportKey, visitor.numberOfSystemImports)
        file.putUserData(numberOfDomainImportKey, visitor.numberOfDomainImports)
    }

    companion object {
        val numberOfSystemImportKey = Key<Int>("number of system imports")
        val numberOfDomainImportKey = Key<Int>("number of domain imports")
    }

    class ImportsVisitor : DetektVisitor() {
        internal var numberOfSystemImports = 0
        internal var numberOfDomainImports = 0

        override fun visitImportList(importList: KtImportList) {
            super.visitImportList(importList)
            numberOfSystemImports += importList.imports.count { it.importPath?.pathStr?.startsWith("java") == true }
            numberOfDomainImports += importList.imports.count { it.importPath?.pathStr?.startsWith("java") == false }
        }
    }
}
