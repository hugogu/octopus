package io.hugogu.octopus.kotlin

import io.hugogu.octopus.git.GitFileActivityDetector
import io.hugogu.octopus.git.GitRepoContentUtils
import io.hugogu.octopus.model.getIfPercent
import io.hugogu.octopus.model.getSystemImportUsedRate
import io.hugogu.octopus.model.getTotalSystemImportPercent
import io.hugogu.octopus.visual.demo.BrowserDemoUtil
import org.junit.jupiter.api.Test
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.letsPlot.geom.geom_line
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.ggtitle
import jetbrains.letsPlot.intern.toSpec
import org.eclipse.jgit.api.Git
import java.io.File

class KotlinGraphTest {
    @Test
    fun gitChangesChartTest() {
        val detector = GitFileActivityDetector()
        val statistics = detector.extractCommitStatistics("..")
        val data = mapOf<String, Any>(
            "X" to statistics.map { it.commitTime },
            "Added" to statistics.map { it.getTotalAdded() },
            "Deleted" to statistics.map { -it.getTotalDeleted() }
        )
        val p = ggplot(data) +
                geom_line {
                    x = "X"
                    y = "Added"
                    color = "Added"
                } +
                geom_line {
                    x = "X"
                    y = "Deleted"
                    color = "Deleted"
                } +
                ggsize(1440, 900) +
                ggtitle("Git Commit Statistics")

        val spec = p.toSpec()

        // Export: use PlotSvgExport utility to generate SVG.
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(spec, null)
        BrowserDemoUtil.openInBrowser(svg)
    }

    @Test
    fun importTrendTest() {
        val git = Git.open(File(".."))
        val history = GitRepoContentUtils.readFileHistory(git) { it.endsWith("kt") }
        val historyStats = history.map {
            KotlinMeasurementFacade.extractImportUsage(it)
        }
        val data = mapOf<String, Any>(
            "X" to 0.until(historyStats.size).toList(),
            "SysImportPercent" to historyStats.map { it.getTotalSystemImportPercent() },
            "SysImportUseRate" to historyStats.map { it.getSystemImportUsedRate() },
            "IfPercent" to historyStats.map { it.getIfPercent() }
        )

        val p = ggplot(data) +
                geom_line {
                    x = "X"
                    y = "SysImportPercent"
                }  +
                geom_line {
                    x = "X"
                    y = "SysImportUseRate"
                } +
                geom_line {
                    x = "X"
                    y = "IfPercent"
                } +
                ggsize(1440, 900) +
                ggtitle("Import Usage Trend Statistics " + git.repository.directory.parentFile.name)

        val spec = p.toSpec()

        // Export: use PlotSvgExport utility to generate SVG.
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(spec, null)
        BrowserDemoUtil.openInBrowser(svg)
    }

    @Test
    fun importUsageTest() {
        val result = KotlinMeasurementFacade.extractImportUsage("..")
            .sortedBy { it.domainImport }
            .sortedBy { it.systemImport }

        val data = mapOf<String, Any>(
            "X" to 0.until(result.size).toList(),
            "SystemImports" to result.map { it.systemImport },
            "DomainImports" to result.map { it.domainImport }
        )
        val p = ggplot(data) +
                geom_line {
                    x = "X"
                    y = "SystemImports"
                }  +
                geom_line {
                    x = "X"
                    y = "DomainImports"
                } +
                ggsize(1440, 900) +
                ggtitle("Import Statistics")

        val spec = p.toSpec()

        // Export: use PlotSvgExport utility to generate SVG.
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(spec, null)
        BrowserDemoUtil.openInBrowser(svg)
    }
}