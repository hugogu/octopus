package io.hugogu.octopus.git

import io.hugogu.octopus.model.AllFilesAtCommit
import io.hugogu.octopus.model.FileWithContent
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.FileNotFoundException
import java.io.IOException

object GitRepoContentUtils {
    fun readFileHistory(git: Git, filter: (String) -> Boolean): Iterable<AllFilesAtCommit> {
        val log = git.log().call().reversed()
        return log.map {
            AllFilesAtCommit(
                it.id.name,
                it.authorIdent.`when`.toInstant(),
                readElementsAt(git.repository, it.id.name, "", filter)
            )
        }
    }

    @Throws(IOException::class)
    fun readElementsAt(
        repository: Repository,
        commit: String,
        path: String,
        filter: (String) -> Boolean = { true }
    ): Iterable<FileWithContent> {
        val revCommit = buildRevCommit(repository, commit)

        // and using commit's tree find the path
        val tree = revCommit.tree
        //System.out.println("Having tree: " + tree + " for commit " + commit);
        val items = mutableListOf<FileWithContent>()

        // shortcut for root-path
        if (path.isEmpty()) {
            TreeWalk(repository).use { treeWalk ->
                treeWalk.addTree(tree)
                treeWalk.isRecursive = true
                treeWalk.isPostOrderTraversal = false
                while (treeWalk.next()) {
                    if (filter(treeWalk.pathString)) {
                        val content = repository.open(treeWalk.getObjectId(0)).openStream().reader().readText()
                        items.add(FileWithContent(treeWalk.pathString, content))
                    }
                }
            }
        } else {
            // now try to find a specific file
            buildTreeWalk(repository, tree, path).use { treeWalk ->
                check(treeWalk.getFileMode(0).bits and FileMode.TYPE_TREE != 0) {
                    "Tried to read the elements of a non-tree for commit '$commit' and path '$path', had filemode " + treeWalk.getFileMode(
                        0
                    ).bits
                }
                TreeWalk(repository).use { dirWalk ->
                    dirWalk.addTree(treeWalk.getObjectId(0))
                    dirWalk.isRecursive = true
                    while (dirWalk.next()) {
                        val content = repository.open(treeWalk.getObjectId(0)).openStream().reader().readText()
                        items.add(FileWithContent(treeWalk.pathString, content))
                    }
                }
            }
        }
        return items
    }

    @Throws(IOException::class)
    fun buildRevCommit(repository: Repository, commit: String): RevCommit {
        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk(repository).use { revWalk -> return revWalk.parseCommit(ObjectId.fromString(commit)) }
    }

    @Throws(IOException::class)
    fun buildTreeWalk(repository: Repository, tree: RevTree, path: String): TreeWalk {
        return TreeWalk.forPath(repository, path, tree)
            ?: throw FileNotFoundException("Did not find expected file '" + path + "' in tree '" + tree.name + "'")
    }
}
