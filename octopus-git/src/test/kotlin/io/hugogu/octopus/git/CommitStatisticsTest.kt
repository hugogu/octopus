package io.hugogu.octopus.git

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class CommitStatisticsTest {
    @Test
    fun fileChangeTest() {
        val detector = GitFileActivityDetector()
        val statistics = detector.extractCommitStatistics(Git.open(File("..")))
        assertNotNull(statistics)
    }
}