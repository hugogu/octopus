package io.hugogu.octopus.model

data class CodeFileMeasurement(
    val path: String,
    val systemImport: Int,
    val domainImport: Int,
    val ifCount: Int,
    val lineCount: Int
)

fun Iterable<CodeFileMeasurement>.getIfPercent(): Double =
    sumByDouble { it.ifCount.toDouble() } / sumByDouble { it.lineCount.toDouble() }

fun Iterable<CodeFileMeasurement>.getTotalSystemImportPercent(): Double =
    sumByDouble { it.systemImport.toDouble() } / sumByDouble { it.systemImport.toDouble() + it.domainImport }

fun Iterable<CodeFileMeasurement>.getSystemImportUsedRate(): Double =
    count { it.systemImport > 0 } * 1.0 / count()

