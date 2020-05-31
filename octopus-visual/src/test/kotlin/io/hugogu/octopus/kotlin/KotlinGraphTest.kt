package io.hugogu.octopus.kotlin

import io.hugogu.octopus.git.GitFileActivityDetector
import io.hugogu.octopus.git.GitRepoContentUtils
import io.hugogu.octopus.model.getIfPercent
import io.hugogu.octopus.model.getSystemImportUsedRate
import io.hugogu.octopus.model.getTotalSystemImportPercent
import io.hugogu.octopus.visual.demo.BrowserDemoUtil
import org.junit.jupiter.api.Test
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.letsPlot.geom.geom_bar
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
            "Deleted" to statistics.map { -it.getTotalDeleted() },
            "Author" to statistics.map { it.author }
        )
        val p = ggplot(data) +
                geom_line {
                    x = "X"
                    y = "Added"
                    color = "Author"
                } +
                geom_line {
                    x = "X"
                    y = "Deleted"
                    color = "Author"
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
        val git = Git.open(File("/Users/hugogu-awx/Projects/airwallex-rails-dbs"))
        val history = GitRepoContentUtils.readFileHistory(git) { it.endsWith("kt") }
        val historyStats = history.map {
            it to KotlinMeasurementFacade.extractImportUsage(it)
        }
        val data = mapOf<String, Any>(
            "X" to 0.until(historyStats.size).toList(),
            "Author" to historyStats.map { it.first.author },
            "SysImportPercent" to historyStats.map { it.second.getTotalSystemImportPercent() },
            "SysImportUseRate" to historyStats.map { it.second.getSystemImportUsedRate() },
            "IfPercent" to historyStats.map { it.second.getIfPercent() },
            "IfTotal" to historyStats.map { it.second.sumBy { it.ifCount }}
        )

        val p = ggplot(data) +
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