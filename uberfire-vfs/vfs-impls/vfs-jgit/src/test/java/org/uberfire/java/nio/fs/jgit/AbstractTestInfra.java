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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public abstract class AbstractTestInfra {

    protected static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    private static final List<File> tempFiles = new ArrayList<File>();

    protected Git setupGit() throws IOException, GitAPIException {
        return setupGit(createTempDirectory());
    }

    protected Git setupGit(final File tempDir) throws IOException, GitAPIException {

        final Git git = Git.init().setBare(true).setDirectory(tempDir).call();

        commit(git, "master", "file1.txt", tempFile("content"), "name", "name@example.com", "cool1", null, null);
        commit(git, "master", "file2.txt", tempFile("content2"), "name", "name@example.com", "cool1", null, null);

        return git;
    }

    protected static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        tempFiles.add(temp);

        return temp;
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            try {
                FileUtils.delete(tempFile, FileUtils.RECURSIVE);
            } catch (IOException e) {
            }
        }
    }

    @AfterClass
    @BeforeClass
    public static void cleanupGit() {
        try {
            FileUtils.delete(JGitFileSystemProvider.FILE_REPOSITORIES_ROOT, FileUtils.RECURSIVE);
        } catch (Exception ex) {

        }
    }

    public File tempFile(final String content) throws IOException {
        final File file = File.createTempFile("bar", "foo");
        final OutputStream out = new FileOutputStream(file);

        if (content != null && !content.isEmpty()) {
            out.write(content.getBytes());
            out.flush();
        }

        out.close();
        return file;
    }

    public PersonIdent getAuthor() {
        return new PersonIdent("user", "user@example.com");
    }

}
