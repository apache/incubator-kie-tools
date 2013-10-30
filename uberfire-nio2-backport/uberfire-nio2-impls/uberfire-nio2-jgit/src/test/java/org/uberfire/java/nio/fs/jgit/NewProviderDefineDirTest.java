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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static org.fest.assertions.api.Assertions.*;

public class NewProviderDefineDirTest {

    protected static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    private static final List<File> tempFiles = new ArrayList<File>();

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

    @Test
    @Ignore
    public void testUsingProvidedPath() throws IOException {

        final File dir = createTempDirectory();
        System.setProperty("org.uberfire.nio.git.dir", dir.toString());

        final JGitFileSystemProvider provider = new JGitFileSystemProvider();

        final URI newRepo = URI.create("git://repo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final String[] names = dir.list();

        assertThat(names).isNotEmpty().contains(".niogit");

        final String[] repos = new File(dir, ".niogit").list();

        assertThat(repos).isNotEmpty().contains("repo-name.git");

        System.setProperty("org.uberfire.nio.git.dir", "");
    }

}
