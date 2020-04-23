/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FS_POSIX;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.jgit.util.ProcessResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterParameters;
import org.uberfire.commons.cluster.ConnectionMode;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.ListRefs;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.DefaultCommitContent;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractTestInfra {

    static class TestFile {

        final String path;
        final String content;

        TestFile(final String path,
                 final String content) {
            this.path = path;
            this.content = content;
        }
    }

    private static final String PROTOCOL_SEPARATOR = "://";

    private static final Logger logger = LoggerFactory.getLogger(AbstractTestInfra.class);

    protected static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    protected static final List<File> tempFiles = new ArrayList<>();

    protected JGitFileSystemProvider provider;

    @Before
    public void createGitFsProvider() {
        provider = new JGitFileSystemProvider(getGitPreferences());
    }

    /*
     * Default Git preferences suitable for most of the tests. If specific test needs some custom configuration, it needs to
     * override this method and provide own map of preferences.
     */
    public Map<String, String> getGitPreferences() {
        System.setProperty(ClusterParameters.APPFORMER_JMS_CONNECTION_MODE,
                           ConnectionMode.NONE.toString());
        Map<String, String> gitPrefs = new HashMap<>();
        // disable the daemons by default as they not needed in most of the cases
        gitPrefs.put("org.uberfire.nio.git.daemon.enabled",
                     "false");
        gitPrefs.put("org.uberfire.nio.git.ssh.enabled",
                     "false");
        return gitPrefs;
    }

    @After
    public void destroyGitFsProvider() throws IOException {
        if (provider == null) {
            // this would mean that setup failed. no need to clean up.
            return;
        }

        provider.shutdown();

        if (provider.getGitRepoContainerDir() != null && provider.getGitRepoContainerDir().exists()) {
            FileUtils.delete(provider.getGitRepoContainerDir(),
                             FileUtils.RECURSIVE);
        }
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            try {
                FileUtils.delete(tempFile,
                                 FileUtils.RECURSIVE);
            } catch (IOException e) {
            }
        }
    }

    protected Git setupGit() throws IOException, GitAPIException {
        return setupGit(createTempDirectory());
    }

    protected Git setupGit(final File tempDir) throws IOException, GitAPIException {

        final Git git = Git.createRepository(tempDir);

        new Commit(git,
                   "master",
                   new CommitInfo(null,
                                  "name",
                                  "name@example.com",
                                  "cool1",
                                  null,
                                  null),
                   false,
                   null,
                   new DefaultCommitContent(new HashMap<String, File>() {{
                       put("file1.txt",
                           tempFile("content"));
                       put("file2.txt",
                           tempFile("content2"));
                   }})).execute();

        return git;
    }

    protected static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        tempFiles.add(temp);

        return temp;
    }

    public static File tempFile(final String content) throws IOException {
        final File file = File.createTempFile("bar",
                                              "foo");
        final OutputStream out = new FileOutputStream(file);

        if (content != null && !content.isEmpty()) {
            out.write(content.getBytes());
            out.flush();
        }

        out.close();
        return file;
    }

    public File tempFile(final byte[] content) throws IOException {
        final File file = File.createTempFile("bar",
                                              "foo");
        final FileOutputStream out = new FileOutputStream(file);

        if (content != null && content.length > 0) {
            out.write(content);
            out.flush();
        }

        out.close();
        return file;
    }

    public PersonIdent getAuthor() {
        return new PersonIdent("user",
                               "user@example.com");
    }

    public static int findFreePort() {
        int port = 0;
        try {
            ServerSocket server = new ServerSocket(0);
            port = server.getLocalPort();
            server.close();
        } catch (IOException e) {
            Assert.fail("Can't find free port!");
        }
        logger.debug("Found free port " + port);
        return port;
    }

    protected byte[] loadImage(final String path) throws IOException {
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream,
                     writer);
        return writer.toString().getBytes();
    }

    static void commit(final Git origin,
                       final String branchName,
                       final String message,
                       final TestFile... testFiles) throws IOException {
        final Map<String, File> data = Arrays.stream(testFiles)
                .collect(toMap(f -> f.path,
                               f -> tmpFile(f.content)));
        new Commit(origin,
                   branchName,
                   "name",
                   "name@example.com",
                   message,
                   null,
                   null,
                   false,
                   data).execute();
    }

    public static File tmpFile(final String content) {
        try {
            return tempFile(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static TestFile content(final String path,
                            final String content) {
        return new TestFile(path,
                            content);
    }

    /**
     * Creates mock hook in defined hooks directory.
     *
     * @param hooksDirectory Directory in which mock hook is created.
     * @param hookName       Name of the created hook. This is the filename of created hook file.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    void writeMockHook(final File hooksDirectory,
                       final String hookName)
            throws FileNotFoundException, UnsupportedEncodingException {
        final PrintWriter writer = new PrintWriter(new File(hooksDirectory,
                                                            hookName),
                                                   "UTF-8");
        writer.println("# something");
        writer.close();
    }

    /**
     * Tests if defined hook was executed or not.
     *
     * @param gitRepoName    Name of test git repository that is created for committing changes.
     * @param testedHookName Tested hook name. This hook is checked for its execution.
     * @param wasExecuted    Expected hook execution state. If true, test expects that defined hook is executed.
     *                       If false, test expects that defined hook is not executed.
     * @throws IOException
     */
    void testHook(final String gitRepoName,
                  final String testedHookName,
                  final boolean wasExecuted) throws IOException {
        final URI newRepo = URI.create("git://" + gitRepoName);

        final AtomicBoolean hookExecuted = new AtomicBoolean(false);
        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     EMPTY_ENV);

        provider.setDetectedFS(new FS_POSIX() {
            @Override
            public ProcessResult runHookIfPresent(Repository repox,
                                                  String hookName,
                                                  String[] args) throws JGitInternalException {
                if (hookName.equals(testedHookName)) {
                    hookExecuted.set(true);
                }
                return new ProcessResult(ProcessResult.Status.OK);
            }
        });

        assertThat(fs).isNotNull();

        final Path path = provider.getPath(URI.create("git://user_branch@" + gitRepoName + "/some/path/myfile.txt"));

        final OutputStream outStream = provider.newOutputStream(path);
        assertThat(outStream).isNotNull();
        outStream.write("my cool content".getBytes());
        outStream.close();

        final InputStream inStream = provider.newInputStream(path);

        final String content = new Scanner(inStream).useDelimiter("\\A").next();

        inStream.close();

        assertThat(content).isNotNull().isEqualTo("my cool content");

        if (wasExecuted) {
            assertThat(hookExecuted.get()).isTrue();
        } else {
            assertThat(hookExecuted.get()).isFalse();
        }
    }

    protected Ref branch(Git origin, String source, String target) throws Exception {
        final Repository repo = origin.getRepository();
        return org.eclipse.jgit.api.Git.wrap(repo)
                .branchCreate()
                .setName(target)
                .setStartPoint(source)
                .call();
    }

    protected List<Ref> listRefs(final Git cloned) {
        return new ListRefs(cloned.getRepository()).execute();
    }

    protected static String multiline(String prefix, String... lines) {
        return Arrays.stream(lines)
                .map(s -> prefix + s)
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");
    }

    protected static boolean checkProtocolPresent(String hostNames, String protocolName) {
        final String[] uris = hostNames.toString().split("\\r?\\n");
        return Arrays.stream(uris)
                .map(uri -> uri.substring(0, uri.indexOf(PROTOCOL_SEPARATOR)))
                .anyMatch(uri -> Objects.equals(uri, protocolName));
    }
}
