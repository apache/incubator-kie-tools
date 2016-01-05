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

import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.file.FileSystem;
import java.util.HashMap;

import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

public class FileSystemTestingUtils {

    private File path;
    private FileSystem fileSystem;
    private IOService ioService;

    public void setup() throws IOException {
        ioService = new IOServiceDotFileImpl();

        createTempDirectory();
        setupJGitRepository();
    }

    private void createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        this.path = temp;
    }

    private void setupJGitRepository() {
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        final URI newRepo = URI.create( "git://amend-repo-test" );

        fileSystem = ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
        Path init = ioService.get( URI.create( "git://amend-repo-test/init.file" ) );
        ioService.write( init, "setupFS!" );
    }

    public void cleanup() {
        FileUtils.deleteQuietly( path );
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider( URI.create( "git://whatever" ) );
        gitFsProvider.shutdown();
        FileUtils.deleteQuietly( gitFsProvider.getGitRepoContainerDir() );
        gitFsProvider.rescanForExistingRepositories();
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public IOService getIoService() {
        return ioService;
    }
}
