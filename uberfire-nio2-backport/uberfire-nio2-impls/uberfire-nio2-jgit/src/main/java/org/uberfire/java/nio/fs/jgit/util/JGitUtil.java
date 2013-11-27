/*
 * Copyright 2013 JBoss Inc
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileTimeImpl;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import static java.util.Collections.*;
import static org.apache.commons.io.FileUtils.*;
import static org.eclipse.jgit.lib.Constants.*;
import static org.eclipse.jgit.lib.FileMode.*;
import static org.eclipse.jgit.treewalk.filter.PathFilterGroup.*;
import static org.eclipse.jgit.util.FS.*;
import static org.uberfire.commons.data.Pair.*;
import static org.uberfire.commons.validation.Preconditions.*;

public final class JGitUtil {

    private JGitUtil() {
    }

    public static Git newRepository( final File repoFolder,
                                     final boolean bare ) throws IOException {
        checkNotNull( "repoFolder", repoFolder );

        try {
            return Git.init().setBare( bare ).setDirectory( repoFolder ).call();
        } catch ( GitAPIException e ) {
            throw new IOException( e );
        }
    }

    public static List<Ref> branchList( final Git git ) {
        checkNotNull( "git", git );
        return branchList( git, null );
    }

    public static List<Ref> branchList( final Git git,
                                        final ListBranchCommand.ListMode listMode ) {
        checkNotNull( "git", git );
        try {
            return git.branchList().setListMode( listMode ).call();
        } catch ( GitAPIException e ) {
            throw new RuntimeException( e );
        }
    }

    public static InputStream resolveInputStream( final Git git,
                                                  final String treeRef,
                                                  final String path ) {
        checkNotNull( "git", git );
        checkNotEmpty( "treeRef", treeRef );
        checkNotEmpty( "path", path );

        final String gitPath = fixPath( path );

        RevWalk rw = null;
        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve( treeRef + "^{tree}" );
            rw = new RevWalk( git.getRepository() );
            tw = new TreeWalk( git.getRepository() );
            tw.setFilter( createFromStrings( singleton( gitPath ) ) );
            tw.reset( tree );
            while ( tw.next() ) {
                if ( tw.isSubtree() && !gitPath.equals( tw.getPathString() ) ) {
                    tw.enterSubtree();
                    continue;
                }
                final ObjectId entid = tw.getObjectId( 0 );
                final FileMode entmode = tw.getFileMode( 0 );
                final RevObject ro = rw.lookupAny( entid, entmode.getObjectType() );
                rw.parseBody( ro );
                final ObjectLoader ldr = git.getRepository().open( ro.getId(), Constants.OBJ_BLOB );
                return ldr.openStream();
            }
        } catch ( final Throwable t ) {
            throw new NoSuchFileException( "Can't find '" + gitPath + "' in tree '" + treeRef + "'" );
        } finally {
            if ( rw != null ) {
                rw.dispose();
            }
            if ( tw != null ) {
                tw.release();
            }
        }
        throw new NoSuchFileException( "" );
    }

    private static String fixPath( final String path ) {

        if ( path.equals( "/" ) ) {
            return "";
        }

        boolean startsWith = path.startsWith( "/" );
        boolean endsWith = path.endsWith( "/" );
        if ( startsWith && endsWith ) {
            return path.substring( 1, path.length() - 1 );
        }
        if ( startsWith ) {
            return path.substring( 1 );
        }
        if ( endsWith ) {
            return path.substring( 0, path.length() - 1 );
        }
        return path;
    }

    public static Git cloneRepository( final File repoFolder,
                                       final String fromURI,
                                       final boolean bare,
                                       final CredentialsProvider credentialsProvider ) {

        if ( !repoFolder.getName().endsWith( DOT_GIT_EXT ) ) {
            throw new RuntimeException( "Invalid name" );
        }

        try {
            final File gitDir = RepositoryCache.FileKey.resolve( repoFolder, DETECTED );
            final Repository repository;
            final Git git;
            if ( gitDir != null && gitDir.exists() ) {
                repository = FileRepositoryBuilder.create( gitDir );
                git = new Git( repository );
            } else {
                git = Git.cloneRepository()
                        .setBare( bare )
                        .setCloneAllBranches( true )
                        .setURI( fromURI )
                        .setDirectory( repoFolder )
                        .setCredentialsProvider( credentialsProvider )
                        .call();
                repository = git.getRepository();
            }

            fetchRepository( git, credentialsProvider );

            repository.close();

            return git;
        } catch ( final Exception ex ) {
            try {
                forceDelete( repoFolder );
            } catch ( final java.io.IOException e ) {
                throw new RuntimeException( e );
            }
            throw new RuntimeException( ex );
        }
    }

    public static void fetchRepository( final Git git,
                                        final CredentialsProvider credentialsProvider,
                                        final RefSpec... refSpecs )
            throws InvalidRemoteException {
        final List<RefSpec> specs = new ArrayList<RefSpec>();
        if ( refSpecs == null || refSpecs.length == 0 ) {
            specs.add( new RefSpec( "+refs/heads/*:refs/remotes/origin/*" ) );
            specs.add( new RefSpec( "+refs/tags/*:refs/tags/*" ) );
            specs.add( new RefSpec( "+refs/notes/*:refs/notes/*" ) );
        } else {
            specs.addAll( Arrays.asList( refSpecs ) );
        }

        try {
            git.fetch()
                    .setCredentialsProvider( credentialsProvider )
                    .setRefSpecs( specs )
                    .call();

        } catch ( final InvalidRemoteException e ) {
            throw e;
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public static void syncRepository( final Git git,
                                       final CredentialsProvider credentialsProvider,
                                       final String origin,
                                       boolean force )
            throws InvalidRemoteException {

        if ( origin == null || origin.isEmpty() ) {
            fetchRepository( git, credentialsProvider );
        } else {
            try {
                final StoredConfig config = git.getRepository().getConfig();
                config.setString( "remote", "upstream", "url", origin );
                config.save();
            } catch ( final Exception ex ) {
                throw new RuntimeException( ex );
            }

            final List<RefSpec> specs = new ArrayList<RefSpec>();
            specs.add( new RefSpec( "+refs/heads/*:refs/remotes/upstream/*" ) );
            specs.add( new RefSpec( "+refs/tags/*:refs/tags/*" ) );
            specs.add( new RefSpec( "+refs/notes/*:refs/notes/*" ) );

            try {
                git.fetch()
                        .setCredentialsProvider( credentialsProvider )
                        .setRefSpecs( specs )
                        .setRemote( origin )
                        .call();

                git.branchCreate()
                        .setName( "master" )
                        .setUpstreamMode( CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM )
                        .setStartPoint( "upstream/master" )
                        .setForce( true )
                        .call();

            } catch ( final InvalidRemoteException e ) {
                throw e;
            } catch ( final Exception ex ) {
                throw new RuntimeException( ex );
            }
        }
    }

    public static void pushRepository( final Git git,
                                       final CredentialsProvider credentialsProvider,
                                       final String origin,
                                       boolean force )
            throws InvalidRemoteException {

        if ( origin != null && !origin.isEmpty() ) {

            try {
                final StoredConfig config = git.getRepository().getConfig();
                config.setString( "remote", "upstream", "url", origin );
                config.save();
            } catch ( final Exception ex ) {
                throw new RuntimeException( ex );
            }

            final List<RefSpec> specs = new ArrayList<RefSpec>();
            specs.add( new RefSpec( "+refs/heads/*:refs/remotes/upstream/*" ) );
            specs.add( new RefSpec( "+refs/tags/*:refs/tags/*" ) );
            specs.add( new RefSpec( "+refs/notes/*:refs/notes/*" ) );

            try {
                git.push()
                        .setCredentialsProvider( credentialsProvider )
                        .setRefSpecs( specs )
                        .setRemote( origin )
                        .setForce( force )
                        .setPushAll()
                        .call();

            } catch ( final InvalidRemoteException e ) {
                throw e;
            } catch ( final Exception ex ) {
                throw new RuntimeException( ex );
            }
        }
    }

    public static ObjectId getTreeRefObjectId( final Repository repo,
                                               final String treeRef ) {
        try {
            return repo.resolve( treeRef + "^{tree}" );
        } catch ( java.io.IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public static List<DiffEntry> getDiff( final Repository repo,
                                           final ObjectId oldRef,
                                           final ObjectId newRef ) {
        if ( oldRef == null || newRef == null || repo == null ) {
            return emptyList();
        }

        try {
            ObjectReader reader = repo.newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset( reader, oldRef );
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset( reader, newRef );
            return new Git( repo ).diff().setNewTree( newTreeIter ).setOldTree( oldTreeIter ).setShowNameAndStatusOnly( true ).call();
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public static void commit( final Git git,
                               final String branchName,
                               final String name,
                               final String email,
                               final String message,
                               final TimeZone timeZone,
                               final Date when,
                               final boolean amend,
                               final Map<String, File> content ) {

        final PersonIdent author = buildPersonIdent( git, name, email, timeZone, when );

        try {
            final ObjectInserter odi = git.getRepository().newObjectInserter();
            try {
                // Create the in-memory index of the new/updated issue.
                final ObjectId headId = git.getRepository().resolve( branchName + "^{commit}" );
                final DirCache index = createTemporaryIndex( git, headId, content );
                if ( index != null ) {
                    final ObjectId indexTreeId = index.writeTree( odi );

                    // Create a commit object
                    final CommitBuilder commit = new CommitBuilder();
                    commit.setAuthor( author );
                    commit.setCommitter( author );
                    commit.setEncoding( Constants.CHARACTER_ENCODING );
                    commit.setMessage( message );
                    //headId can be null if the repository has no commit yet
                    if ( headId != null ) {
                        if ( amend ) {
                            final List<ObjectId> parents = new LinkedList<ObjectId>();
                            final RevCommit previousCommit = new RevWalk( git.getRepository() ).parseCommit( headId );
                            final RevCommit[] p = previousCommit.getParents();
                            for ( final RevCommit revCommit : p ) {
                                parents.add( 0, revCommit.getId() );
                            }
                            commit.setParentIds( parents );
                        } else {
                            commit.setParentId( headId );
                        }
                    }
                    commit.setTreeId( indexTreeId );

                    // Insert the commit into the repository
                    final ObjectId commitId = odi.insert( commit );
                    odi.flush();

                    final RevWalk revWalk = new RevWalk( git.getRepository() );
                    try {
                        final RevCommit revCommit = revWalk.parseCommit( commitId );
                        final RefUpdate ru = git.getRepository().updateRef( "refs/heads/" + branchName );
                        if ( headId == null ) {
                            ru.setExpectedOldObjectId( ObjectId.zeroId() );
                        } else {
                            ru.setExpectedOldObjectId( headId );
                        }
                        ru.setNewObjectId( commitId );
                        ru.setRefLogMessage( "commit: " + revCommit.getShortMessage(), false );
                        final RefUpdate.Result rc = ru.forceUpdate();
                        switch ( rc ) {
                            case NEW:
                            case FORCED:
                            case FAST_FORWARD:
                                break;
                            case REJECTED:
                            case LOCK_FAILURE:
                                throw new ConcurrentRefUpdateException( JGitText.get().couldNotLockHEAD, ru.getRef(), rc );
                            default:
                                throw new JGitInternalException( MessageFormat.format( JGitText.get().updatingRefFailed, Constants.HEAD, commitId.toString(), rc ) );
                        }

                    } finally {
                        revWalk.release();
                    }
                } else {
                    //empty commit
                }
            } finally {
                odi.release();
            }
        } catch ( final Throwable t ) {
            throw new RuntimeException( t );
        }
    }

    private static PersonIdent buildPersonIdent( final Git git,
                                                 final String name,
                                                 final String _email,
                                                 final TimeZone timeZone,
                                                 final Date when ) {
        final TimeZone tz = timeZone == null ? TimeZone.getDefault() : timeZone;
        final String email = _email == null ? "" : _email;

        if ( name != null ) {
            if ( when != null ) {
                return new PersonIdent( name, email, when, tz );
            } else {
                return new PersonIdent( name, email );
            }
        }
        return new PersonIdent( git.getRepository() );
    }

    /**
     * Creates an in-memory index of the issue change.
     */
    private static DirCache createTemporaryIndex( final Git git,
                                                  final ObjectId headId,
                                                  final Map<String, File> content ) {

        final Map<String, Pair<File, ObjectId>> paths = new HashMap<String, Pair<File, ObjectId>>( content.size() );
        final Set<String> path2delete = new HashSet<String>();

        final DirCache inCoreIndex = DirCache.newInCore();
        final ObjectInserter inserter = git.getRepository().newObjectInserter();
        final DirCacheEditor editor = inCoreIndex.editor();

        try {
            for ( final Map.Entry<String, File> pathAndContent : content.entrySet() ) {
                final String gPath = fixPath( pathAndContent.getKey() );
                if ( pathAndContent.getValue() == null ) {
                    final TreeWalk treeWalk = new TreeWalk( git.getRepository() );
                    treeWalk.addTree( new RevWalk( git.getRepository() ).parseTree( headId ) );
                    treeWalk.setRecursive( true );
                    treeWalk.setFilter( PathFilter.create( gPath ) );

                    while ( treeWalk.next() ) {
                        path2delete.add( treeWalk.getPathString() );
                    }
                    treeWalk.release();
                } else {
                    try {
                        final InputStream inputStream = new FileInputStream( pathAndContent.getValue() );
                        try {
                            final ObjectId objectId = inserter.insert( Constants.OBJ_BLOB, pathAndContent.getValue().length(), inputStream );
                            paths.put( gPath, Pair.newPair( pathAndContent.getValue(), objectId ) );
                        } finally {
                            inputStream.close();
                        }
                    } catch ( final Exception ex ) {
                        throw new RuntimeException( ex );
                    }
                }
            }

            if ( headId != null ) {
                final TreeWalk treeWalk = new TreeWalk( git.getRepository() );
                final int hIdx = treeWalk.addTree( new RevWalk( git.getRepository() ).parseTree( headId ) );
                treeWalk.setRecursive( true );

                while ( treeWalk.next() ) {
                    final String walkPath = treeWalk.getPathString();
                    final CanonicalTreeParser hTree = treeWalk.getTree( hIdx, CanonicalTreeParser.class );

                    if ( paths.containsKey( walkPath ) && paths.get( walkPath ).getK2().equals( hTree.getEntryObjectId() ) ) {
                        paths.remove( walkPath );
                    }

                    if ( paths.get( walkPath ) == null && !path2delete.contains( walkPath ) ) {
                        final DirCacheEntry dcEntry = new DirCacheEntry( walkPath );
                        final ObjectId _objectId = hTree.getEntryObjectId();
                        final FileMode _fileMode = hTree.getEntryFileMode();

                        // add to temporary in-core index
                        editor.add( new DirCacheEditor.PathEdit( dcEntry ) {
                            @Override
                            public void apply( final DirCacheEntry ent ) {
                                ent.setObjectId( _objectId );
                                ent.setFileMode( _fileMode );
                            }
                        } );
                    }
                }
                treeWalk.release();
            }

            for ( final Map.Entry<String, Pair<File, ObjectId>> pathAndContent : paths.entrySet() ) {
                if ( pathAndContent.getValue().getK1() != null ) {
                    editor.add( new DirCacheEditor.PathEdit( new DirCacheEntry( pathAndContent.getKey() ) ) {
                        @Override
                        public void apply( final DirCacheEntry ent ) {
                            ent.setLength( pathAndContent.getValue().getK1().length() );
                            ent.setLastModified( pathAndContent.getValue().getK1().lastModified() );
                            ent.setFileMode( REGULAR_FILE );
                            ent.setObjectId( pathAndContent.getValue().getK2() );
                        }
                    } );
                }
            }

            editor.finish();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        } finally {
            inserter.release();
        }

        if ( path2delete.isEmpty() && paths.isEmpty() ) {
            //no changes!
            return null;
        }

        return inCoreIndex;
    }

    public static ObjectId resolveObjectId( final Git git,
                                            final String name ) {

        try {
            final Ref refName = getBranch( git, name );
            if ( refName != null ) {
                return refName.getObjectId();
            }

            try {
                final ObjectId id = ObjectId.fromString( name );
                if ( git.getRepository().getObjectDatabase().has( id ) ) {
                    return id;
                }
            } catch ( final IllegalArgumentException ex ) {
            }

            return null;
        } catch ( java.io.IOException e ) {
        }

        return null;
    }

    public static Ref getBranch( final Git git,
                                 final String name ) {

        try {
            return git.getRepository().getRefDatabase().getRef( name );
        } catch ( java.io.IOException e ) {
        }

        return null;
    }

    public static void deleteBranch( final Git git,
                                     final Ref branch ) {
        try {
            git.branchDelete().setBranchNames( branch.getName() ).setForce( true ).call();
        } catch ( final GitAPIException e ) {
            throw new IOException( e );
        }
    }

    public static VersionAttributes buildVersionAttributes( final JGitFileSystem fs,
                                                            final String branchName,
                                                            final String path ) {
        final JGitPathInfo pathInfo = resolvePath( fs.gitRepo(), branchName, path );

        if ( pathInfo == null ) {
            throw new NoSuchFileException( path );
        }

        final String gPath = fixPath( path );

        final ObjectId id = resolveObjectId( fs.gitRepo(), branchName );

        final List<VersionRecord> records = new ArrayList<VersionRecord>();

        if ( id != null ) {
            try {
                final LogCommand logCommand = fs.gitRepo().log().add( id );
                if ( !gPath.isEmpty() ) {
                    logCommand.addPath( gPath );
                }

                for ( final RevCommit commit : logCommand.call() ) {

                    records.add( new VersionRecord() {
                        @Override
                        public String id() {
                            return commit.name();
                        }

                        @Override
                        public String author() {
                            return commit.getCommitterIdent().getName();
                        }

                        @Override
                        public String email() {
                            return commit.getCommitterIdent().getEmailAddress();
                        }

                        @Override
                        public String comment() {
                            return commit.getFullMessage();
                        }

                        @Override
                        public Date date() {
                            return commit.getCommitterIdent().getWhen();
                        }

                        @Override
                        public String uri() {
                            return fs.getPath( commit.name(), path ).toUri().toString();
                        }
                    } );
                }
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        Collections.sort( records, new Comparator<VersionRecord>() {
            @Override
            public int compare( final VersionRecord o1,
                                final VersionRecord o2 ) {
                return o1.date().compareTo( o2.date() );
            }
        } );

        return new VersionAttributes() {
            @Override
            public VersionHistory history() {
                return new VersionHistory() {
                    @Override
                    public List<VersionRecord> records() {
                        return records;
                    }
                };
            }

            @Override
            public FileTime lastModifiedTime() {
                if ( records.size() > 0 ) {
                    return new FileTimeImpl( records.get( records.size() - 1 ).date().getTime() );
                }
                return null;
            }

            @Override
            public FileTime lastAccessTime() {
                return null;
            }

            @Override
            public FileTime creationTime() {
                if ( records.size() > 0 ) {
                    return new FileTimeImpl( records.get( 0 ).date().getTime() );
                }
                return null;
            }

            @Override
            public boolean isRegularFile() {
                return pathInfo.getPathType().equals( PathType.FILE );
            }

            @Override
            public boolean isDirectory() {
                return pathInfo.getPathType().equals( PathType.DIRECTORY );
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return pathInfo.getSize();
            }

            @Override
            public Object fileKey() {
                return pathInfo.getObjectId() == null ? null : pathInfo.getObjectId().toString();
            }
        };
    }

    public static BasicFileAttributes buildBasicAttributes( final JGitFileSystem fs,
                                                            final String branchName,
                                                            final String path ) {
        final JGitPathInfo pathInfo = resolvePath( fs.gitRepo(), branchName, path );

        if ( pathInfo == null ) {
            throw new NoSuchFileException( path );
        }

        final ObjectId id = resolveObjectId( fs.gitRepo(), branchName );
        final String gPath = fixPath( path );

        return new BasicFileAttributes() {

            private long lastModifiedDate = -1;
            private long creationDate = -1;

            @Override
            public FileTime lastModifiedTime() {
                if ( lastModifiedDate == -1L ) {
                    RevWalk revWalk = null;
                    try {
                        final LogCommand logCommand = fs.gitRepo().log().add( id ).setMaxCount( 1 );
                        if ( !gPath.isEmpty() ) {
                            logCommand.addPath( gPath );
                        }
                        revWalk = (RevWalk) logCommand.call();
                        lastModifiedDate = revWalk.iterator().next().getCommitterIdent().getWhen().getTime();
                    } catch ( Exception ex ) {
                        lastModifiedDate = 0;
                    } finally {
                        if ( revWalk != null ) {
                            revWalk.dispose();
                        }
                    }
                }
                return new FileTimeImpl( lastModifiedDate );
            }

            @Override
            public FileTime lastAccessTime() {
                return null;
            }

            @Override
            public FileTime creationTime() {
                if ( creationDate == -1L ) {
                    RevWalk revWalk = null;
                    try {
                        final LogCommand logCommand = fs.gitRepo().log().add( id ).setMaxCount( 1 );
                        if ( !gPath.isEmpty() ) {
                            logCommand.addPath( gPath );
                        }
                        revWalk = (RevWalk) logCommand.call();
                        creationDate = revWalk.iterator().next().getCommitterIdent().getWhen().getTime();
                    } catch ( Exception ex ) {
                        creationDate = 0;
                    } finally {
                        if ( revWalk != null ) {
                            revWalk.dispose();
                        }
                    }
                }
                return new FileTimeImpl( creationDate );
            }

            @Override
            public boolean isRegularFile() {
                return pathInfo.getPathType().equals( PathType.FILE );
            }

            @Override
            public boolean isDirectory() {
                return pathInfo.getPathType().equals( PathType.DIRECTORY );
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return pathInfo.getSize();
            }

            @Override
            public Object fileKey() {
                return pathInfo.getObjectId() == null ? null : pathInfo.getObjectId().toString();
            }
        };
    }

    public static void createBranch( final Git git,
                                     final String source,
                                     final String target ) {
        try {
            git.branchCreate().setName( target ).setStartPoint( source ).call();
        } catch ( GitAPIException e ) {
            throw new RuntimeException( e );
        }
    }

    public static boolean hasBranch( final Git git,
                                     final String branchName ) {
        checkNotNull( "git", git );
        checkNotEmpty( "branchName", branchName );

        return getBranch( git, branchName ) != null;
    }

    public static enum PathType {
        NOT_FOUND, DIRECTORY, FILE
    }

    public static Pair<PathType, ObjectId> checkPath( final Git git,
                                                      final String branchName,
                                                      final String path ) {
        checkNotNull( "git", git );
        checkNotNull( "path", path );
        checkNotEmpty( "branchName", branchName );

        final String gitPath = fixPath( path );

        if ( gitPath.isEmpty() ) {
            return newPair( PathType.DIRECTORY, null );
        }

        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve( branchName + "^{tree}" );
            tw = new TreeWalk( git.getRepository() );
            tw.setFilter( PathFilter.create( gitPath ) );
            tw.reset( tree );
            while ( tw.next() ) {
                if ( tw.getPathString().equals( gitPath ) ) {
                    if ( tw.getFileMode( 0 ).equals( FileMode.TYPE_TREE ) ) {
                        return newPair( PathType.DIRECTORY, tw.getObjectId( 0 ) );
                    } else if ( tw.getFileMode( 0 ).equals( FileMode.TYPE_FILE ) ||
                            tw.getFileMode( 0 ).equals( FileMode.EXECUTABLE_FILE ) ||
                            tw.getFileMode( 0 ).equals( FileMode.REGULAR_FILE ) ) {
                        return newPair( PathType.FILE, tw.getObjectId( 0 ) );
                    }
                }
                if ( tw.isSubtree() ) {
                    tw.enterSubtree();
                }
            }
        } catch ( final Throwable ignored ) {
        } finally {
            if ( tw != null ) {
                tw.release();
            }
        }
        return newPair( PathType.NOT_FOUND, null );
    }

    public static JGitPathInfo resolvePath( final Git git,
                                            final String branchName,
                                            final String path ) {
        checkNotNull( "git", git );
        checkNotNull( "path", path );
        checkNotEmpty( "branchName", branchName );

        final String gitPath = fixPath( path );

        if ( gitPath.isEmpty() ) {
            return new JGitPathInfo( null, "/", TREE );
        }

        TreeWalk tw = null;
        try {
            final ObjectId tree = git.getRepository().resolve( branchName + "^{tree}" );
            tw = new TreeWalk( git.getRepository() );
            tw.setFilter( PathFilter.create( gitPath ) );
            tw.reset( tree );
            while ( tw.next() ) {
                if ( tw.getPathString().equals( gitPath ) ) {
                    if ( tw.getFileMode( 0 ).equals( TREE ) ) {
                        return new JGitPathInfo( tw.getObjectId( 0 ), tw.getPathString(), TREE );
                    } else if ( tw.getFileMode( 0 ).equals( REGULAR_FILE ) || tw.getFileMode( 0 ).equals( EXECUTABLE_FILE ) ) {
                        final long size = tw.getObjectReader().getObjectSize( tw.getObjectId( 0 ), OBJ_BLOB );
                        return new JGitPathInfo( tw.getObjectId( 0 ), tw.getPathString(), REGULAR_FILE, size );
                    }
                }
                if ( tw.isSubtree() ) {
                    tw.enterSubtree();
                }
            }
        } catch ( final Throwable ignored ) {
        } finally {
            if ( tw != null ) {
                tw.release();
            }
        }

        return null;
    }

    public static List<JGitPathInfo> listPathContent( final Git git,
                                                      final String branchName,
                                                      final String path ) {
        checkNotNull( "git", git );
        checkNotNull( "path", path );
        checkNotEmpty( "branchName", branchName );

        final String gitPath = fixPath( path );

        TreeWalk tw = null;
        final List<JGitPathInfo> result = new ArrayList<JGitPathInfo>();
        try {
            final ObjectId tree = git.getRepository().resolve( branchName + "^{tree}" );
            tw = new TreeWalk( git.getRepository() );
            boolean found = false;
            if ( gitPath.isEmpty() ) {
                found = true;
            } else {
                tw.setFilter( PathFilter.create( gitPath ) );
            }
            tw.reset( tree );
            while ( tw.next() ) {
                if ( !found && tw.isSubtree() ) {
                    tw.enterSubtree();
                }
                if ( tw.getPathString().equals( gitPath ) ) {
                    found = true;
                    continue;
                }
                if ( found ) {
                    result.add( new JGitPathInfo( tw.getObjectId( 0 ), tw.getPathString(), tw.getFileMode( 0 ) ) );
                }
            }
        } catch ( final Throwable ignored ) {
        } finally {
            if ( tw != null ) {
                tw.release();
            }
        }

        return result;
    }

    public static class JGitPathInfo {

        private final ObjectId objectId;
        private final String path;
        private final long size;
        private final PathType pathType;

        public JGitPathInfo( final ObjectId objectId,
                             final String path,
                             final FileMode fileMode ) {
            this( objectId, path, fileMode, -1 );
        }

        public JGitPathInfo( final ObjectId objectId,
                             final String path,
                             final FileMode fileMode,
                             long size ) {
            this.objectId = objectId;
            this.size = size;
            this.path = path;

            if ( fileMode.equals( FileMode.TYPE_TREE ) ) {
                this.pathType = PathType.DIRECTORY;
            } else if ( fileMode.equals( TYPE_FILE ) ) {
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
