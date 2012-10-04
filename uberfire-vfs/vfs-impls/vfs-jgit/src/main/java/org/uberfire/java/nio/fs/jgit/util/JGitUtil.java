/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.merge.MergeMessageFormatter;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.Merger;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.merge.SquashMessageFormatter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.base.FileTimeImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileAttributes;

import static java.util.Collections.*;
import static org.eclipse.jgit.api.MergeResult.*;
import static org.eclipse.jgit.api.MergeResult.MergeStatus.*;
import static org.eclipse.jgit.lib.Constants.*;
import static org.eclipse.jgit.lib.FileMode.*;
import static org.eclipse.jgit.treewalk.filter.PathFilterGroup.*;
import static org.eclipse.jgit.util.FS.*;
import static org.uberfire.commons.data.Pair.*;
import static org.uberfire.commons.util.Preconditions.*;

public final class JGitUtil {

    private JGitUtil() {
    }

    public static Git newRepository(final File repoFolder) throws IOException {
        checkNotNull("repoFolder", repoFolder);

        try {
            return Git.init().setBare(true).setDirectory(repoFolder).call();
        } catch (GitAPIException e) {
            throw new IOException(e);
        }
    }

    public static List<Ref> branchList(final Git git) {
        checkNotNull("git", git);
        return branchList(git, null);
    }

    public static List<Ref> branchList(final Git git, final ListBranchCommand.ListMode listMode) {
        checkNotNull("git", git);
        try {
            return git.branchList().setListMode(listMode).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream resolveInputStream(final Git git, final String treeRef, final String path) {
        checkNotNull("git", git);
        checkNotEmpty("treeRef", treeRef);
        checkNotEmpty("path", path);

        final String gitPath = fixPath(path);

        RevWalk rw = null;
        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve(treeRef + "^{tree}");
            rw = new RevWalk(git.getRepository());
            tw = new TreeWalk(git.getRepository());
            tw.setFilter(createFromStrings(singleton(gitPath)));
            tw.reset(tree);
            while (tw.next()) {
                if (tw.isSubtree() && !gitPath.equals(tw.getPathString())) {
                    tw.enterSubtree();
                    continue;
                }
                final ObjectId entid = tw.getObjectId(0);
                final FileMode entmode = tw.getFileMode(0);
                final RevObject ro = rw.lookupAny(entid, entmode.getObjectType());
                rw.parseBody(ro);
                final ObjectLoader ldr = git.getRepository().open(ro.getId(), Constants.OBJ_BLOB);
                return ldr.openStream();
            }
        } catch (final Throwable t) {
            throw new NoSuchFileException("Can't find '" + gitPath + "' in tree '" + treeRef + "'");
        } finally {
            if (rw != null) {
                rw.dispose();
            }
            if (tw != null) {
                tw.release();
            }
        }
        throw new NoSuchFileException("");
    }

    private static String fixPath(final String path) {

        if (path.equals("/")) {
            return "";
        }

        boolean startsWith = path.startsWith("/");
        boolean endsWith = path.endsWith("/");
        if (startsWith && endsWith) {
            return path.substring(1, path.length() - 1);
        }
        if (startsWith) {
            return path.substring(1);
        }
        if (endsWith) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static Git cloneRepository(final File repoFolder,
            final String fromURI, final CredentialsProvider credentialsProvider) {

        if (!repoFolder.getName().endsWith(DOT_GIT_EXT)) {
            throw new RuntimeException("Invalid name");
        }

        try {
            final File gitDir = RepositoryCache.FileKey.resolve(repoFolder, DETECTED);
            final Repository repository;
            final Git git;
            if (gitDir != null && gitDir.exists()) {
                repository = new FileRepository(gitDir);
                git = new Git(repository);
            } else {
                git = Git.cloneRepository()
                        .setBare(true)
                        .setCloneAllBranches(true)
                        .setURI(fromURI)
                        .setDirectory(repoFolder)
                        .setCredentialsProvider(credentialsProvider)
                        .call();
                repository = git.getRepository();
            }

            fetchRepository(git, credentialsProvider);

            repository.close();

            return git;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void fetchRepository(final Git git, final CredentialsProvider credentialsProvider, final RefSpec... refSpecs) {
        final List<RefSpec> specs = new ArrayList<RefSpec>();
        if (refSpecs == null || refSpecs.length == 0) {
            specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
            specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));
            specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
        } else {
            specs.addAll(Arrays.asList(refSpecs));
        }

        try {
            git.fetch()
                    .setCredentialsProvider(credentialsProvider)
                    .setRefSpecs(specs)
                    .call();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void delete(final Git git, final String branchName, final String path,
            final String name, final String email, final String message, final TimeZone timeZone, final Date when) {
        commit(git, branchName, path, null, name, email, message, timeZone, when);
    }

    public static void commit(final Git git, final String branchName, final String path, final File file,
            final String name, final String email, final String message, final TimeZone timeZone, final Date when) {

        final String gitPath = fixPath(path);

        final PersonIdent author = buildPersonIdent(git, name, email, timeZone, when);

        try {
            final ObjectInserter odi = git.getRepository().newObjectInserter();
            try {
                // Create the in-memory index of the new/updated issue.
                final ObjectId headId = git.getRepository().resolve(branchName + "^{commit}");
                final DirCache index = createTemporaryIndex(git, headId, gitPath, file);
                final ObjectId indexTreeId = index.writeTree(odi);

                // Create a commit object
                final CommitBuilder commit = new CommitBuilder();
                commit.setAuthor(author);
                commit.setCommitter(author);
                commit.setEncoding(Constants.CHARACTER_ENCODING);
                commit.setMessage(message);
                //headId can be null if the repository has no commit yet
                if (headId != null) {
                    commit.setParentId(headId);
                }
                commit.setTreeId(indexTreeId);

                // Insert the commit into the repository
                final ObjectId commitId = odi.insert(commit);
                odi.flush();

                final RevWalk revWalk = new RevWalk(git.getRepository());
                try {
                    final RevCommit revCommit = revWalk.parseCommit(commitId);
                    final RefUpdate ru = git.getRepository().updateRef("refs/heads/" + branchName);
                    if (headId == null) {
                        ru.setExpectedOldObjectId(ObjectId.zeroId());
                    } else {
                        ru.setExpectedOldObjectId(headId);
                    }
                    ru.setNewObjectId(commitId);
                    ru.setRefLogMessage("commit: " + revCommit.getShortMessage(), false);
                    final RefUpdate.Result rc = ru.forceUpdate();
                    switch (rc) {
                        case NEW:
                        case FORCED:
                        case FAST_FORWARD:
                            break;
                        case REJECTED:
                        case LOCK_FAILURE:
                            throw new ConcurrentRefUpdateException(JGitText.get().couldNotLockHEAD, ru.getRef(), rc);
                        default:
                            throw new JGitInternalException(MessageFormat.format(JGitText.get().updatingRefFailed, Constants.HEAD, commitId.toString(), rc));
                    }

                } finally {
                    revWalk.release();
                }
            } finally {
                odi.release();
            }
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static PersonIdent buildPersonIdent(final Git git, final String name, final String email,
            final TimeZone timeZone, final Date when) {
        final TimeZone tz = timeZone == null ? TimeZone.getDefault() : timeZone;

        if (name != null) {
            if (when != null) {
                return new PersonIdent(name, email, when, tz);
            } else {
                return new PersonIdent(name, email);
            }
        }
        return new PersonIdent(git.getRepository());
    }

    /**
     * Creates an in-memory index of the issue change.
     */
    private static DirCache createTemporaryIndex(final Git git, final ObjectId headId, final String path, final File file) {

        final DirCache inCoreIndex = DirCache.newInCore();
        final DirCacheBuilder dcBuilder = inCoreIndex.builder();
        final ObjectInserter inserter = git.getRepository().newObjectInserter();

        try {
            if (file != null) {
                final DirCacheEntry dcEntry = new DirCacheEntry(path);
                dcEntry.setLength(file.length());
                dcEntry.setLastModified(file.lastModified());
                dcEntry.setFileMode(REGULAR_FILE);

                final InputStream inputStream = new FileInputStream(file);
                try {
                    dcEntry.setObjectId(inserter.insert(Constants.OBJ_BLOB, file.length(), inputStream));
                } finally {
                    inputStream.close();
                }

                dcBuilder.add(dcEntry);
            }

            if (headId != null) {
                final TreeWalk treeWalk = new TreeWalk(git.getRepository());
                final int hIdx = treeWalk.addTree(new RevWalk(git.getRepository()).parseTree(headId));
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    final String walkPath = treeWalk.getPathString();
                    final CanonicalTreeParser hTree = treeWalk.getTree(hIdx, CanonicalTreeParser.class);

                    if (!walkPath.equals(path)) {
                        // add entries from HEAD for all other paths
                        // create a new DirCacheEntry with data retrieved from HEAD
                        final DirCacheEntry dcEntry = new DirCacheEntry(walkPath);
                        dcEntry.setObjectId(hTree.getEntryObjectId());
                        dcEntry.setFileMode(hTree.getEntryFileMode());

                        // add to temporary in-core index
                        dcBuilder.add(dcEntry);
                    }
                }
                treeWalk.release();
            }

            dcBuilder.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            inserter.release();
        }

        if (file == null) {
            final DirCacheEditor editor = inCoreIndex.editor();
            editor.add(new DirCacheEditor.DeleteTree(path));
            editor.finish();
        }

        return inCoreIndex;
    }

    public static Ref getBranch(final Git git, final String name) {

        try {
            return git.getRepository().getRefDatabase().getRef("refs/heads/" + name);
        } catch (java.io.IOException e) {
        }

        return null;
    }

    public static void deleteBranch(final Git git, final Ref branch) {
        try {
            git.branchDelete().setBranchNames(branch.getName()).setForce(true).call();
        } catch (final GitAPIException e) {
            throw new IOException(e);
        }
    }

    public static BasicFileAttributes buildBasicFileAttributes(final Git git, final String branchName, final String path) {

        long createDate = Long.MAX_VALUE;
        long lastModified = Long.MIN_VALUE;

        final JGitPathInfo pathInfo = resolvePath(git, branchName, path);

        if (pathInfo == null) {
            throw new NoSuchFileException(path);
        }

        final String gPath = fixPath(path);

        try {
            final LogCommand logCommand = git.log().add(getBranch(git, branchName).getObjectId());
            if (!gPath.isEmpty()) {
                logCommand.addPath(gPath);
            }

            for (final RevCommit commit : logCommand.call()) {
                if (commit.getAuthorIdent().getWhen().getTime() < createDate) {
                    createDate = commit.getAuthorIdent().getWhen().getTime();
                }
                if (commit.getAuthorIdent().getWhen().getTime() > lastModified) {
                    lastModified = commit.getAuthorIdent().getWhen().getTime();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new JGitFileAttributes(pathInfo.getObjectId() == null ? null : pathInfo.getObjectId().toString(), new FileTimeImpl(lastModified), new FileTimeImpl(createDate), pathInfo.getSize(), pathInfo.getPathType().equals(PathType.FILE), pathInfo.getPathType().equals(PathType.DIRECTORY));
    }

    public static void createBranch(final Git git, final String source, final String target) {
        try {
            git.branchCreate().setName(target).setStartPoint(source).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasBranch(final Git git, final String branchName) {
        checkNotNull("git", git);
        checkNotEmpty("branchName", branchName);

        return getBranch(git, branchName) != null;
    }

    public static enum PathType {
        NOT_FOUND, DIRECTORY, FILE;
    }

    public static Pair<PathType, ObjectId> checkPath(final Git git, final String branchName, final String path) {
        checkNotNull("git", git);
        checkNotNull("path", path);
        checkNotEmpty("branchName", branchName);

        final String gitPath = fixPath(path);

        if (gitPath.isEmpty()) {
            return newPair(PathType.DIRECTORY, null);
        }

        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve(branchName + "^{tree}");
            tw = new TreeWalk(git.getRepository());
            tw.setFilter(PathFilter.create(gitPath));
            tw.reset(tree);
            while (tw.next()) {
                if (tw.getPathString().equals(gitPath)) {
                    if (tw.getFileMode(0).equals(FileMode.TYPE_TREE)) {
                        return newPair(PathType.DIRECTORY, tw.getObjectId(0));
                    } else if (tw.getFileMode(0).equals(FileMode.TYPE_FILE)) {
                        return newPair(PathType.FILE, tw.getObjectId(0));
                    }
                }
                if (tw.isSubtree()) {
                    tw.enterSubtree();
                    continue;
                }
            }
        } catch (final Throwable t) {
        } finally {
            if (tw != null) {
                tw.release();
            }
        }
        return newPair(PathType.NOT_FOUND, null);
    }

    public static JGitPathInfo resolvePath(final Git git, final String branchName, final String path) {
        checkNotNull("git", git);
        checkNotNull("path", path);
        checkNotEmpty("branchName", branchName);

        final String gitPath = fixPath(path);

        if (gitPath.isEmpty()) {
            return new JGitPathInfo(null, "/", TREE);
        }

        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve(branchName + "^{tree}");
            tw = new TreeWalk(git.getRepository());
            tw.setFilter(PathFilter.create(gitPath));
            tw.reset(tree);
            while (tw.next()) {
                if (tw.getPathString().equals(gitPath)) {
                    if (tw.getFileMode(0).equals(TREE)) {
                        return new JGitPathInfo(tw.getObjectId(0), tw.getPathString(), TREE);
                    } else if (tw.getFileMode(0).equals(REGULAR_FILE) || tw.getFileMode(0).equals(EXECUTABLE_FILE)) {
                        final long size = tw.getObjectReader().getObjectSize(tw.getObjectId(0), OBJ_BLOB);
                        return new JGitPathInfo(tw.getObjectId(0), tw.getPathString(), REGULAR_FILE, size);
                    }
                }
                if (tw.isSubtree()) {
                    tw.enterSubtree();
                    continue;
                }
            }
        } catch (final Throwable t) {
        } finally {
            if (tw != null) {
                tw.release();
            }
        }

        return null;
    }

    public static List<JGitPathInfo> listPathContent(final Git git, final String branchName, final String path) {
        checkNotNull("git", git);
        checkNotNull("path", path);
        checkNotEmpty("branchName", branchName);

        final String gitPath = fixPath(path);

        TreeWalk tw = null;
        final List<JGitPathInfo> result = new ArrayList<JGitPathInfo>();
        try {
            final ObjectId tree = git.getRepository().resolve(branchName + "^{tree}");
            tw = new TreeWalk(git.getRepository());
            boolean found = false;
            if (gitPath.isEmpty()) {
                found = true;
            } else {
                tw.setFilter(PathFilter.create(gitPath));
            }
            tw.reset(tree);
            while (tw.next()) {
                if (!found && tw.isSubtree()) {
                    tw.enterSubtree();
                }
                if (tw.getPathString().equals(gitPath)) {
                    found = true;
                    continue;
                }
                if (found) {
                    result.add(new JGitPathInfo(tw.getObjectId(0), tw.getPathString(), tw.getFileMode(0)));
                }
            }
        } catch (final Throwable t) {
        } finally {
            if (tw != null) {
                tw.release();
            }
        }

        return result;
    }

    public static MergeResult mergeBranches(final Git git, final String source, final String target)
            throws Exception {

        final Repository repo = git.getRepository();
        final MergeStrategy mergeStrategy = MergeStrategy.RESOLVE;
        final List<Ref> commits = new LinkedList<Ref>();
        final boolean squash = false;

        RevWalk revWalk = null;
        DirCacheCheckout dco = null;
        try {
            Ref head = repo.getRef(Constants.HEAD);
            if (head == null) {
                throw new NoHeadException(JGitText.get().commitOnRepoWithoutHEADCurrentlyNotSupported);
            }
            final StringBuilder refLogMessage = new StringBuilder("merge ");

            // Check for FAST_FORWARD, ALREADY_UP_TO_DATE
            revWalk = new RevWalk(repo);

            // we know for now there is only one commit
            Ref ref = commits.get(0);

            refLogMessage.append(ref.getName());

            // handle annotated tags
            ObjectId objectId = ref.getPeeledObjectId();
            if (objectId == null) {
                objectId = ref.getObjectId();
            }

            final RevCommit srcCommit = revWalk.lookupCommit(objectId);

            ObjectId headId = head.getObjectId();
            if (headId == null) {
                revWalk.parseHeaders(srcCommit);
                dco = new DirCacheCheckout(repo,
                        repo.lockDirCache(), srcCommit.getTree());
                dco.setFailOnConflict(true);
                dco.checkout();
                RefUpdate refUpdate = repo
                        .updateRef(head.getTarget().getName());
                refUpdate.setNewObjectId(objectId);
                refUpdate.setExpectedOldObjectId(null);
                refUpdate.setRefLogMessage("initial pull", false);
                if (refUpdate.update() != RefUpdate.Result.NEW) {
                    throw new NoHeadException(JGitText.get().commitOnRepoWithoutHEADCurrentlyNotSupported);
                }

                return new MergeResult(srcCommit, srcCommit, new ObjectId[]{
                        null, srcCommit}, MergeStatus.FAST_FORWARD,
                        mergeStrategy, null, null);
            }

            final RevCommit headCommit = revWalk.lookupCommit(headId);

            if (revWalk.isMergedInto(srcCommit, headCommit)) {
                return new MergeResult(headCommit, srcCommit,
                        new ObjectId[]{headCommit, srcCommit},
                        ALREADY_UP_TO_DATE, mergeStrategy, null, null);
            } else if (revWalk.isMergedInto(headCommit, srcCommit)) {
                // FAST_FORWARD detected: skip doing a real merge but only
                // update HEAD
                refLogMessage.append(": " + FAST_FORWARD);
                dco = new DirCacheCheckout(repo, headCommit.getTree(), repo.lockDirCache(), srcCommit.getTree());
                dco.setFailOnConflict(true);
                dco.checkout();
                String msg = null;
                ObjectId newHead, base = null;
                final MergeStatus mergeStatus;
                if (!squash) {
                    updateHead(git, refLogMessage, srcCommit, headId);
                    newHead = base = srcCommit;
                    mergeStatus = FAST_FORWARD;
                } else {
                    msg = JGitText.get().squashCommitNotUpdatingHEAD;
                    newHead = base = headId;
                    mergeStatus = FAST_FORWARD_SQUASHED;
                    final List<RevCommit> squashedCommits = RevWalkUtils.find(revWalk, srcCommit, headCommit);
                    final String squashMessage = new SquashMessageFormatter().format(squashedCommits, head);
                    repo.writeSquashCommitMsg(squashMessage);
                }
                return new MergeResult(newHead, base, new ObjectId[]{
                        headCommit, srcCommit}, mergeStatus, mergeStrategy,
                        null, msg);
            } else {
                String mergeMessage = "";
                if (!squash) {
                    mergeMessage = new MergeMessageFormatter().format(commits, head);
                    repo.writeMergeCommitMsg(mergeMessage);
                    repo.writeMergeHeads(Arrays.asList(ref.getObjectId()));
                } else {
                    final List<RevCommit> squashedCommits = RevWalkUtils.find(revWalk, srcCommit, headCommit);
                    final String squashMessage = new SquashMessageFormatter().format(squashedCommits, head);
                    repo.writeSquashCommitMsg(squashMessage);
                }
                boolean noProblems;
                final Merger merger = mergeStrategy.newMerger(repo);
                final Map<String, org.eclipse.jgit.merge.MergeResult<?>> lowLevelResults;
                final Map<String, ResolveMerger.MergeFailureReason> failingPaths;
                final List<String> unmergedPaths;

                if (merger instanceof ResolveMerger) {
                    ResolveMerger resolveMerger = (ResolveMerger) merger;
                    resolveMerger.setCommitNames(new String[]{"BASE", "HEAD", ref.getName()});
                    resolveMerger.setWorkingTreeIterator(new FileTreeIterator(repo));
                    noProblems = merger.merge(headCommit, srcCommit);
                    lowLevelResults = (Map<String, org.eclipse.jgit.merge.MergeResult<?>>) resolveMerger.getMergeResults();
                    failingPaths = resolveMerger.getFailingPaths();
                    unmergedPaths = resolveMerger.getUnmergedPaths();
                } else {
                    noProblems = merger.merge(headCommit, srcCommit);
                    lowLevelResults = emptyMap();
                    failingPaths = emptyMap();
                    unmergedPaths = emptyList();
                }

                refLogMessage.append(": Merge made by ");
                refLogMessage.append(mergeStrategy.getName());
                refLogMessage.append('.');
                if (noProblems) {
                    dco = new DirCacheCheckout(repo, headCommit.getTree(), repo.lockDirCache(), merger.getResultTreeId());
                    dco.setFailOnConflict(true);
                    dco.checkout();

                    String msg = null;
                    RevCommit newHead = null;
                    MergeStatus mergeStatus = null;
                    if (!squash) {
                        newHead = new Git(repo).commit().setReflogComment(refLogMessage.toString()).call();
                        mergeStatus = MERGED;
                    } else {
                        msg = JGitText.get().squashCommitNotUpdatingHEAD;
                        newHead = headCommit;
                        mergeStatus = MERGED_SQUASHED;
                    }
                    return new MergeResult(newHead.getId(), null,
                            new ObjectId[]{headCommit.getId(), srcCommit.getId()},
                            mergeStatus, mergeStrategy, null, msg);
                } else {
                    if (failingPaths != null && !failingPaths.isEmpty()) {
                        repo.writeMergeCommitMsg(null);
                        repo.writeMergeHeads(null);
                        return new MergeResult(null, merger.getBaseCommit(0, 1),
                                new ObjectId[]{headCommit.getId(), srcCommit.getId()},
                                FAILED, mergeStrategy, lowLevelResults, failingPaths, null);
                    } else {
                        final String mergeMessageWithConflicts =
                                new MergeMessageFormatter().formatWithConflicts(mergeMessage, unmergedPaths);
                        repo.writeMergeCommitMsg(mergeMessageWithConflicts);
                        return new MergeResult(null, merger.getBaseCommit(0, 1),
                                new ObjectId[]{headCommit.getId(), srcCommit.getId()},
                                CONFLICTING, mergeStrategy, lowLevelResults, null);
                    }
                }
            }
        } catch (org.eclipse.jgit.errors.CheckoutConflictException e) {
            final List<String> conflicts = (dco == null) ? Collections.<String>emptyList() : dco.getConflicts();
            throw new CheckoutConflictException(conflicts, e);
        } catch (java.io.IOException e) {
            throw new JGitInternalException(MessageFormat.format(JGitText.get().exceptionCaughtDuringExecutionOfMergeCommand, e), e);
        } finally {
            if (revWalk != null) {
                revWalk.release();
            }
        }
    }

    private static void updateHead(final Git git, final StringBuilder refLogMessage, final ObjectId newHeadId, final ObjectId oldHeadID)
            throws java.io.IOException,
            ConcurrentRefUpdateException {
        RefUpdate refUpdate = git.getRepository().updateRef(Constants.HEAD);
        refUpdate.setNewObjectId(newHeadId);
        refUpdate.setRefLogMessage(refLogMessage.toString(), false);
        refUpdate.setExpectedOldObjectId(oldHeadID);
        RefUpdate.Result rc = refUpdate.update();
        switch (rc) {
            case NEW:
            case FAST_FORWARD:
                return;
            case REJECTED:
            case LOCK_FAILURE:
                throw new ConcurrentRefUpdateException(
                        JGitText.get().couldNotLockHEAD, refUpdate.getRef(), rc);
            default:
                throw new JGitInternalException(MessageFormat.format(
                        JGitText.get().updatingRefFailed, Constants.HEAD,
                        newHeadId.toString(), rc));
        }
    }

    public static class JGitPathInfo {

        private final ObjectId objectId;
        private final String path;
        private final long size;
        private final PathType pathType;

        public JGitPathInfo(final ObjectId objectId, final String path, final FileMode fileMode) {
            this(objectId, path, fileMode, -1);
        }

        public JGitPathInfo(final ObjectId objectId, final String path, final FileMode fileMode, long size) {
            this.objectId = objectId;
            this.size = size;
            this.path = path;

            if (fileMode.equals(FileMode.TYPE_TREE)) {
                this.pathType = PathType.DIRECTORY;
            } else if (fileMode.equals(TYPE_FILE)) {
                this.pathType = PathType.FILE;
            } else {
                this.pathType = null;
            }
        }

        public ObjectId getObjectId() {
            return objectId;
        }

        public String getPath() {
            return path;
        }

        public PathType getPathType() {
            return pathType;
        }

        public long getSize() {
            return size;
        }
    }

}
