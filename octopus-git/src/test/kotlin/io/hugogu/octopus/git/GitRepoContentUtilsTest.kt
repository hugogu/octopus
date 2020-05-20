package io.hugogu.octopus.git

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class GitRepoContentUtilsTest {
    @Test
    fun readElementsAtTest() {
        val git = Git.open(File(".."))
        val elements = GitRepoContentUtils.readElementsAt(git.repository, "45cd55bb5cf9aa1129a02b40029d8c10e113fac0", "")
        assertNotNull(elements)
    }
}