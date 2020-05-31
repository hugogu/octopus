package io.hugogu.octopus.model

import java.time.Instant

data class AllFilesAtCommit(
    val commitId: String,
    val commitTime: Instant,
    val author: String,
    val files: Iterable<FileWithContent>
)
