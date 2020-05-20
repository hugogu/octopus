package io.hugogu.octopus.git

import io.hugogu.octopus.model.CommitStatistics
import io.hugogu.octopus.model.FileChangeStatistics
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.util.io.DisabledOutputStream
import java.io.File

class GitFileActivityDetector {
    fun extractCommitStatistics(path: String) =
        extractCommitStatistics(Git.open(File(path)))

    fun extractCommitStatistics(git: Git): Iterable<CommitStatistics> {
        val commits = git.log().call().reversed()
        val treeWalk = TreeWalk(git.repository)
        val formatter = DiffFormatter(DisabledOutputStream.INSTANCE)
        formatter.setRepository(git.repository)
        formatter.setDiffComparator(RawTextComparator.DEFAULT)
        formatter.isDetectRenames = true
        return commits.mapIndexed { index, commit ->
            val description = git.describe().setTarget(commit).call()
            val diffs = formatter.scan(commit.parents.firstOrNull(), commit.id)
            val addedMap = mutableMapOf<String, Int>()
            val deletedMap = mutableMapOf<String, Int>()
            diffs.forEach { diff ->
                val fileHeader = formatter.toFileHeader(diff)
                addedMap[fileHeader.newPath] = fileHeader.toEditList().sumBy { it.endB - it.beginB }
                deletedMap[fileHeader.oldPath] = fileHeader.toEditList().sumBy { it.endA - it.beginA }
            }
            treeWalk.reset(commit.tree)
            val fileChanges =
            (addedMap.keys + deletedMap.keys).map { file ->
                FileChangeStatistics(file, addedMap.getOrDefault(file, 0), deletedMap.getOrDefault(file, 0))
            }
            CommitStatistics(index, commit.committerIdent.`when`.toInstant(), commit.id.name, fileChanges)
        }
    }
}