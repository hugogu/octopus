package io.hugogu.octopus.model

import java.time.Instant

data class CommitStatistics(
    val index: Int,
    val commitTime: Instant,
    val commitId: String,
    val fileChanges: Iterable<FileChangeStatistics>
) {
    fun getTotalAdded() = fileChanges.sumBy { it.added }

    fun getTotalDeleted() = fileChanges.sumBy { it.deleted }
}

data class FileChangeStatistics(
    val path: String,
    val added: Int,
    val deleted: Int
)

fun Iterable<CommitStatistics>.getAllFiles() = flatMap { it.fileChanges.map { it.path } }.toSet()
