/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteListCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.ketch.KetchLeader;
import org.eclipse.jgit.internal.ketch.KetchLeaderCache;
import org.eclipse.jgit.internal.storage.reftree.RefTreeDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.base.FileDiff;
import org.uberfire.java.nio.base.TextualDiff;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.commands.AddRemote;
import org.uberfire.java.nio.fs.jgit.util.commands.BlobAsInputStream;
import org.uberfire.java.nio.fs.jgit.util.commands.CherryPick;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.ConflictBranchesChecker;
import org.uberfire.java.nio.fs.jgit.util.commands.ConvertRefTree;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateBranch;
import org.uberfire.java.nio.fs.jgit.util.commands.DeleteBranch;
import org.uberfire.java.nio.fs.jgit.util.commands.DiffBranches;
import org.uberfire.java.nio.fs.jgit.util.commands.Fetch;
import org.uberfire.java.nio.fs.jgit.util.commands.GarbageCollector;
import org.uberfire.java.nio.fs.jgit.util.commands.GetCommit;
import org.uberfire.java.nio.fs.jgit.util.commands.GetCommonAncestorCommit;
import org.uberfire.java.nio.fs.jgit.util.commands.GetFirstCommit;
import org.uberfire.java.nio.fs.jgit.util.commands.GetLastCommit;
import org.uberfire.java.nio.fs.jgit.util.commands.GetPathInfo;
import org.uberfire.java.nio.fs.jgit.util.commands.GetRef;
import org.uberfire.java.nio.fs.jgit.util.commands.GetTreeFromRef;
import org.uberfire.java.nio.fs.jgit.util.commands.ListCommits;
import org.uberfire.java.nio.fs.jgit.util.commands.ListDiffs;
import org.uberfire.java.nio.fs.jgit.util.commands.ListPathContent;
import org.uberfire.java.nio.fs.jgit.util.commands.ListRefs;
import org.uberfire.java.nio.fs.jgit.util.commands.MapDiffContent;
import org.uberfire.java.nio.fs.jgit.util.commands.Merge;
import org.uberfire.java.nio.fs.jgit.util.commands.Push;
import org.uberfire.java.nio.fs.jgit.util.commands.RefTreeUpdateCommand;
import org.uberfire.java.nio.fs.jgit.util.commands.RemoveRemote;
import org.uberfire.java.nio.fs.jgit.util.commands.ResolveObjectIds;
import org.uberfire.java.nio.fs.jgit.util.commands.ResolveRevCommit;
import org.uberfire.java.nio.fs.jgit.util.commands.RevertMerge;
import org.uberfire.java.nio.fs.jgit.util.commands.SimpleRefUpdateCommand;
import org.uberfire.java.nio.fs.jgit.util.commands.Squash;
import org.uberfire.java.nio.fs.jgit.util.commands.SyncRemote;
import org.uberfire.java.nio.fs.jgit.util.commands.TextualDiffBranches;
import org.uberfire.java.nio.fs.jgit.util.commands.UpdateRemoteConfig;
import org.uberfire.java.nio.fs.jgit.util.model.CommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.CommitHistory;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;

import static org.uberfire.java.nio.fs.jgit.util.commands.PathUtil.normalize;

public class GitImpl implements Git {

    private static final Logger LOG = LoggerFactory.getLogger(GitImpl.class);
    private static final String DEFAULT_JGIT_RETRY_SLEEP_TIME = "50";
    private static int JGIT_RETRY_TIMES = initRetryValue();
    private static final int JGIT_RETRY_SLEEP_TIME = initSleepTime();
    private static final String MASTER_BRANCH = "master";
    private boolean isEnabled = false;

    private static int initSleepTime() {
        final ConfigProperties config = new ConfigProperties(System.getProperties());
        return config.get("org.uberfire.nio.git.retry.onfail.sleep",
                          DEFAULT_JGIT_RETRY_SLEEP_TIME).getIntValue();
    }

    private static int initRetryValue() {
        final ConfigProperties config = new ConfigProperties(System.getProperties());
        final String osName = config.get("os.name",
                                         "any").getValue();
        final String defaultRetryTimes;
        if (osName.toLowerCase().contains("windows")) {
            defaultRetryTimes = "10";
        } else {
            defaultRetryTimes = "0";
        }
        try {
            return config.get("org.uberfire.nio.git.retry.onfail.times",
                              defaultRetryTimes).getIntValue();
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private org.eclipse.jgit.api.Git git;
    private KetchLeaderCache leaders;
    private final AtomicBoolean isHeadInitialized = new AtomicBoolean(false);

    public GitImpl(final org.eclipse.jgit.api.Git git) {
        this(git,
             null);
    }

    public GitImpl(final org.eclipse.jgit.api.Git git,
                   final KetchLeaderCache leaders) {
        this.git = git;
        this.leaders = leaders;
    }

    @Override
    public void convertRefTree() {
        try {
            new ConvertRefTree(this).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteRef(final Ref ref) {
        new DeleteBranch(this,
                         ref).execute();
    }

    @Override
    public Ref getRef(final String ref) {
        return new GetRef(git.getRepository(),
                          ref).execute();
    }

    @Override
    public void push(final CredentialsProvider credentialsProvider,
                     final Pair<String, String> remote,
                     final boolean force,
                     final Collection<RefSpec> refSpecs) throws InvalidRemoteException {
        new Push(this,
                 credentialsProvider,
                 remote,
                 force,
                 refSpecs).execute();
    }

    @Override
    public void gc() {
        new GarbageCollector(this).execute();
    }

    @Override
    public RevCommit getCommit(final String commitId) {
        return new GetCommit(this,
                             commitId).execute();
    }

    @Override
    public RevCommit getLastCommit(final String refName) {
        return retryIfNeeded(RuntimeException.class,
                             () -> new GetLastCommit(this,
                                                     refName).execute());
    }

    @Override
    public RevCommit getLastCommit(final Ref ref) throws IOException {
        return new GetLastCommit(this,
                                 ref).execute();
    }

    @Override
    public RevCommit getCommonAncestorCommit(final String branchA,
                                             final String branchB) {
        return new GetCommonAncestorCommit(this,
                                           getLastCommit(branchA),
                                           getLastCommit(branchB)).execute();
    }

    @Override
    public CommitHistory listCommits(final Ref ref,
                                     final String path) throws IOException, GitAPIException {
        return new ListCommits(this,
                               ref,
                               path).execute();
    }

    @Override
    public List<RevCommit> listCommits(final String startCommitId,
                                       final String endCommitId) {
        return listCommits(new GetCommit(this, startCommitId).execute(),
                           new GetCommit(this, endCommitId).execute());
    }

    @Override
    public List<RevCommit> listCommits(final ObjectId startRange,
                                       final ObjectId endRange) {
        return retryIfNeeded(RuntimeException.class,
                             () -> new ListCommits(this,
                                                   startRange,
                                                   endRange).execute()
                                                            .getCommits());
    }

    @Override
    public Repository getRepository() {
        return git.getRepository();
    }

    public DeleteBranchCommand _branchDelete() {
        return git.branchDelete();
    }

    public ListBranchCommand _branchList() {
        return git.branchList();
    }

    public CreateBranchCommand _branchCreate() {
        return git.branchCreate();
    }

    public FetchCommand _fetch() {
        return git.fetch();
    }

    public GarbageCollectCommand _gc() {
        return git.gc();
    }

    public PushCommand _push() {
        return git.push();
    }

    @Override
    public ObjectId getTreeFromRef(final String treeRef) {
        return new GetTreeFromRef(this,
                                  treeRef).execute();
    }

    @Override
    public void fetch(final CredentialsProvider credential,
                      final Pair<String, String> remote,
                      final Collection<RefSpec> refSpecs) throws InvalidRemoteException {
        new Fetch(this,
                  credential,
                  remote,
                  refSpecs).execute();
    }

    @Override
    public void syncRemote(final Pair<String, String> remote) throws InvalidRemoteException {
        new SyncRemote(this,
                       remote).execute();
    }

    @Override
    public List<String> merge(final String source,
                              final String target) {
        return new Merge(this,
                         source,
                         target).execute();
    }

    @Override
    public List<String> merge(final String source,
                              final String target,
                              final boolean noFastForward,
                              final boolean squash,
                              final CommitInfo commitInfo) {
        return new Merge(this,
                         source,
                         target,
                         noFastForward,
                         squash,
                         commitInfo).execute();
    }

    @Override
    public boolean revertMerge(final String source,
                               final String target,
                               final String commonAncestorCommitId,
                               final String mergeCommitId) {
        return new RevertMerge(this,
                               source,
                               target,
                               commonAncestorCommitId,
                               mergeCommitId).execute();
    }

    @Override
    public void cherryPick(final JGitPathImpl target,
                           final String... commits) {
        new CherryPick(this,
                       target.getRefTree(),
                       commits).execute();
    }

    @Override
    public void cherryPick(final String targetBranch,
                           final String... commitsIDs) {
        new CherryPick(this,
                       targetBranch,
                       commitsIDs).execute();
    }

    @Override
    public void createRef(final String source,
                          final String target) {
        new CreateBranch(this,
                         source,
                         target).execute();
    }

    @Override
    public List<FileDiff> diffRefs(final String branchA,
                                   final String branchB) {
        return new DiffBranches(this,
                                branchA,
                                branchB).execute();
    }

    @Override
    public List<TextualDiff> textualDiffRefs(final String branchA,
                                             final String branchB) {
        return new TextualDiffBranches(this,
                                       branchA,
                                       branchB).execute();
    }

    @Override
    public List<TextualDiff> textualDiffRefs(final String branchA,
                                             final String branchB,
                                             final String commitIdBranchA,
                                             final String commitIdBranchB) {
        return new TextualDiffBranches(this,
                                       branchA,
                                       branchB,
                                       commitIdBranchA,
                                       commitIdBranchB).execute();
    }

    @Override
    public List<String> conflictBranchesChecker(final String branchA,
                                                final String branchB) {
        return new ConflictBranchesChecker(this,
                                           branchA,
                                           branchB).execute();
    }

    @Override
    public void squash(final String branch,
                       final String startCommit,
                       final String commitMessage) {
        new Squash(this,
                   branch,
                   startCommit,
                   commitMessage).execute();
    }

    @Override
    public boolean resetWithSquash(String commitMessage) throws IOException {
        return resetWithSquash(MASTER_BRANCH,
                               commitMessage);
    }

    @Override
    public boolean resetWithSquash(final String branch,
                                   final String commitMessage) throws IOException {
        final Ref branchRef = getRef(branch);
        final RevCommit firstCommit = getFirstCommit(branchRef);
        final RevCommit lastCommit = getLastCommit(branchRef);

        if (!firstCommit.equals(lastCommit)) {
            squash(branch,
                   firstCommit.getName(),
                   commitMessage);
            return true;
        }
        return false;
    }

    public LogCommand _log() {
        return git.log();
    }

    @Override
    public boolean commit(final String branchName,
                          final CommitInfo commitInfo,
                          final boolean amend,
                          final ObjectId originId,
                          final CommitContent content) {
        return new Commit(this,
                          branchName,
                          commitInfo,
                          amend,
                          originId,
                          content).execute();
    }

    @Override
    public List<DiffEntry> listDiffs(final String startCommitId,
                                     final String endCommitId) {
        return listDiffs(getCommit(startCommitId).getTree(),
                         getCommit(endCommitId).getTree());
    }

    @Override
    public List<DiffEntry> listDiffs(final ObjectId refA,
                                     final ObjectId refB) {
        return new ListDiffs(this,
                             refA,
                             refB).execute();
    }

    @Override
    public Map<String, File> mapDiffContent(final String branch,
                                            final String startCommitId,
                                            final String endCommitId) {
        return new MapDiffContent(this,
                                  branch,
                                  startCommitId,
                                  endCommitId).execute();
    }

    @Override
    public InputStream blobAsInputStream(final String treeRef,
                                         final String path) {
        return retryIfNeeded(NoSuchFileException.class,
                             () -> new BlobAsInputStream(this,
                                                         treeRef,
                                                         normalize(path)).execute().get());
    }

    @Override
    public RevCommit getFirstCommit(final Ref ref) throws IOException {
        return new GetFirstCommit(this,
                                  ref).execute();
    }

    @Override
    public List<Ref> listRefs() {
        return new ListRefs(git.getRepository()).execute();
    }

    @Override
    public List<ObjectId> resolveObjectIds(final String... commits) {
        return new ResolveObjectIds(this,
                                    commits).execute();
    }

    @Override
    public RevCommit resolveRevCommit(final ObjectId objectId) throws IOException {
        return new ResolveRevCommit(git.getRepository(),
                                    objectId).execute();
    }

    @Override
    public List<RefSpec> updateRemoteConfig(final Pair<String, String> remote,
                                            final Collection<RefSpec> refSpecs) throws IOException, URISyntaxException {
        return new UpdateRemoteConfig(this,
                                      remote,
                                      refSpecs).execute();
    }

    public AddCommand _add() {
        return git.add();
    }

    public CommitCommand _commit() {
        return git.commit();
    }

    public RemoteListCommand _remoteList() {
        return git.remoteList();
    }

    public static CloneCommand _cloneRepository() {
        return org.eclipse.jgit.api.Git.cloneRepository();
    }

    @Override
    public PathInfo getPathInfo(final String branchName,
                                final String path) {
        return retryIfNeeded(RuntimeException.class,
                             () -> new GetPathInfo(this,
                                                   branchName,
                                                   path).execute());
    }

    @Override
    public List<PathInfo> listPathContent(final String branchName,
                                          final String path) {
        return retryIfNeeded(RuntimeException.class,
                             () -> new ListPathContent(this,
                                                       branchName,
                                                       path).execute());
    }

    @Override
    public boolean isHEADInitialized() {
        return isHeadInitialized.get();
    }

    @Override
    public void setHeadAsInitialized() {
        isHeadInitialized.set(true);
    }

    @Override
    public void refUpdate(final String branch,
                          final RevCommit commit)
            throws IOException, ConcurrentRefUpdateException {
        if (getRepository().getRefDatabase() instanceof RefTreeDatabase) {
            new RefTreeUpdateCommand(this,
                                     branch,
                                     commit).execute();
        } else {
            new SimpleRefUpdateCommand(this,
                                       branch,
                                       commit).execute();
        }
    }

    @Override
    public KetchLeader getKetchLeader() {
        try {
            return leaders.get(getRepository());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isKetchEnabled() {
        return isEnabled;
    }

    @Override
    public void enableKetch() {
        isEnabled = true;
    }

    @Override
    public void updateRepo(final Repository repo) {
        this.git = new org.eclipse.jgit.api.Git(repo);
    }

    @Override
    public void updateLeaders(final KetchLeaderCache leaders) {
        this.leaders = leaders;
    }

    @Override
    public void removeRemote(final String remote,
                             final String ref) {
        new RemoveRemote(this,
                         remote,
                         ref).execute();
    }

    @Override
    public void addRemote(final String remote,
                          final String url) {
        new AddRemote(this,
                      remote,
                      url).execute();
    }

    //just for test purposes
    static void setRetryTimes(int retryTimes) {
        JGIT_RETRY_TIMES = retryTimes;
    }

    public static <E extends Throwable, T> T retryIfNeeded(final Class<E> eclazz,
                                                           final ThrowableSupplier<T> supplier) throws E {
        int i = 0;
        do {
            try {
                return supplier.get();
            } catch (final Throwable ex) {
                if (i < (JGIT_RETRY_TIMES - 1)) {
                    try {
                        Thread.sleep(JGIT_RETRY_SLEEP_TIME);
                    } catch (final InterruptedException ignored) {
                    }
                    LOG.debug(String.format("Unexpected exception (%d/%d).",
                                            i + 1,
                                            JGIT_RETRY_TIMES),
                              ex);
                } else {
                    LOG.error(String.format("Unexpected exception (%d/%d).",
                                            i + 1,
                                            JGIT_RETRY_TIMES),
                              ex);
                    if (ex.getClass().isAssignableFrom(eclazz)) {
                        throw (E) ex;
                    }
                    throw new RuntimeException(ex);
                }
            }

            i++;
        } while (i < JGIT_RETRY_TIMES);

        return null;
    }
}
