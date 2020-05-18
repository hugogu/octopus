package io.hugogu.octopus.visual.demo

import io.hugogu.octopus.git.GitFileActivityDetector
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.letsPlot.geom.geom_line
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.ggtitle
import jetbrains.letsPlot.intern.toSpec

object SinglePlotSvg {
    @JvmStatic
    fun main(args: Array<String>) {
        val detector = GitFileActivityDetector()
        val statistics = detector.extractCommitStatistics(".")
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
}