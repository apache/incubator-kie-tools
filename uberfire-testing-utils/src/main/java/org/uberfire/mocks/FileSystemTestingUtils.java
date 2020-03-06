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

package org.uberfire.mocks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.manager.JGitFileSystemsCache;

public class FileSystemTestingUtils {

    private File path;
    private FileSystem fileSystem;
    private IOService ioService;

    public void setup() throws IOException {
        setup(true);
    }

    public void setup(boolean initRepo) throws IOException {
        setup(initRepo, "git://amend-repo-test");
    }

    public void setup(String repoPath) throws IOException {
        setup(true, repoPath);
    }

    public void setup(boolean initRepo, String repoPath) throws IOException {
        ioService = new IOServiceDotFileImpl();

        createTempDirectory();
        setupJGitRepository(repoPath, initRepo);
    }

    private void createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        this.path = temp;
    }

    public FileSystem setupJGitRepository(String repoPath,
                                          boolean initRepo) {
        System.setProperty("org.uberfire.nio.git.dir",
                           path.getAbsolutePath());
        final URI newRepo = URI.create(repoPath);

        try {

            fileSystem = ioService.newFileSystem(newRepo,
                                                 new HashMap<String, Object>());
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(newRepo);
        }
        if (initRepo) {

            Path init = ioService.get(URI.create(repoPath + "/init.file"));
            ioService.write(init,
                            "setupFS!");
        }
        return fileSystem;
    }

    public void setProviderAsDefault() {
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        gitFsProvider.forceAsDefault();
    }

    public void cleanup() {
        FileUtils.deleteQuietly(path);
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        gitFsProvider.shutdown();
        FileUtils.deleteQuietly(gitFsProvider.getGitRepoContainerDir());
    }

    public void shutDownProvider() {
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        gitFsProvider.shutdown();
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public IOService getIoService() {
        return ioService;
    }

    public JGitFileSystemProvider getProvider() {
        return (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
    }

    public JGitFileSystemsCache.JGitFileSystemsCacheInfo getFSCacheInfo() {
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        return gitFsProvider.getFsManager().getFsCache().getCacheInfo();
    }
}
