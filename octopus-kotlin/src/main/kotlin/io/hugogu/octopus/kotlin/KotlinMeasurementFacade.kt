package io.hugogu.octopus.kotlin

import io.gitlab.arturbosch.detekt.api.internal.relativePath
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.hugogu.octopus.model.AllFilesAtCommit
import io.hugogu.octopus.model.CodeFileMeasurement
import java.nio.file.Paths

object KotlinMeasurementFacade {
    fun extractImportUsage(commitFiles: AllFilesAtCommit): Iterable<CodeFileMeasurement> {
        val processor = ImportDistributionProcessor()
        val ktFiles = commitFiles.files.filter { it.path.endsWith("kt") }.map {
            KtTestCompiler.compileFromContent(content = it.content)
        }
        processor.onStart(ktFiles)
        ktFiles.forEach(processor::onProcess)

        return processor.files.map {
            CodeFileMeasurement(it.virtualFilePath,
                it.getUserData(ImportDistributionProcessor.numberOfSystemImportKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfDomainImportKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfIfKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfLinesKey)!!
            )
        }
    }

    fun extractImportUsage(dir: String): Iterable<CodeFileMeasurement> {
        val settings = ProcessingSettings(
            inputPaths = listOf(Paths.get(dir)),
            outPrinter = System.out,
            errPrinter = System.err
        )
        val processor = ImportDistributionProcessor()
        DetektFacade.create(settings, processor).run()

        return processor.files.map {
            CodeFileMeasurement(it.relativePath(),
                it.getUserData(ImportDistributionProcessor.numberOfSystemImportKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfDomainImportKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfIfKey)!!,
                it.getUserData(ImportDistributionProcessor.numberOfLinesKey)!!
            )
        }
    }
}