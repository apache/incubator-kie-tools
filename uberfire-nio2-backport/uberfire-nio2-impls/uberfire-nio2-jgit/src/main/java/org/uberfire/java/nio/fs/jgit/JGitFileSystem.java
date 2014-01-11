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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InterruptedException;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.Watchable;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.eclipse.jgit.lib.Repository.*;
import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystem implements FileSystem,
                                       FileSystemId {

    private static final Logger LOGGER = LoggerFactory.getLogger( JGitFileSystem.class );

    private static final Set<String> SUPPORTED_ATTR_VIEWS = Collections.unmodifiableSet( new HashSet<String>() {{
        add( "basic" );
        add( "version" );
    }} );

    private final JGitFileSystemProvider provider;
    private final Git gitRepo;
    private final ListBranchCommand.ListMode listMode;
    private final String toStringContent;
    private boolean isClose = false;
    private final FileStore fileStore;
    private final String name;
    private final CredentialsProvider credential;
    private final Map<WatchService, Queue<WatchKey>> events = new ConcurrentHashMap<WatchService, Queue<WatchKey>>();
    private final Collection<WatchService> watchServices = new ArrayList<WatchService>();

    JGitFileSystem( final JGitFileSystemProvider provider,
                    final Map<String, String> fullHostNames,
                    final Git git,
                    final String name,
                    final CredentialsProvider credential ) {
        this( provider, fullHostNames, git, name, null, credential );
    }

    JGitFileSystem( final JGitFileSystemProvider provider,
                    final Map<String, String> fullHostNames,
                    final Git git,
                    final String name,
                    final ListBranchCommand.ListMode listMode,
                    final CredentialsProvider credential ) {
        this.provider = checkNotNull( "provider", provider );
        this.gitRepo = checkNotNull( "git", git );
        this.name = checkNotEmpty( "name", name );
        this.credential = checkNotNull( "credential", credential );
        this.listMode = listMode;
        this.fileStore = new JGitFileStore( gitRepo.getRepository() );
        if ( fullHostNames != null && !fullHostNames.isEmpty() ) {
            final StringBuilder sb = new StringBuilder();
            final Iterator<Map.Entry<String, String>> iterator = fullHostNames.entrySet().iterator();
            while ( iterator.hasNext() ) {
                final Map.Entry<String, String> entry = iterator.next();
                sb.append( entry.getKey() ).append( "://" ).append( entry.getValue() ).append( "/" ).append( name );
                if ( iterator.hasNext() ) {
                    sb.append( "\n" );
                }
            }
            toStringContent = sb.toString();
        } else {
            toStringContent = "git://" + name;
        }
    }

    @Override
    public String id() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Git gitRepo() {
        return gitRepo;
    }

    public CredentialsProvider getCredential() {
        return credential;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public boolean isOpen() {
        return !isClose;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        checkClose();
        return new Iterable<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {

                    Iterator<Ref> branches = null;

                    @Override
                    public boolean hasNext() {
                        if ( branches == null ) {
                            init();
                        }
                        return branches.hasNext();
                    }

                    private void init() {
                        branches = branchList( gitRepo, listMode ).iterator();
                    }

                    @Override
                    public Path next() {
                        if ( branches == null ) {
                            init();
                        }
                        return JGitPathImpl.createRoot( JGitFileSystem.this, "/", shortenRefName( branches.next().getName() ) + "@" + name, false );
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        checkClose();
        return new Iterable<FileStore>() {
            @Override
            public Iterator<FileStore> iterator() {
                return new Iterator<FileStore>() {

                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < 1;
                    }

                    @Override
                    public FileStore next() {
                        if ( i < 1 ) {
                            i++;
                            return fileStore;
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        checkClose();
        return SUPPORTED_ATTR_VIEWS;
    }

    @Override
    public Path getPath( final String first,
                         final String... more )
            throws InvalidPathException {
        checkClose();
        if ( first == null || first.trim().isEmpty() ) {
            return new JGitFSPath( this );
        }

        if ( more == null || more.length == 0 ) {
            return JGitPathImpl.create( this, first, JGitPathImpl.DEFAULT_REF_TREE + "@" + name, false );
        }

        final StringBuilder sb = new StringBuilder();
        for ( final String segment : more ) {
            if ( segment.length() > 0 ) {
                if ( sb.length() > 0 ) {
                    sb.append( getSeparator() );
                }
                sb.append( segment );
            }
        }
        return JGitPathImpl.create( this, sb.toString(), first + "@" + name, false );
    }

    @Override
    public PathMatcher getPathMatcher( final String syntaxAndPattern )
            throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        checkClose();
        checkNotEmpty( "syntaxAndPattern", syntaxAndPattern );
        throw new UnsupportedOperationException();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
            throws UnsupportedOperationException {
        checkClose();
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService()
            throws UnsupportedOperationException, IOException {
        checkClose();
        final WatchService ws = new WatchService() {
            private boolean wsClose = false;

            @Override
            public WatchKey poll() throws ClosedWatchServiceException {
                return events.get( this ).poll();
            }

            @Override
            public WatchKey poll( long timeout,
                                  TimeUnit unit ) throws ClosedWatchServiceException, org.uberfire.java.nio.file.InterruptedException {
                return events.get( this ).poll();
            }

            @Override
            public WatchKey take() throws ClosedWatchServiceException, InterruptedException {
                while ( !wsClose && !isClose ) {
                    if ( events.get( this ).size() > 0 ) {
                        return events.get( this ).poll();
                    } else {
                        try {
                            Thread.sleep( 200 );
                        } catch ( java.lang.InterruptedException e ) {
                        }
                    }
                }
                return null;
            }

            @Override
            public boolean isClose() {
                return isClose;
            }

            @Override
            public void close() throws IOException {
                wsClose = true;
                watchServices.remove( this );
            }

            @Override
            public String toString() {
                return "WatchService{" +
                        "FileSystem=" + JGitFileSystem.this.toString() +
                        '}';
            }
        };
        events.put( ws, new ConcurrentLinkedQueue<WatchKey>() );
        watchServices.add( ws );
        return ws;
    }

    @Override
    public void close() throws IOException {
        if ( isClose ) {
            return;
        }
        gitRepo.getRepository().close();
        isClose = true;
        try {

            for ( final WatchService ws : new ArrayList<WatchService>( watchServices ) ) {
                try {
                    ws.close();
                } catch ( final Exception ex ) {
                    LOGGER.error( "Can't close watch service [" + toString() + "]", ex );
                }
            }
            watchServices.clear();
            events.clear();
        } catch ( final Exception ex ) {
            LOGGER.error( "Error during close of WatchServices [" + toString() + "]", ex );
        } finally {
            provider.onCloseFileSystem( this );
        }
    }

    private void checkClose() throws IllegalStateException {
        if ( isClose ) {
            throw new IllegalStateException( "FileSystem is close." );
        }
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        JGitFileSystem that = (JGitFileSystem) o;

        if ( fileStore != null ? !fileStore.equals( that.fileStore ) : that.fileStore != null ) {
            return false;
        }
        if ( !gitRepo.equals( that.gitRepo ) ) {
            return false;
        }
        if ( listMode != that.listMode ) {
            return false;
        }
        if ( !name.equals( that.name ) ) {
            return false;
        }
        if ( !provider.equals( that.provider ) ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return toStringContent;
    }

    @Override
    public int hashCode() {
        int result = provider.hashCode();
        result = 31 * result + gitRepo.hashCode();
        result = 31 * result + ( listMode != null ? listMode.hashCode() : 0 );
        result = 31 * result + ( fileStore != null ? fileStore.hashCode() : 0 );
        result = 31 * result + name.hashCode();
        return result;
    }

    public void publishEvents( final Path watchable,
                               final List<WatchEvent<?>> elist ) {
        if ( this.events.isEmpty() ) {
            return;
        }

        final WatchKey wk = new WatchKey() {

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public List<WatchEvent<?>> pollEvents() {
                return new ArrayList<WatchEvent<?>>( elist );
            }

            @Override
            public boolean reset() {
                return false;
            }

            @Override
            public void cancel() {
            }

            @Override
            public Watchable watchable() {
                return watchable;
            }
        };

        for ( final Queue<WatchKey> queue : events.values() ) {
            queue.add( wk );
        }
    }
}
