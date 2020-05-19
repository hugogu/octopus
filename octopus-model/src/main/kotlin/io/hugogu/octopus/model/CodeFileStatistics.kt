package io.hugogu.octopus.model

data class CodeFileMeasurement(
    val path: String,
    val systemImport: Int,
    val domainImport: Int
)