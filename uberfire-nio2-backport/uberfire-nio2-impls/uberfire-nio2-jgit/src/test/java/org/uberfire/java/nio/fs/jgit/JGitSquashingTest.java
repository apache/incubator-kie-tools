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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand.InteractiveHandler;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IllegalTodoFileModification;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RebaseTodoLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.commands.Squash;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.DIRECTORY;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.FILE;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.NOT_FOUND;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.cloneRepository;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.commit;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.resolveObjectId;

public class JGitSquashingTest extends AbstractTestInfra {

    static {
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("guest",
                                                                               ""));
    }

    private Logger logger = LoggerFactory.getLogger(JGitSquashingTest.class);

    /*
     * The following test shows how to do a rebase with Fixup to squash a set of commits
     * Notice that RebaseCommand only works on non-BARE repos that's why I need to clone 
     *   the original repo using the BARE repo flag set to false
     * This initial test is here to demonstrate what we need to achieve on the BARE repo
     */
    @Test
    public void rawRebaseWithFixUp() throws IOException, GitAPIException {

        logger.info(">>>>>>>>>>>>>>>>>>> rawRebaseWithFixUp");

        final File parentFolder = createTempDirectory();

        final File gitFolder = new File(parentFolder,
                                        "myrepo.git");

        final Git origin = JGitUtil.newRepository(gitFolder,
                                                  true);

        final File gitClonedFolder = new File(parentFolder,
                                              "myclone.git");

        final Git clone = cloneRepository(gitClonedFolder,
                                          origin.getRepository().getDirectory().toString(),
                                          false,
                                          CredentialsProvider.getDefault());

        createAddAndCommitFile(clone,
                               "testfile0");

        Iterable<RevCommit> logs = clone.log().all().setMaxCount(1).call();
        Iterator<RevCommit> iterator = logs.iterator();
        assertThat(iterator.hasNext()).isTrue();
        RevCommit firstCommit = iterator.next();

        createAddAndCommitFile(clone,
                               "testfile1");

        createAddAndCommitFile(clone,
                               "testfile2");

        createAddAndCommitFile(clone,
                               "testfile3");

        logs = clone.log().all().setMaxCount(1).call();
        iterator = logs.iterator();
        assertThat(iterator.hasNext()).isTrue();
        final RevCommit thirdCommit = iterator.next();
        createAddAndCommitFile(clone,
                               "testfile4");

        final String squashedCommitMessage = "I'm here to squash some changes";
        final RevCommit lastSquashedCommit = thirdCommit;
        InteractiveHandler handler = new InteractiveHandler() {
            public void prepareSteps(List<RebaseTodoLine> steps) {
                try {

                    int counter = 0;
                    for (RebaseTodoLine step : steps) {
                        if (counter == 0) {
                            step.setAction(RebaseTodoLine.Action.PICK);
                        } else {
                            step.setAction(RebaseTodoLine.Action.SQUASH);
                        }
                        if (step.getCommit().prefixCompare(lastSquashedCommit) == 0) {
                            break;
                        }
                        counter++;
                    }
                } catch (IllegalTodoFileModification ex) {
                    logger.error(ex.getLocalizedMessage(),
                                 ex);
                }
            }

            @Override
            public String modifyCommitMessage(String oldMessage) {
                return squashedCommitMessage;
            }
        };

        logger.info("#### Before Rebase");
        int counter = 0;
        for (RevCommit commit : clone.log().all().call()) {
            logger.info(">Commit: " + commit.getFullMessage());
            counter++;
        }
        logger.info("#### Before Rebase Commits: " + counter);

        RebaseResult rebaseResult = clone.rebase().setUpstream(firstCommit).runInteractively(handler).call();
        assertThat(rebaseResult.getStatus()).isSameAs(RebaseResult.Status.OK);

        logger.info("#### After Rebase");
        counter = 0;
        for (RevCommit commit : clone.log().all().call()) {
            logger.info(">Commit: " + commit.getFullMessage());
            counter++;
        }
        logger.info("#### After Rebase Commits: " + counter);

        logger.info(">>>>>>>>>>>>>>>>>>> END rawRebaseWithFixUp");
    }

    /*
     * This test make 5 commits and then squah the last 4 into a single commit
    */
    @Test
    public void testSquash4Of5Commits() throws IOException, GitAPIException {

        final File parentFolder = createTempDirectory();
        logger.info(">> Parent Forlder for the Test: " + parentFolder.getAbsolutePath());
        final File gitFolder = new File(parentFolder,
                                        "my-local-repo.git");

        final Git origin = JGitUtil.newRepository(gitFolder,
                                                  true);

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 1!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file1.txt",
                           tempFile("initial content file 1"));
                   }
               });
        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 2!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file2.txt",
                           tempFile("initial content file 2"));
                   }
               });
        Iterable<RevCommit> logs = origin.log().setMaxCount(1).all().call();
        RevCommit secondCommit = logs.iterator().next();

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 3!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file1.txt",
                           tempFile("new content file 1"));
                   }
               });

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 4!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file2.txt",
                           tempFile("new content file 2"));
                   }
               });
        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 5!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file3.txt",
                           tempFile("initial content file 3"));
                   }
               });
        logs = origin.log().all().call();
        int commitsCount = 0;
        for (RevCommit commit : logs) {
            logger.info(">>> Origin Commit: " + commit.getFullMessage() + " - " + commit.toString());
            commitsCount++;
        }
        assertThat(commitsCount).isEqualTo(5);

        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "pathx/").getK1()).isEqualTo(NOT_FOUND);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to/file1.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to/file2.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to/file3.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to").getK1()).isEqualTo(DIRECTORY);

        logger.info("Squashing from " + secondCommit.getName() + "  to HEAD");
        new Squash(origin,
                   "master",
                   secondCommit.getName(),
                   "squashed message").execute();

        commitsCount = 0;
        for (RevCommit commit : origin.log().all().call()) {
            logger.info(">>> Final Commit: " + commit.getFullMessage() + " - " + commit.toString());
            commitsCount++;
        }
        assertThat(commitsCount).isEqualTo(2);
    }

    @Test
    public void testFailWhenTryToSquashCommitsFromDifferentBranches() throws IOException, GitAPIException {

        final File parentFolder = createTempDirectory();
        logger.info(">> Parent Forlder for the Test: " + parentFolder.getAbsolutePath());
        final File gitFolder = new File(parentFolder,
                                        "my-local-repo.git");

        final Git origin = JGitUtil.newRepository(gitFolder,
                                                  true);

        commit(origin,
               "master",
               "aparedes",
               "aparedes@example.com",
               "commit 1!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file1.txt",
                           tempFile("initial content file 1"));
                   }
               });
        commit(origin,
               "develop",
               "salaboy",
               "salaboy@example.com",
               "commit 2!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file2.txt",
                           tempFile("initial content file 2"));
                   }
               });
        commit(origin,
               "master",
               "aparedes",
               "aparedes@example.com",
               "commit 3!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file3.txt",
                           tempFile("initial content file 1"));
                   }
               });
        commit(origin,
               "master",
               "aparedes",
               "aparedes@example.com",
               "commit 4!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file4.txt",
                           tempFile("initial content file 1"));
                   }
               });

        List<RevCommit> masterCommits = getCommitsFromBranch(origin,
                                                             "master");
        List<RevCommit> developCommits = getCommitsFromBranch(origin,
                                                              "develop");

        assertThat(masterCommits.size()).isEqualTo(3);
        assertThat(developCommits.size()).isEqualTo(1);

        try {
            new Squash(origin,
                       "master",
                       developCommits.get(0).getName(),
                       "squashed message").execute();
            fail("If it reaches here the test has failed because he found the commit into the branch");
        } catch (GitException e) {
            logger.info(e.getMessage());
            assertThat(e).isNotNull();
        }
    }

    private List<RevCommit> getCommitsFromBranch(final Git origin,
                                                 String branch) throws GitAPIException, MissingObjectException, IncorrectObjectTypeException {
        List<RevCommit> commits = new ArrayList<>();
        final ObjectId id = resolveObjectId(origin,
                                            branch);
        for (RevCommit commit : origin.log().add(id).call()) {
            logger.info(">>> " + branch + " Commits: " + commit.getFullMessage() + " - " + commit.toString());
            commits.add(commit);
        }
        return commits;
    }

    /*
     * This test also perform 5 commits and squash the last 4 into a single commit
     *  but now the changes are in different paths
    */
    @Test
    public void testSquashCommitsWithDifferentPaths() throws IOException, GitAPIException {

        final File parentFolder = createTempDirectory();
        logger.info(">> Parent Folder for the Test: " + parentFolder.getAbsolutePath());
        final File gitFolder = new File(parentFolder,
                                        "my-local-repo.git");

        final Git origin = JGitUtil.newRepository(gitFolder,
                                                  true);

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 1!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("file1.txt",
                           tempFile("initial content file 1"));
                   }
               });
        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 2!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file2.txt",
                           tempFile("initial content file 2"));
                   }
               });
        Iterable<RevCommit> logs = origin.log().setMaxCount(1).all().call();
        RevCommit secondCommit = logs.iterator().next();

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 3!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("file1.txt",
                           tempFile("new content file 1"));
                   }
               });

        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 4!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/to/file2.txt",
                           tempFile("new content file 2"));
                   }
               });
        commit(origin,
               "master",
               "salaboy",
               "salaboy@example.com",
               "commit 5!",
               null,
               null,
               false,
               new HashMap<String, File>() {
                   {
                       put("path/file3.txt",
                           tempFile("initial content file 3"));
                   }
               });

        for (RevCommit commit : origin.log().all().call()) {
            logger.info(">>> Origin Commit: " + commit.getFullMessage() + " - " + commit.toString());
        }

        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "pathx/").getK1()).isEqualTo(NOT_FOUND);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "file1.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to/file2.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/file3.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin,
                                      "master",
                                      "path/to").getK1()).isEqualTo(DIRECTORY);

        logger.info("Squashing from " + secondCommit.getName() + "  to HEAD");
        new Squash(origin,
                   "master",
                   secondCommit.getName(),
                   "squashed message").execute();

        int commitsCount = 0;
        for (RevCommit commit : origin.log().all().call()) {
            logger.info(">>> Final Commit: " + commit.getFullMessage() + " - " + commit.toString());
            commitsCount++;
        }

        assertThat(commitsCount).isEqualTo(2);
    }

    @Test(expected = IllegalStateException.class)
    public void repositoryIsBareTest() throws IOException {

        final File parentFolder = createTempDirectory();

        final File gitFolder = new File(parentFolder,
                                        "myrepo.git");
        final Git origin = JGitUtil.newRepository(gitFolder,
                                                  false);

        new Squash(origin,
                   "master",
                   null,
                   "squashed message").execute();
    }

    private void createAddAndCommitFile(Git git,
                                        String file) throws GitAPIException, IOException {
        File myfile = new File(git.getRepository().getDirectory().getParent(),
                               file);
        myfile.createNewFile();

        git.add()
                .addFilepattern(file)
                .call();

        git.commit()
                .setMessage("Added " + file)
                .call();
    }
}
