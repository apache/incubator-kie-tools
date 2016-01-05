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
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.Path;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JGitFileSystemTest extends AbstractTestInfra {

    static {
        CredentialsProvider.setDefault( new UsernamePasswordCredentialsProvider( "guest", "" ) );
    }

    @Test
    public void testOnlyLocalRoot() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();
        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.isReadOnly() ).isFalse();
        assertThat( fileSystem.getSeparator() ).isEqualTo( "/" );
        assertThat( fileSystem.getName() ).isEqualTo( "my-repo" );

        assertThat( fileSystem.getRootDirectories() ).hasSize( 1 );
        final Path root = fileSystem.getRootDirectories().iterator().next();
        assertThat( root.toString() ).isEqualTo( "/" );

        assertThat( root.getRoot().toString() ).isEqualTo( "/" );
    }

    @Test
    public void testRemoteRoot() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final File tempDir = createTempDirectory();
        final Git git = Git.cloneRepository().setNoCheckout( false ).setBare( true ).setCloneAllBranches( true ).setURI( setupGit().getRepository().getDirectory().toString() ).setDirectory( tempDir ).call();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.isReadOnly() ).isFalse();
        assertThat( fileSystem.getSeparator() ).isEqualTo( "/" );
        assertThat( fileSystem.getName() ).isEqualTo( "my-repo" );

        assertThat( fileSystem.getRootDirectories() ).hasSize( 1 );
        final Path root = fileSystem.getRootDirectories().iterator().next();
        assertThat( root.toString() ).isEqualTo( "/" );

        assertThat( root.getRoot().toString() ).isEqualTo( "/" );
    }

    @Test
    public void testProvider() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.getName() ).isEqualTo( "my-repo" );
        assertThat( fileSystem.isReadOnly() ).isFalse();
        assertThat( fileSystem.getSeparator() ).isEqualTo( "/" );

        assertThat( fileSystem.provider() ).isEqualTo( fsProvider );
    }

    @Test(expected = IllegalStateException.class)
    public void testClose() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.isReadOnly() ).isFalse();
        assertThat( fileSystem.getSeparator() ).isEqualTo( "/" );
        assertThat( fileSystem.getName() ).isEqualTo( "my-repo" );

        assertThat( fileSystem.isOpen() ).isTrue();
        assertThat( fileSystem.getFileStores() ).isNotNull();
        fileSystem.close();
        assertThat( fileSystem.isOpen() ).isFalse();
        assertThat( fileSystem.getFileStores() ).isNotNull();
    }

    @Test
    public void testSupportedFileAttributeViews() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.isReadOnly() ).isFalse();
        assertThat( fileSystem.getSeparator() ).isEqualTo( "/" );
        assertThat( fileSystem.getName() ).isEqualTo( "my-repo" );

        assertThat( fileSystem.supportedFileAttributeViews() ).isNotEmpty().hasSize( 2 ).contains( "basic", "version" );
    }

    @Test
    public void testPathNonBranchRooted() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "/path/to/some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://master@my-repo/path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "/" );
    }

    @Test
    public void testPathNonBranchNonRooted() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "path/to/some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isFalse();
        assertThat( path.toString() ).isEqualTo( "path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://master@my-repo/:path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "" );
    }

    @Test
    public void testPathBranchRooted() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "test-branch", "/path/to/some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://test-branch@my-repo/path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "/" );
    }

    @Test
    public void testPathBranchNonRooted() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "test-branch", "path/to/some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isFalse();
        assertThat( path.toString() ).isEqualTo( "path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://test-branch@my-repo/:path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "" );
    }

    @Test
    public void testPathBranchRooted2() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "test-branch", "/path/to", "some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://test-branch@my-repo/path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "/" );
    }

    @Test
    public void testPathBranchNonRooted2() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );
        when( fsProvider.isDefault() ).thenReturn( false );
        when( fsProvider.getScheme() ).thenReturn( "git" );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        final Path path = fileSystem.getPath( "test-branch", "path/to", "some/place.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isFalse();
        assertThat( path.toString() ).isEqualTo( "path/to/some/place.txt" );
        assertThat( path.toUri().toString() ).isEqualTo( "git://test-branch@my-repo/:path/to/some/place.txt" );

        assertThat( path.getNameCount() ).isEqualTo( 4 );

        assertThat( path.getName( 0 ).toString() ).isNotNull().isEqualTo( "path" );
        assertThat( path.getRoot().toString() ).isNotNull().isEqualTo( "" );
    }

    @Test
    public void testFileStore() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final File tempDir = createTempDirectory();
        final Git git = setupGit( tempDir );

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );

        assertThat( fileSystem.getFileStores() ).hasSize( 1 );
        final FileStore fileStore = fileSystem.getFileStores().iterator().next();
        assertThat( fileStore ).isNotNull();

        assertThat( fileStore.getTotalSpace() ).isEqualTo( tempDir.getTotalSpace() );
        assertThat( fileStore.getUsableSpace() ).isEqualTo( tempDir.getUsableSpace() );
    }

    @Test
    public void testPathEqualsWithDifferentRepos() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git1 = setupGit();

        final JGitFileSystem fileSystem1 = new JGitFileSystem( fsProvider, null, git1, "my-repo1", CredentialsProvider.getDefault() );

        final Git git2 = setupGit();

        final JGitFileSystem fileSystem2 = new JGitFileSystem( fsProvider, null, git2, "my-repo2", CredentialsProvider.getDefault() );

        final Path path1 = fileSystem1.getPath( "master", "/path/to/some.txt" );
        final Path path2 = fileSystem2.getPath( "master", "/path/to/some.txt" );

        assertThat( path1 ).isNotEqualTo( path2 );

        assertThat( path1 ).isEqualTo( fileSystem1.getPath( "/path/to/some.txt" ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    @Ignore
    public void testNewWatchService() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );
        fileSystem.newWatchService();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetUserPrincipalLookupService() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );
        fileSystem.getUserPrincipalLookupService();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPathMatcher() throws IOException, GitAPIException {
        final JGitFileSystemProvider fsProvider = mock( JGitFileSystemProvider.class );

        final Git git = setupGit();

        final JGitFileSystem fileSystem = new JGitFileSystem( fsProvider, null, git, "my-repo", CredentialsProvider.getDefault() );
        fileSystem.getPathMatcher( "*" );
    }
}
