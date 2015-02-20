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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.file.WindowCache;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.commons.config.ConfigProperties.ConfigProperty;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.message.MessageType;
import org.uberfire.java.nio.EncodingUtil;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.ExtendedAttributeView;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.base.SeekableByteChannelFileBasedImpl;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.dotfiles.DotFileOption;
import org.uberfire.java.nio.base.options.CherryPickCopyOption;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.fs.jgit.daemon.git.Daemon;
import org.uberfire.java.nio.fs.jgit.daemon.git.DaemonClient;
import org.uberfire.java.nio.fs.jgit.daemon.ssh.BaseGitCommand;
import org.uberfire.java.nio.fs.jgit.daemon.ssh.GitSSHService;
import org.uberfire.java.nio.fs.jgit.util.CommitContent;
import org.uberfire.java.nio.fs.jgit.util.CopyCommitContent;
import org.uberfire.java.nio.fs.jgit.util.DefaultCommitContent;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;
import org.uberfire.java.nio.fs.jgit.util.MoveCommitContent;
import org.uberfire.java.nio.fs.jgit.util.RevertCommitContent;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.SecuredFileSystemProvider;

import static org.eclipse.jgit.api.ListBranchCommand.ListMode.*;
import static org.eclipse.jgit.lib.Constants.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.base.dotfiles.DotFileUtils.*;
import static org.uberfire.java.nio.file.StandardOpenOption.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystemProvider implements SecuredFileSystemProvider {

    private static final Logger LOG = LoggerFactory.getLogger( JGitFileSystemProvider.class );

    protected static final String DEFAULT_IO_SERVICE_NAME = "default";

    public static final String GIT_ENV_KEY_DEFAULT_REMOTE_NAME = DEFAULT_REMOTE_NAME;

    /**
     * Specifies the list mode for the repository parent directory. Must match one of the enum constants defined in
     * {@link ListMode}.
     */
    public static final String GIT_ENV_KEY_LIST_MODE = "listMode";

    public static final String GIT_ENV_KEY_DEST_PATH = "out-dir";
    public static final String GIT_ENV_KEY_USER_NAME = "username";
    public static final String GIT_ENV_KEY_PASSWORD = "password";
    public static final String GIT_ENV_KEY_INIT = "init";

    private static final String SCHEME = "git";
    private static final int SCHEME_SIZE = ( SCHEME + "://" ).length();
    private static final int DEFAULT_SCHEME_SIZE = ( "default://" ).length();

    public static final String REPOSITORIES_CONTAINER_DIR = ".niogit";
    public static final String SSH_FILE_CERT_CONTAINER_DIR = ".security";

    public static final String DEFAULT_HOST_NAME = "localhost";
    public static final String DEFAULT_HOST_ADDR = "127.0.0.1";
    public static final String DAEMON_DEFAULT_ENABLED = "true";
    public static final String DAEMON_DEFAULT_PORT = "9418";
    public static final String SSH_DEFAULT_ENABLED = "true";
    public static final String SSH_DEFAULT_PORT = "8001";
    public static final String DEFAULT_COMMIT_LIMIT_TO_GC = "20";

    private File gitReposParentDir;

    private int commitLimit;
    private boolean daemonEnabled;
    private int daemonPort;
    private String daemonHostAddr;
    private String daemonHostName;
    private int daemonHostPort;

    private boolean sshEnabled;
    private int sshPort;
    private String sshHostAddr;
    private String sshHostName;
    private int sshHostPort;
    private File sshFileCertDir;

    private final Map<String, JGitFileSystem> fileSystems = new ConcurrentHashMap<String, JGitFileSystem>();
    private final Set<JGitFileSystem> closedFileSystems = new HashSet<JGitFileSystem>();
    private final Map<Repository, JGitFileSystem> repoIndex = new ConcurrentHashMap<Repository, JGitFileSystem>();
    private final Map<Repository, ClusterService> clusterMap = new ConcurrentHashMap<Repository, ClusterService>();

    private final Map<String, String> fullHostNames = new HashMap<String, String>();

    private boolean isDefault;

    private final Map<JGitFileSystem, Map<String, NotificationModel>> oldHeadsOfPendingDiffs = new HashMap<JGitFileSystem, Map<String, NotificationModel>>();

    private Daemon daemonService = null;
    private GitSSHService gitSSHService = null;
    private FileSystemAuthenticator authenticator;
    private FileSystemAuthorizer fileSystemAuthorizer;

    private void loadConfig( ConfigProperties config ) {
        LOG.debug( "Configuring from properties:" );

        final String currentDirectory = System.getProperty( "user.dir" );

        final ConfigProperty bareReposDirProp = config.get( "org.uberfire.nio.git.dir", currentDirectory );
        final ConfigProperty enabledProp = config.get( "org.uberfire.nio.git.daemon.enabled", DAEMON_DEFAULT_ENABLED );
        final ConfigProperty hostProp = config.get( "org.uberfire.nio.git.daemon.host", DEFAULT_HOST_ADDR );
        final ConfigProperty hostNameProp = config.get( "org.uberfire.nio.git.daemon.hostname", hostProp.isDefault() ? DEFAULT_HOST_NAME : hostProp.getValue() );
        final ConfigProperty portProp = config.get( "org.uberfire.nio.git.daemon.port", DAEMON_DEFAULT_PORT );
        final ConfigProperty hostPortProp = config.get( "org.uberfire.nio.git.daemon.hostport", DAEMON_DEFAULT_PORT );
        final ConfigProperty sshEnabledProp = config.get( "org.uberfire.nio.git.ssh.enabled", SSH_DEFAULT_ENABLED );
        final ConfigProperty sshHostProp = config.get( "org.uberfire.nio.git.ssh.host", DEFAULT_HOST_ADDR );
        final ConfigProperty sshHostNameProp = config.get( "org.uberfire.nio.git.ssh.hostname", sshHostProp.isDefault() ? DEFAULT_HOST_NAME : sshHostProp.getValue() );
        final ConfigProperty sshPortProp = config.get( "org.uberfire.nio.git.ssh.port", SSH_DEFAULT_PORT );
        final ConfigProperty sshCertDirProp = config.get( "org.uberfire.nio.git.ssh.cert.dir", currentDirectory );
        final ConfigProperty sshHostPortProp = config.get( "org.uberfire.nio.git.ssh.hostport", SSH_DEFAULT_PORT );
        final ConfigProperty commitLimitProp = config.get( "org.uberfire.nio.git.gc.limit", DEFAULT_COMMIT_LIMIT_TO_GC );

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( config.getConfigurationSummary( "Summary of JGit configuration:" ) );
        }

        gitReposParentDir = new File( bareReposDirProp.getValue(), REPOSITORIES_CONTAINER_DIR );
        commitLimit = commitLimitProp.getIntValue();

        daemonEnabled = enabledProp.getBooleanValue();
        if ( daemonEnabled ) {
            daemonPort = portProp.getIntValue();
            daemonHostAddr = hostProp.getValue();
            daemonHostName = hostNameProp.getValue();
            daemonHostPort = hostPortProp.getIntValue();
        }

        sshEnabled = sshEnabledProp.getBooleanValue();
        if ( sshEnabled ) {
            sshPort = sshPortProp.getIntValue();
            sshHostAddr = sshHostProp.getValue();
            sshHostName = sshHostNameProp.getValue();
            sshHostPort = sshHostPortProp.getIntValue();
            sshFileCertDir = new File( sshCertDirProp.getValue(), SSH_FILE_CERT_CONTAINER_DIR );
        }
    }

    public void onCloseFileSystem( final JGitFileSystem fileSystem ) {
        closedFileSystems.add( fileSystem );
        oldHeadsOfPendingDiffs.remove( fileSystem );
        if ( closedFileSystems.size() == fileSystems.size() ) {
            forceStopDaemon();
            shutdownSSH();
        }
    }

    public void onDisposeFileSystem( final JGitFileSystem fileSystem ) {
        onCloseFileSystem( fileSystem );
        closedFileSystems.remove( fileSystem );
        fileSystems.remove( fileSystem.id() );

        repoIndex.remove( fileSystem.gitRepo().getRepository() );
        clusterMap.remove( fileSystem.gitRepo().getRepository() );
    }

    public Set<JGitFileSystem> getOpenFileSystems() {
        Set<JGitFileSystem> open = new HashSet<JGitFileSystem>( fileSystems.values() );
        open.removeAll( closedFileSystems );
        return open;
    }

    @Override
    public void setAuthenticator( final FileSystemAuthenticator authenticator ) {
        this.authenticator = checkNotNull( "authenticator", authenticator );
        if ( gitSSHService != null ) {
            gitSSHService.setUserPassAuthenticator( authenticator );
        }
    }

    @Override
    public void setAuthorizer( FileSystemAuthorizer authorizer ) {
        this.fileSystemAuthorizer = checkNotNull( "authorizer", authorizer );
        if ( gitSSHService != null ) {
            gitSSHService.setAuthorizationManager( authorizer );
        }
    }

    public final class RepositoryResolverImpl<T> implements RepositoryResolver<T> {

        @Override
        public Repository open( final T client,
                                final String name )
                throws RepositoryNotFoundException,
                ServiceNotAuthorizedException, ServiceNotEnabledException,
                ServiceMayNotContinueException {
            final JGitFileSystem fs = fileSystems.get( name );
            if ( fs == null ) {
                throw new RepositoryNotFoundException( name );
            }
            return fs.gitRepo().getRepository();
        }

        public JGitFileSystem resolveFileSystem( final Repository repository ) {
            return repoIndex.get( repository );
        }
    }

    /**
     * Creates a JGit filesystem provider which takes its configuration from system properties. In a normal production
     * deployment of UberFire, this is the constructor that will be invoked by the ServiceLoader mechanism.
     * For a list of properties that affect the configuration of JGitFileSystemProvider, see the DEBUG log output of
     * this class during startup.
     */
    public JGitFileSystemProvider() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    /**
     * Creates a JGit filesystem provider which takes its configuration from the given map.
     * For a list of properties that affect the configuration of JGitFileSystemProvider, see the DEBUG log output of
     * this class during startup.
     */
    public JGitFileSystemProvider( final Map<String, String> gitPrefs ) {
        this( new ConfigProperties( gitPrefs ) );
    }

    /**
     * Creates a JGit filesystem provider which takes its configuration from the given ConfigProperties instance.
     * For a list of properties that affect the configuration of JGitFileSystemProvider, see the DEBUG log output of
     * this class during startup.
     */
    public JGitFileSystemProvider( final ConfigProperties gitPrefs ) {
        loadConfig( gitPrefs );
        CredentialsProvider.setDefault( new UsernamePasswordCredentialsProvider( "guest", "" ) );

        if ( daemonEnabled ) {
            fullHostNames.put( "git", daemonHostName + ":" + daemonHostPort );
        }
        if ( sshEnabled ) {
            fullHostNames.put( "ssh", sshHostName + ":" + sshHostPort );
        }

        rescanForExistingRepositories();

        if ( daemonEnabled ) {
            buildAndStartDaemon();
        } else {
            daemonService = null;
        }

        if ( sshEnabled ) {
            buildAndStartSSH();
        } else {
            gitSSHService = null;
        }
    }

    /**
     * Forgets all existing registered filesystems and scans for existing git repositories under
     * {@link #gitReposParentDir}. Call this method any time you add or remove git repositories without using this
     * class. If you only ever add or remove git repositories using the methods of this class, there is no need to call
     * this method.
     */
    public final void rescanForExistingRepositories() {
        fileSystems.clear();
        final String[] repos = gitReposParentDir.list( new FilenameFilter() {
            @Override
            public boolean accept( final File dir,
                                   String name ) {
                return name.endsWith( DOT_GIT_EXT );
            }
        } );
        if ( repos != null ) {
            for ( final String repo : repos ) {
                final File repoDir = new File( gitReposParentDir, repo );
                if ( repoDir.isDirectory() ) {
                    final String name = repoDir.getName().substring( 0, repoDir.getName().indexOf( DOT_GIT_EXT ) );
                    final JGitFileSystem fs = new JGitFileSystem( this, fullHostNames, newRepository( repoDir, true ), name, ALL, buildCredential( null ) );
                    LOG.debug( "Registering existing GIT filesystem '" + name + "' at " + repoDir );
                    fileSystems.put( name, fs );
                    repoIndex.put( fs.gitRepo().getRepository(), fs );
                    LOG.debug( "Running GIT GC on '" + name + "'" );
                    JGitUtil.gc( fs.gitRepo() );
                } else {
                    LOG.debug( "Not registering " + repoDir + " as a GIT filesystem because it is not a directory" );
                }
            }
        }
    }

    private void buildAndStartSSH() {
        final ReceivePackFactory receivePackFactory = new ReceivePackFactory<BaseGitCommand>() {
            @Override
            public ReceivePack create( final BaseGitCommand req,
                                       final Repository db ) throws ServiceNotEnabledException, ServiceNotAuthorizedException {

                return new ReceivePack( db ) {{
                    final ClusterService clusterService = clusterMap.get( db );
                    final JGitFileSystem fs = repoIndex.get( db );
                    final Map<String, RevCommit> oldTreeRefs = new HashMap<String, RevCommit>();

                    setPreReceiveHook( new PreReceiveHook() {
                        @Override
                        public void onPreReceive( final ReceivePack rp,
                                                  final Collection<ReceiveCommand> commands ) {
                            if ( clusterService != null ) {
                                clusterService.lock();
                            }

                            for ( final ReceiveCommand command : commands ) {
                                final RevCommit lastCommit = JGitUtil.getLastCommit( fs.gitRepo(), command.getRefName() );
                                oldTreeRefs.put( command.getRefName(), lastCommit );
                            }
                        }
                    } );

                    setPostReceiveHook( new PostReceiveHook() {
                        @Override
                        public void onPostReceive( final ReceivePack rp,
                                                   final Collection<ReceiveCommand> commands ) {
                            final String userName = req.getUser().getName();
                            for ( Map.Entry<String, RevCommit> oldTreeRef : oldTreeRefs.entrySet() ) {
                                final List<RevCommit> commits = JGitUtil.getCommits( fs, oldTreeRef.getKey(), oldTreeRef.getValue(), JGitUtil.getLastCommit( fs.gitRepo(), oldTreeRef.getKey() ) );
                                for ( final RevCommit revCommit : commits ) {
                                    notifyDiffs( fs, oldTreeRef.getKey(), "<ssh>", userName, revCommit.getFullMessage(), revCommit.getParent( 0 ).getTree(), revCommit.getTree() );
                                }
                            }

                            if ( clusterService != null ) {
                                //TODO {porcelli} hack, that should be addressed in future
                                clusterService.broadcast( DEFAULT_IO_SERVICE_NAME,
                                                          new MessageType() {

                                                              @Override
                                                              public String toString() {
                                                                  return "SYNC_FS";
                                                              }

                                                              @Override
                                                              public int hashCode() {
                                                                  return "SYNC_FS".hashCode();
                                                              }
                                                          },
                                                          new HashMap<String, String>() {{
                                                              put( "fs_scheme", "git" );
                                                              put( "fs_id", fs.id() );
                                                              put( "fs_uri", fs.toString() );
                                                          }}
                                                        );

                                clusterService.unlock();
                            }
                        }
                    } );
                }};
            }
        };

        gitSSHService = new GitSSHService();

        gitSSHService.setup( sshFileCertDir, sshHostAddr, sshPort, authenticator, fileSystemAuthorizer, receivePackFactory, new RepositoryResolverImpl<BaseGitCommand>() );

        gitSSHService.start();
    }

    void buildAndStartDaemon() {
        if ( daemonService == null || !daemonService.isRunning() ) {
            daemonService = new Daemon( new InetSocketAddress( daemonHostAddr, daemonPort ),
                                        new ExecutorWrapper( SimpleAsyncExecutorService.getUnmanagedInstance() ) );
            daemonService.setRepositoryResolver( new RepositoryResolverImpl<DaemonClient>() );
            try {
                daemonService.start();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            }
        }
    }

    private void shutdownSSH() {
        if ( gitSSHService != null ) {
            gitSSHService.stop();
        }
    }

    void forceStopDaemon() {
        if ( daemonService != null && daemonService.isRunning() ) {
            daemonService.stop();
        }
    }

    /**
     * Closes and disposes all open filesystems and stops the Git and SSH daemons if they are running. This filesystem
     * provider can be reactivated by attempting to open a new filesystem, by creating a new filesystem, or by calling
     * {@link #rescanForExistingRepositories()}.
     */
    public void shutdown() {
        for ( JGitFileSystem fs : getOpenFileSystems() ) {
            fs.close();
        }
        shutdownSSH();
        forceStopDaemon();
    }

    /**
     * Returns the directory that contains all the git repositories managed by this file system provider.
     */
    public File getGitRepoContainerDir() {
        return gitReposParentDir;
    }

    @Override
    public synchronized void forceAsDefault() {
        this.isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public FileSystem newFileSystem( final Path path,
                                     final Map<String, ?> env )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env )
            throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        checkNotNull( "uri", uri );
        checkCondition( "uri scheme not supported", uri.getScheme().equals( getScheme() ) || uri.getScheme().equals( "default" ) );
        checkURI( "uri", uri );
        checkNotNull( "env", env );

        final String name = extractRepoName( uri );

        if ( fileSystems.containsKey( name ) ) {
            throw new FileSystemAlreadyExistsException();
        }

        ListBranchCommand.ListMode listMode;
        if ( env.containsKey( GIT_ENV_KEY_LIST_MODE ) ) {
            try {
                listMode = ListBranchCommand.ListMode.valueOf( (String) env.get( GIT_ENV_KEY_LIST_MODE ) );
            } catch ( Exception ex ) {
                listMode = null;
            }
        } else {
            listMode = null;
        }

        final Git git;
        final CredentialsProvider credential;

        boolean bare = true;
        final String outPath = (String) env.get( GIT_ENV_KEY_DEST_PATH );

        final File repoDest;
        if ( outPath != null ) {
            repoDest = new File( outPath, name + DOT_GIT_EXT );
        } else {
            repoDest = new File( gitReposParentDir, name + DOT_GIT_EXT );
        }

        if ( env.containsKey( GIT_ENV_KEY_DEFAULT_REMOTE_NAME ) ) {
            final String originURI = env.get( GIT_ENV_KEY_DEFAULT_REMOTE_NAME ).toString();
            credential = buildCredential( env );
            git = cloneRepository( repoDest, originURI, bare, credential );
        } else {
            credential = buildCredential( null );
            git = newRepository( repoDest, bare );
        }

        final JGitFileSystem fs = new JGitFileSystem( this, fullHostNames, git, name, listMode, credential );
        fileSystems.put( name, fs );
        repoIndex.put( fs.gitRepo().getRepository(), fs );

        boolean init = false;

        if ( env.containsKey( GIT_ENV_KEY_INIT ) && Boolean.valueOf( env.get( GIT_ENV_KEY_INIT ).toString() ) ) {
            init = true;
        }

        if ( !env.containsKey( GIT_ENV_KEY_DEFAULT_REMOTE_NAME ) && init ) {
            try {
                final URI initURI = URI.create( getScheme() + "://master@" + name + "/readme.md" );
                final CommentedOption op = setupOp( env );
                final OutputStream stream = newOutputStream( getPath( initURI ), op );
                final String _init = "Repository Init Content\n" +
                        "=======================\n" +
                        "\n" +
                        "Your project description here.";
                stream.write( _init.getBytes() );
                stream.close();
            } catch ( final Exception e ) {
            }
            if ( !bare ) {
                //todo: checkout
            }
        }

        final Object _clusterService = env.get( "clusterService" );
        if ( _clusterService != null && _clusterService instanceof ClusterService ) {
            clusterMap.put( git.getRepository(), (ClusterService) _clusterService );
        }

        if ( daemonEnabled && daemonService != null && !daemonService.isRunning() ) {
            buildAndStartDaemon();
        }

        return fs;
    }

    private CommentedOption setupOp( final Map<String, ?> env ) {
        return null;
    }

    @Override
    public FileSystem getFileSystem( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull( "uri", uri );
        checkCondition( "uri scheme not supported", uri.getScheme().equals( getScheme() ) || uri.getScheme().equals( "default" ) );
        checkURI( "uri", uri );

        final JGitFileSystem fileSystem = fileSystems.get( extractRepoName( uri ) );

        if ( fileSystem == null ) {
            throw new FileSystemNotFoundException( "No filesystem for uri (" + uri + ") found." );
        }

        if ( hasSyncFlag( uri ) ) {
            try {
                final String treeRef = "master";
                final ObjectId oldHead = JGitUtil.getTreeRefObjectId( fileSystem.gitRepo().getRepository(), treeRef );
                final Map<String, String> params = getQueryParams( uri );
                syncRepository( fileSystem.gitRepo(), fileSystem.getCredential(), params.get( "sync" ), hasForceFlag( uri ) );
                final ObjectId newHead = JGitUtil.getTreeRefObjectId( fileSystem.gitRepo().getRepository(), treeRef );
                notifyDiffs( fileSystem, treeRef, "<system>", "<system>", "", oldHead, newHead );
            } catch ( final Exception ex ) {
                throw new IOException( ex );
            }
        }
        if ( hasPushFlag( uri ) ) {
            try {
                final Map<String, String> params = getQueryParams( uri );
                pushRepository( fileSystem.gitRepo(), fileSystem.getCredential(), params.get( "push" ), hasForceFlag( uri ) );
            } catch ( final Exception ex ) {
                throw new IOException( ex );
            }
        }

        return fileSystem;
    }

    @Override
    public Path getPath( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull( "uri", uri );
        checkCondition( "uri scheme not supported", uri.getScheme().equals( getScheme() ) || uri.getScheme().equals( "default" ) );
        checkURI( "uri", uri );

        final JGitFileSystem fileSystem = fileSystems.get( extractRepoName( uri ) );

        if ( fileSystem == null ) {
            throw new FileSystemNotFoundException();
        }

        return JGitPathImpl.create( fileSystem, extractPath( uri ), extractHost( uri ), false );
    }

    @Override
    public InputStream newInputStream( final Path path,
                                       final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException, SecurityException {
        checkNotNull( "path", path );

        final JGitPathImpl gPath = toPathImpl( path );

        return resolveInputStream( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );
    }

    @Override
    public OutputStream newOutputStream( final Path path,
                                         final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> result = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        if ( result.getK1().equals( PathType.DIRECTORY ) ) {
            throw new IOException();
        }

        try {
            final File file = File.createTempFile( "gitz", "woot" );
            return new FilterOutputStream( new FileOutputStream( file ) ) {
                @Override
                public void close() throws java.io.IOException {
                    super.close();

                    commit( gPath, buildCommitInfo( "{" + toPathImpl( path ).getPath() + "}", Arrays.asList( options ) ), new DefaultCommitContent( new HashMap<String, File>() {{
                        put( gPath.getPath(), file );
                    }} ) );
                }
            };
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    private CommitInfo buildCommitInfo( final String defaultMessage,
                                        final Collection<? extends Option> options ) {
        String sessionId = null;
        String name = null;
        String email = null;
        String message = defaultMessage;
        TimeZone timeZone = null;
        Date when = null;

        if ( options != null && !options.isEmpty() ) {
            final CommentedOption op = extractCommentedOption( options );
            if ( op != null ) {
                sessionId = op.getSessionId();
                name = op.getName();
                email = op.getEmail();
                if ( op.getMessage() != null && !op.getMessage().trim().isEmpty() ) {
                    message = op.getMessage() + " " + defaultMessage;
                }
                timeZone = op.getTimeZone();
                when = op.getWhen();
            }
        }

        return new CommitInfo( sessionId, name, email, message, timeZone, when );
    }

    final CommentedOption extractCommentedOption( final Collection<? extends Option> options ) {
        for ( final Option option : options ) {
            if ( option instanceof CommentedOption ) {
                return (CommentedOption) option;
            }
        }
        return null;
    }

    @Override
    public FileChannel newFileChannel( final Path path,
                                       Set<? extends OpenOption> options,
                                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel( final Path path,
                                                               final Set<? extends OpenOption> options,
                                                               final ExecutorService executor,
                                                               FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final Set<? extends OpenOption> options,
                                               final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final JGitPathImpl gPath = toPathImpl( path );

        if ( exists( path ) ) {
            if ( !shouldCreateOrOpenAByteChannel( options ) ) {
                throw new FileAlreadyExistsException( path.toString() );
            }
        }

        final Pair<PathType, ObjectId> result = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        if ( result.getK1().equals( PathType.DIRECTORY ) ) {
            throw new IOException();
        }

        try {
            if ( options != null && options.contains( READ ) ) {
                return openAByteChannel( path );
            } else {
                return createANewByteChannel( path, options, gPath, attrs );
            }
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        } finally {
            ( (AbstractPath) path ).clearCache();
        }
    }

    private SeekableByteChannel createANewByteChannel( final Path path,
                                                       final Set<? extends OpenOption> options,
                                                       final JGitPathImpl gPath,
                                                       final FileAttribute<?>[] attrs ) throws java.io.IOException {
        final File file = File.createTempFile( "gitz", "woot" );

        return new SeekableByteChannelFileBasedImpl( new RandomAccessFile( file, "rw" ).getChannel() ) {
            @Override
            public void close() throws java.io.IOException {
                super.close();

                File tempDot = null;
                final boolean hasDotContent;
                if ( options != null && options.contains( new DotFileOption() ) ) {
                    deleteIfExists( dot( path ), extractCommentedOption( options ) );
                    tempDot = File.createTempFile( "meta", "dot" );
                    hasDotContent = buildDotFile( path, new FileOutputStream( tempDot ), attrs );
                } else {
                    hasDotContent = false;
                }

                final File dotfile = tempDot;

                commit( gPath, buildCommitInfo( "{" + toPathImpl( path ).getPath() + "}", options ), new DefaultCommitContent( new HashMap<String, File>() {{
                    put( gPath.getPath(), file );
                    if ( hasDotContent ) {
                        put( toPathImpl( dot( gPath ) ).getPath(), dotfile );
                    }
                }} ) );
            }

        };
    }

    private SeekableByteChannelFileBasedImpl openAByteChannel( Path path ) throws FileNotFoundException {
        return new SeekableByteChannelFileBasedImpl( new RandomAccessFile( path.toFile(), "r" ).getChannel() );
    }

    private boolean shouldCreateOrOpenAByteChannel( Set<? extends OpenOption> options ) {
        return ( options != null && ( options.contains( TRUNCATE_EXISTING ) || options.contains( READ ) ) );
    }

    protected boolean exists( final Path path ) {
        try {
            readAttributes( path, BasicFileAttributes.class );
            return true;
        } catch ( final Exception ignored ) {
        }
        return false;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path path,
                                                     final DirectoryStream.Filter<Path> pfilter )
            throws NotDirectoryException, IOException, SecurityException {
        checkNotNull( "path", path );
        final DirectoryStream.Filter<Path> filter;
        if ( pfilter == null ) {
            filter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept( final Path entry ) throws IOException {
                    return true;
                }
            };
        } else {
            filter = pfilter;
        }

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> result = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        if ( !result.getK1().equals( PathType.DIRECTORY ) ) {
            throw new NotDirectoryException( path.toString() );
        }

        final List<JGitPathInfo> pathContent = listPathContent( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        return new DirectoryStream<Path>() {
            boolean isClosed = false;

            @Override
            public void close() throws IOException {
                if ( isClosed ) {
                    throw new IOException();
                }
                isClosed = true;
            }

            @Override
            public Iterator<Path> iterator() {
                if ( isClosed ) {
                    throw new IOException();
                }
                return new Iterator<Path>() {
                    private int i = -1;
                    private Path nextEntry = null;
                    public boolean atEof = false;

                    @Override
                    public boolean hasNext() {
                        if ( nextEntry == null && !atEof ) {
                            nextEntry = readNextEntry();
                        }
                        return nextEntry != null;
                    }

                    @Override
                    public Path next() {
                        final Path result;
                        if ( nextEntry == null && !atEof ) {
                            result = readNextEntry();
                        } else {
                            result = nextEntry;
                            nextEntry = null;
                        }
                        if ( result == null ) {
                            throw new NoSuchElementException();
                        }
                        return result;
                    }

                    private Path readNextEntry() {
                        if ( atEof ) {
                            return null;
                        }

                        Path result = null;
                        while ( true ) {
                            i++;
                            if ( i >= pathContent.size() ) {
                                atEof = true;
                                break;
                            }

                            final JGitPathInfo content = pathContent.get( i );
                            final Path path = JGitPathImpl.create( gPath.getFileSystem(), "/" + content.getPath(), gPath.getHost(), content.getObjectId(), gPath.isRealPath() );
                            if ( filter.accept( path ) ) {
                                result = path;
                                break;
                            }
                        }

                        return result;
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
    public void createDirectory( final Path path,
                                 final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "path", path );

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> result = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        if ( !result.getK1().equals( NOT_FOUND ) ) {
            throw new FileAlreadyExistsException( path.toString() );
        }

        try {
            final OutputStream outputStream = newOutputStream( path.resolve( ".gitignore" ) );
            outputStream.write( "# empty\n".getBytes() );
            outputStream.close();
        } catch ( final Exception e ) {
            throw new IOException( e );
        }
    }

    @Override
    public void createSymbolicLink( final Path link,
                                    final Path target,
                                    final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createLink( final Path link,
                            final Path existing )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete( final Path path,
                        final DeleteOption... options )
            throws DirectoryNotEmptyException, NoSuchFileException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( path instanceof JGitFSPath ) {
            deleteRepo( path.getFileSystem() );
            return;
        }

        final JGitPathImpl gPath = toPathImpl( path );

        if ( isBranch( gPath ) ) {
            deleteBranch( gPath );
            return;
        }

        deleteAsset( gPath, options );
    }

    private boolean deleteRepo( final FileSystem fileSystem ) {
        final File gitDir = ( (JGitFileSystem) fileSystem ).gitRepo().getRepository().getDirectory();
        fileSystem.close();
        fileSystem.dispose();

        try {
            if ( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) ) {
                //this operation forces a cache clean freeing any lock -> windows only issue!
                WindowCache.reconfigure( new WindowCacheConfig() );
            }
            FileUtils.delete( gitDir, FileUtils.RECURSIVE | FileUtils.RETRY );
            return true;
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    public void deleteAsset( final JGitPathImpl path,
                             final DeleteOption... options ) {
        final Pair<PathType, ObjectId> result = checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );

        if ( result.getK1().equals( PathType.DIRECTORY ) ) {
            if ( deleteNonEmptyDirectory( options ) ) {
                deleteResource( path, options );
                return;
            }
            final List<JGitPathInfo> content = listPathContent( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
            if ( content.size() == 1 && content.get( 0 ).getPath().equals( path.getPath().substring( 1 ) + "/.gitignore" ) ) {
                delete( path.resolve( ".gitignore" ) );
                deleteResource( path, options );
                return;
            }
            throw new DirectoryNotEmptyException( path.toString() );
        }

        if ( result.getK1().equals( NOT_FOUND ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        deleteResource( path, options );
    }

    void deleteResource( final JGitPathImpl path,
                         final DeleteOption... options ) {
        delete( path, buildCommitInfo( "delete {" + path.getPath() + "}", Arrays.asList( options ) ) );
    }

    private boolean deleteNonEmptyDirectory( final DeleteOption... options ) {

        for ( final DeleteOption option : options ) {
            if ( option.equals( StandardDeleteOption.NON_EMPTY_DIRECTORIES ) ) {
                return true;
            }
        }

        return false;
    }

    public void deleteBranch( final JGitPathImpl path ) {
        final Ref branch = getBranch( path.getFileSystem().gitRepo(), path.getRefTree() );

        if ( branch == null ) {
            throw new NoSuchFileException( path.toString() );
        }

        JGitUtil.deleteBranch( path.getFileSystem().gitRepo(), branch );
    }

    @Override
    public boolean deleteIfExists( final Path path,
                                   final DeleteOption... options )
            throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( path instanceof JGitFSPath ) {
            return deleteRepo( path.getFileSystem() );
        }

        final JGitPathImpl gPath = toPathImpl( path );

        if ( isBranch( gPath ) ) {
            return deleteBranchIfExists( gPath );
        }

        return deleteAssetIfExists( gPath, options );
    }

    public boolean deleteBranchIfExists( final JGitPathImpl path ) {
        final Ref branch = getBranch( path.getFileSystem().gitRepo(), path.getRefTree() );

        if ( branch == null ) {
            return false;
        }

        JGitUtil.deleteBranch( path.getFileSystem().gitRepo(), branch );
        return true;
    }

    public boolean deleteAssetIfExists( final JGitPathImpl path,
                                        final DeleteOption... options ) {
        final Pair<PathType, ObjectId> result = checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );

        if ( result.getK1().equals( PathType.DIRECTORY ) ) {
            if ( deleteNonEmptyDirectory( options ) ) {
                deleteResource( path, options );
                return true;
            }
            final List<JGitPathInfo> content = listPathContent( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
            if ( content.size() == 1 && content.get( 0 ).getPath().equals( path.getPath().substring( 1 ) + "/.gitignore" ) ) {
                delete( path.resolve( ".gitignore" ) );
                return true;
            }
            throw new DirectoryNotEmptyException( path.toString() );
        }

        if ( result.getK1().equals( NOT_FOUND ) ) {
            return false;
        }

        deleteResource( path, options );
        return true;
    }

    private String deleteCommitMessage( final String path,
                                        final DeleteOption... options ) {
        return "delete {" + path + "}";
    }

    @Override
    public Path readSymbolicLink( final Path link )
            throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        checkNotNull( "link", link );
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );

        final JGitPathImpl gSource = toPathImpl( source );
        final JGitPathImpl gTarget = toPathImpl( target );
        final boolean isBranch = isBranch( gSource ) && isBranch( gTarget );

        if ( options.length == 1 && options[ 0 ] instanceof CherryPickCopyOption ) {
            if ( !isBranch ) {
                throw new IOException( "Cherry pick needs source and target as root." );
            }
            final String[] commits = ( (CherryPickCopyOption) options[ 0 ] ).getCommits();
            if ( commits == null || commits.length == 0 ) {
                throw new IOException( "Cherry pick needs at least one commit id." );
            }
            cherryPick( gSource, gTarget, commits );
        } else {
            if ( isBranch ) {
                copyBranch( gSource, gTarget );
                return;
            }
            copyAsset( gSource, gTarget, options );
        }
    }

    private void cherryPick( final JGitPathImpl source,
                             final JGitPathImpl target,
                             final String... commits ) {
        JGitUtil.cherryPick( source.getFileSystem().gitRepo().getRepository(), target.getRefTree(), commits );
    }

    private void copyBranch( final JGitPathImpl source,
                             final JGitPathImpl target ) {
        checkCondition( "source and taget should have same setup", !hasSameFileSystem( source, target ) );
        if ( existsBranch( target ) ) {
            throw new FileAlreadyExistsException( target.toString() );
        }
        if ( !existsBranch( source ) ) {
            throw new NoSuchFileException( target.toString() );
        }
        createBranch( source, target );
    }

    private void copyAsset( final JGitPathImpl source,
                            final JGitPathImpl target,
                            final CopyOption... options ) {
        final Pair<PathType, ObjectId> sourceResult = checkPath( source.getFileSystem().gitRepo(), source.getRefTree(), source.getPath() );
        final Pair<PathType, ObjectId> targetResult = checkPath( target.getFileSystem().gitRepo(), target.getRefTree(), target.getPath() );

        if ( !isRoot( target ) && targetResult.getK1() != NOT_FOUND ) {
            if ( !contains( options, StandardCopyOption.REPLACE_EXISTING ) ) {
                throw new FileAlreadyExistsException( target.toString() );
            }
        }

        if ( sourceResult.getK1() == NOT_FOUND ) {
            throw new NoSuchFileException( target.toString() );
        }

        if ( !source.getRefTree().equals( target.getRefTree() ) ) {
            copyAssetContent( source, target, options );
        } else if ( !source.getFileSystem().equals( target.getFileSystem() ) ) {
            copyAssetContent( source, target, options );
        } else {
            final Map<JGitPathImpl, JGitPathImpl> sourceDest = new HashMap<JGitPathImpl, JGitPathImpl>();
            if ( sourceResult.getK1() == DIRECTORY ) {
                sourceDest.putAll( mapDirectoryContent( source, target, options ) );
            } else {
                sourceDest.put( source, target );
            }

            copyFiles( source, target, sourceDest, options );
        }
    }

    private void copyAssetContent( final JGitPathImpl source,
                                   final JGitPathImpl target,
                                   final CopyOption... options ) {
        final Pair<PathType, ObjectId> sourceResult = checkPath( source.getFileSystem().gitRepo(), source.getRefTree(), source.getPath() );
        final Pair<PathType, ObjectId> targetResult = checkPath( target.getFileSystem().gitRepo(), target.getRefTree(), target.getPath() );

        if ( !isRoot( target ) && targetResult.getK1() != NOT_FOUND ) {
            if ( !contains( options, StandardCopyOption.REPLACE_EXISTING ) ) {
                throw new FileAlreadyExistsException( target.toString() );
            }
        }

        if ( sourceResult.getK1() == NOT_FOUND ) {
            throw new NoSuchFileException( target.toString() );
        }

        if ( sourceResult.getK1() == DIRECTORY ) {
            copyDirectory( source, target, options );
            return;
        }

        copyFile( source, target, options );
    }

    private boolean contains( final CopyOption[] options,
                              final CopyOption opt ) {
        for ( final CopyOption option : options ) {
            if ( option.equals( opt ) ) {
                return true;
            }
        }
        return false;
    }

    private void copyDirectory( final JGitPathImpl source,
                                final JGitPathImpl target,
                                final CopyOption... options ) {
        final List<JGitPathImpl> directories = new ArrayList<JGitPathImpl>();
        for ( final Path path : newDirectoryStream( source, null ) ) {
            final JGitPathImpl gPath = toPathImpl( path );
            final Pair<PathType, ObjectId> pathResult = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );
            if ( pathResult.getK1() == DIRECTORY ) {
                directories.add( gPath );
                continue;
            }
            final JGitPathImpl gTarget = composePath( target, (JGitPathImpl) gPath.getFileName() );

            copyFile( gPath, gTarget );
        }
        for ( final JGitPathImpl directory : directories ) {
            createDirectory( composePath( target, (JGitPathImpl) directory.getFileName() ) );
        }
    }

    private JGitPathImpl composePath( final JGitPathImpl directory,
                                      final JGitPathImpl fileName,
                                      final CopyOption... options ) {
        if ( directory.getPath().endsWith( "/" ) ) {
            return toPathImpl( getPath( URI.create( directory.toUri().toString() + uriEncode( fileName.toString( false ) ) ) ) );
        }
        return toPathImpl( getPath( URI.create( directory.toUri().toString() + "/" + uriEncode( fileName.toString( false ) ) ) ) );
    }

    private String uriEncode( final String s ) {
        try {
            return URLEncoder.encode( s, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            return s;
        }
    }

    private void copyFile( final JGitPathImpl source,
                           final JGitPathImpl target,
                           final CopyOption... options ) {

        final InputStream in = newInputStream( source, convert( options ) );
        final SeekableByteChannel out = newByteChannel( target, new HashSet<OpenOption>() {{
            add( StandardOpenOption.TRUNCATE_EXISTING );
            for ( final CopyOption _option : options ) {
                if ( _option instanceof OpenOption ) {
                    add( (OpenOption) _option );
                }
            }
        }} );

        try {
            int count;
            byte[] buffer = new byte[ 8192 ];
            while ( ( count = in.read( buffer ) ) > 0 ) {
                out.write( ByteBuffer.wrap( buffer, 0, count ) );
            }
        } catch ( Exception e ) {
            throw new IOException( e );
        } finally {
            try {
                out.close();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            } finally {
                try {
                    in.close();
                } catch ( java.io.IOException e ) {
                    throw new IOException( e );
                }
            }
        }
    }

    private OpenOption[] convert( CopyOption... options ) {
        if ( options == null || options.length == 0 ) {
            return new OpenOption[ 0 ];
        }
        final List<OpenOption> newOptions = new ArrayList<OpenOption>( options.length );
        for ( final CopyOption option : options ) {
            if ( option instanceof OpenOption ) {
                newOptions.add( (OpenOption) option );
            }
        }

        return newOptions.toArray( new OpenOption[ newOptions.size() ] );
    }

    private void createBranch( final JGitPathImpl source,
                               final JGitPathImpl target ) {
        JGitUtil.createBranch( source.getFileSystem().gitRepo(), source.getRefTree(), target.getRefTree() );
    }

    private boolean existsBranch( final JGitPathImpl path ) {
        return hasBranch( path.getFileSystem().gitRepo(), path.getRefTree() );
    }

    private boolean isBranch( final JGitPathImpl path ) {
        return path.getPath().length() == 1 && path.getPath().equals( "/" );
    }

    private boolean isRoot( final JGitPathImpl path ) {
        return isBranch( path );
    }

    private boolean hasSameFileSystem( final JGitPathImpl source,
                                       final JGitPathImpl target ) {
        return source.getFileSystem().equals( target );
    }

    @Override
    public void move( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );

        final JGitPathImpl gSource = toPathImpl( source );
        final JGitPathImpl gTarget = toPathImpl( target );

        final boolean isSourceBranch = isBranch( gSource );
        final boolean isTargetBranch = isBranch( gTarget );

        if ( isSourceBranch && isTargetBranch ) {
            moveBranch( gSource, gTarget, options );
            return;
        }
        moveAsset( gSource, gTarget, options );
    }

    private void moveBranch( final JGitPathImpl source,
                             final JGitPathImpl target,
                             final CopyOption... options ) {
        checkCondition( "source and taget should have same setup", !hasSameFileSystem( source, target ) );

        if ( !exists( source ) ) {
            throw new NoSuchFileException( target.toString() );
        }

        boolean targetExists = existsBranch( target );
        if ( targetExists && !contains( options, StandardCopyOption.REPLACE_EXISTING ) ) {
            throw new FileAlreadyExistsException( target.toString() );
        }

        if ( !targetExists ) {
            createBranch( source, target );
            deleteBranch( source );
        } else {
            commit( target, buildCommitInfo( "reverting from {" + source.getPath() + "}", Arrays.asList( options ) ), new RevertCommitContent( source.getRefTree() ) );
        }
    }

    private void moveAsset( final JGitPathImpl source,
                            final JGitPathImpl target,
                            final CopyOption... options ) {
        final Pair<PathType, ObjectId> sourceResult = checkPath( source.getFileSystem().gitRepo(), source.getRefTree(), source.getPath() );
        final Pair<PathType, ObjectId> targetResult = checkPath( target.getFileSystem().gitRepo(), target.getRefTree(), target.getPath() );

        if ( !isRoot( target ) && targetResult.getK1() != NOT_FOUND ) {
            if ( !contains( options, StandardCopyOption.REPLACE_EXISTING ) ) {
                throw new FileAlreadyExistsException( target.toString() );
            }
        }

        if ( sourceResult.getK1() == NOT_FOUND ) {
            throw new NoSuchFileException( target.toString() );
        }

        if ( !source.getRefTree().equals( target.getRefTree() ) ) {
            copy( source, target, options );
            delete( source );
        } else {
            final Map<JGitPathImpl, JGitPathImpl> fromTo = new HashMap<JGitPathImpl, JGitPathImpl>();
            if ( sourceResult.getK1() == DIRECTORY ) {
                fromTo.putAll( mapDirectoryContent( source, target, options ) );
            } else {
                fromTo.put( source, target );
            }

            moveFiles( source, target, fromTo, options );
        }
    }

    private Map<JGitPathImpl, JGitPathImpl> mapDirectoryContent( final JGitPathImpl source,
                                                                 final JGitPathImpl target,
                                                                 final CopyOption... options ) {
        final Map<JGitPathImpl, JGitPathImpl> fromTo = new HashMap<JGitPathImpl, JGitPathImpl>();
        for ( final Path path : newDirectoryStream( source, null ) ) {
            final JGitPathImpl gPath = toPathImpl( path );
            final Pair<PathType, ObjectId> pathResult = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );
            if ( pathResult.getK1() == DIRECTORY ) {
                fromTo.putAll( mapDirectoryContent( gPath, composePath( target, (JGitPathImpl) gPath.getFileName() ) ) );
            } else {
                final JGitPathImpl gTarget = composePath( target, (JGitPathImpl) gPath.getFileName() );
                fromTo.put( gPath, gTarget );
            }
        }

        return fromTo;
    }

    private void moveFiles( final JGitPathImpl source,
                            final JGitPathImpl target,
                            final Map<JGitPathImpl, JGitPathImpl> fromTo,
                            final CopyOption... options ) {
        final Map<String, String> result = new HashMap<String, String>( fromTo.size() );
        for ( final Map.Entry<JGitPathImpl, JGitPathImpl> fromToEntry : fromTo.entrySet() ) {
            result.put( fixPath( fromToEntry.getKey().getPath() ), fixPath( fromToEntry.getValue().getPath() ) );
        }
        commit( source, buildCommitInfo( "moving from {" + source.getPath() + "} to {" + target.getPath() + "}", Arrays.asList( options ) ), new MoveCommitContent( result ) );
    }

    private void copyFiles( final JGitPathImpl source,
                            final JGitPathImpl target,
                            final Map<JGitPathImpl, JGitPathImpl> sourceDest,
                            final CopyOption... options ) {
        final Map<String, String> result = new HashMap<String, String>( sourceDest.size() );
        for ( final Map.Entry<JGitPathImpl, JGitPathImpl> sourceDestEntry : sourceDest.entrySet() ) {
            result.put( fixPath( sourceDestEntry.getKey().getPath() ), fixPath( sourceDestEntry.getValue().getPath() ) );
        }
        commit( source, buildCommitInfo( "copy from {" + source.getPath() + "} to {" + target.getPath() + "}", Arrays.asList( options ) ), new CopyCommitContent( result ) );
    }

    @Override
    public boolean isSameFile( final Path pathA,
                               final Path pathB )
            throws IOException, SecurityException {
        checkNotNull( "pathA", pathA );
        checkNotNull( "pathB", pathB );

        final JGitPathImpl gPathA = toPathImpl( pathA );
        final JGitPathImpl gPathB = toPathImpl( pathB );

        final Pair<PathType, ObjectId> resultA = checkPath( gPathA.getFileSystem().gitRepo(), gPathA.getRefTree(), gPathA.getPath() );
        final Pair<PathType, ObjectId> resultB = checkPath( gPathB.getFileSystem().gitRepo(), gPathB.getRefTree(), gPathB.getPath() );

        if ( resultA.getK1() == PathType.FILE && resultA.getK2().equals( resultB.getK2() ) ) {
            return true;
        }

        return pathA.equals( pathB );
    }

    @Override
    public boolean isHidden( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        final JGitPathImpl gPath = toPathImpl( path );

        if ( gPath.getFileName() == null ) {
            return false;
        }

        return toPathImpl( path.getFileName() ).toString( false ).startsWith( "." );
    }

    @Override
    public FileStore getFileStore( final Path path )
            throws IOException, SecurityException {
        checkNotNull( "path", path );

        return new JGitFileStore( toPathImpl( path ).getFileSystem().gitRepo().getRepository() );
    }

    @Override
    public void checkAccess( final Path path,
                             final AccessMode... modes )
            throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        checkNotNull( "path", path );

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> result = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );

        if ( result.getK1().equals( NOT_FOUND ) ) {
            throw new NoSuchFileException( path.toString() );
        }
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type,
                                                                 final LinkOption... options )
            throws NoSuchFileException {
        checkNotNull( "path", path );
        checkNotNull( "type", type );

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> pathResult = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );
        if ( pathResult.getK1().equals( NOT_FOUND ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        final V resultView = gPath.getAttrView( type );

        if ( resultView == null ) {
            if ( type == BasicFileAttributeView.class || type == JGitBasicAttributeView.class ) {
                final V newView = (V) new JGitBasicAttributeView( gPath );
                gPath.addAttrView( newView );
                return newView;
            } else if ( type == VersionAttributeView.class || type == JGitVersionAttributeView.class ) {
                final V newView = (V) new JGitVersionAttributeView( gPath );
                gPath.addAttrView( newView );
                return newView;
            }
        }

        return resultView;
    }

    private ExtendedAttributeView getFileAttributeView( final JGitPathImpl path,
                                                        final String name,
                                                        final LinkOption... options ) {
        final ExtendedAttributeView view = path.getAttrView( name );

        if ( view == null ) {

            if ( name.equals( "basic" ) ) {
                final JGitBasicAttributeView newView = new JGitBasicAttributeView( path );
                path.addAttrView( newView );
                return newView;

            } else if ( name.equals( "version" ) ) {
                final JGitVersionAttributeView newView = new JGitVersionAttributeView( path );
                path.addAttrView( newView );
                return newView;
            }
        }
        return view;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes( final Path path,
                                                             final Class<A> type,
                                                             final LinkOption... options )
            throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "type", type );

        final JGitPathImpl gPath = toPathImpl( path );

        final Pair<PathType, ObjectId> pathResult = checkPath( gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath() );
        if ( pathResult.getK1().equals( NOT_FOUND ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        if ( type == VersionAttributes.class ) {
            final JGitVersionAttributeView view = getFileAttributeView( path, JGitVersionAttributeView.class, options );
            return (A) view.readAttributes();
        } else if ( type == BasicFileAttributesImpl.class || type == BasicFileAttributes.class ) {
            final JGitBasicAttributeView view = getFileAttributeView( path, JGitBasicAttributeView.class, options );
            return (A) view.readAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes,
                                               final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attributes );

        final String[] s = split( attributes );
        if ( s[ 0 ].length() == 0 ) {
            throw new IllegalArgumentException( attributes );
        }

        final ExtendedAttributeView view = getFileAttributeView( toPathImpl( path ), s[ 0 ], options );
        if ( view == null ) {
            throw new UnsupportedOperationException( "View '" + s[ 0 ] + "' not available" );
        }

        return view.readAttributes( s[ 1 ].split( "," ) );
    }

    @Override
    public void setAttribute( final Path path,
                              final String attribute,
                              final Object value,
                              final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attribute );

        if ( attribute.equals( FileSystemState.FILE_SYSTEM_STATE_ATTR ) ) {
            JGitFileSystem fileSystem = (JGitFileSystem) path.getFileSystem();

            if ( value instanceof CommentedOption ) {
                fileSystem.setBatchCommitInfo( "Batch mode", (CommentedOption) value );
                return;
            }

            final boolean isOriginalStateBatch = fileSystem.isOnBatch();

            fileSystem.setState( value.toString() );
            FileSystemState.valueOf( value.toString() );

            if ( isOriginalStateBatch && !fileSystem.isOnBatch() ) {
                fileSystem.setBatchCommitInfo( null );
                notifyAllDiffs();
            }
            fileSystem.setHadCommitOnBatchState( false );
            return;
        }

        final String[] s = split( attribute );
        if ( s[ 0 ].length() == 0 ) {
            throw new IllegalArgumentException( attribute );
        }
        final ExtendedAttributeView view = getFileAttributeView( toPathImpl( path ), s[ 0 ], options );
        if ( view == null ) {
            throw new UnsupportedOperationException( "View '" + s[ 0 ] + "' not available" );
        }

        view.setAttribute( s[ 1 ], value );
    }

    private void checkURI( final String paramName,
                           final URI uri )
            throws IllegalArgumentException {
        checkNotNull( "uri", uri );

        if ( uri.getAuthority() == null || uri.getAuthority().isEmpty() ) {
            throw new IllegalArgumentException( "Parameter named '" + paramName + "' is invalid, missing host repository!" );
        }

        int atIndex = uri.getPath().indexOf( "@" );
        if ( atIndex != -1 && !uri.getAuthority().contains( "@" ) ) {
            if ( uri.getPath().indexOf( "/", atIndex ) == -1 ) {
                throw new IllegalArgumentException( "Parameter named '" + paramName + "' is invalid, missing host repository!" );
            }
        }

    }

    private String extractHost( final URI uri ) {
        checkNotNull( "uri", uri );

        int atIndex = uri.getPath().indexOf( "@" );
        if ( atIndex != -1 && !uri.getAuthority().contains( "@" ) ) {
            return uri.getAuthority() + uri.getPath().substring( 0, uri.getPath().indexOf( "/", atIndex ) );
        }

        return uri.getAuthority();
    }

    private String extractRepoName( final URI uri ) {
        checkNotNull( "uri", uri );

        final String host = extractHost( uri );

        int index = host.indexOf( '@' );
        if ( index != -1 ) {
            return host.substring( index + 1 );
        }

        return host;
    }

    private boolean hasSyncFlag( final URI uri ) {
        checkNotNull( "uri", uri );

        return uri.getQuery() != null && uri.getQuery().contains( "sync" );
    }

    private boolean hasForceFlag( URI uri ) {
        checkNotNull( "uri", uri );

        return uri.getQuery() != null && uri.getQuery().contains( "force" );
    }

    private boolean hasPushFlag( final URI uri ) {
        checkNotNull( "uri", uri );

        return uri.getQuery() != null && uri.getQuery().contains( "push" );
    }

    //by spec, it should be a list of pairs, but here we're just uisng a map.
    private static Map<String, String> getQueryParams( final URI uri ) {
        final String[] params = uri.getQuery().split( "&" );
        return new HashMap<String, String>( params.length ) {{
            for ( String param : params ) {
                final String[] kv = param.split( "=" );
                final String name = kv[ 0 ];
                final String value;
                if ( kv.length == 2 ) {
                    value = kv[ 1 ];
                } else {
                    value = "";
                }

                put( name, value );
            }
        }};
    }

    private String extractPath( final URI uri ) {
        checkNotNull( "uri", uri );

        final String host = extractHost( uri );

        final String path = EncodingUtil.decode( uri.toString() ).substring( getSchemeSize( uri ) + host.length() );

        if ( path.startsWith( "/:" ) ) {
            return path.substring( 2 );
        }

        return path;
    }

    private CredentialsProvider buildCredential( final Map<String, ?> env ) {
        if ( env != null ) {
            if ( env.containsKey( GIT_ENV_KEY_USER_NAME ) ) {
                if ( env.containsKey( GIT_ENV_KEY_PASSWORD ) ) {
                    return new UsernamePasswordCredentialsProvider( env.get( GIT_ENV_KEY_USER_NAME ).toString(), env.get( GIT_ENV_KEY_PASSWORD ).toString() );
                }
                return new UsernamePasswordCredentialsProvider( env.get( GIT_ENV_KEY_USER_NAME ).toString(), "" );
            }
        }
        return CredentialsProvider.getDefault();
    }

    private JGitPathImpl toPathImpl( final Path path ) {
        if ( path instanceof JGitPathImpl ) {
            return (JGitPathImpl) path;
        }
        throw new IllegalArgumentException( "Path not supported by current provider." );
    }

    private String[] split( final String attribute ) {
        final String[] s = new String[ 2 ];
        final int pos = attribute.indexOf( ':' );
        if ( pos == -1 ) {
            s[ 0 ] = "basic";
            s[ 1 ] = attribute;
        } else {
            s[ 0 ] = attribute.substring( 0, pos );
            s[ 1 ] = ( pos == attribute.length() ) ? "" : attribute.substring( pos + 1 );
        }
        return s;
    }

    private int getSchemeSize( final URI uri ) {
        if ( uri.getScheme().equals( SCHEME ) ) {
            return SCHEME_SIZE;
        }
        return DEFAULT_SCHEME_SIZE;
    }

    private void delete( final JGitPathImpl path,
                         final CommitInfo commitInfo ) {
        commit( path, commitInfo, new DefaultCommitContent( new HashMap<String, File>() {{
            put( path.getPath(), null );
        }} ) );
    }

    private synchronized void commit( final JGitPathImpl path,
                                      final CommitInfo commitInfo,
                                      final CommitContent commitContent ) {
        final Git git = path.getFileSystem().gitRepo();
        final String branchName = path.getRefTree();
        JGitFileSystem fileSystem = path.getFileSystem();
        final boolean batchState = fileSystem.isOnBatch();
        final boolean amend = batchState && fileSystem.isHadCommitOnBatchState();

        final ObjectId oldHead = JGitUtil.getTreeRefObjectId( path.getFileSystem().gitRepo().getRepository(), branchName );

        final boolean hasCommit;
        if ( batchState && fileSystem.getBatchCommitInfo() != null ) {
            hasCommit = JGitUtil.commit( git, branchName, fileSystem.getBatchCommitInfo(), amend, commitContent );
        } else {
            hasCommit = JGitUtil.commit( git, branchName, commitInfo, amend, commitContent );
        }

        if ( hasCommit ) {
            int value = fileSystem.incrementAndGetCommitCount();
            if ( value >= commitLimit ) {
                JGitUtil.gc( git );
                fileSystem.resetCommitCount();
            }
        }

        if ( !batchState ) {
            final ObjectId newHead = JGitUtil.getTreeRefObjectId( path.getFileSystem().gitRepo().getRepository(), branchName );

            notifyDiffs( path.getFileSystem(), branchName, commitInfo.getSessionId(), commitInfo.getName(), commitInfo.getMessage(), oldHead, newHead );
        } else if ( !oldHeadsOfPendingDiffs.containsKey( path.getFileSystem() ) ||
                !oldHeadsOfPendingDiffs.get( path.getFileSystem() ).containsKey( branchName ) ) {

            if ( !oldHeadsOfPendingDiffs.containsKey( path.getFileSystem() ) ) {
                oldHeadsOfPendingDiffs.put( path.getFileSystem(), new HashMap<String, NotificationModel>() );
            }

            if ( fileSystem.getBatchCommitInfo() != null ) {
                oldHeadsOfPendingDiffs.get( path.getFileSystem() ).put( branchName, new NotificationModel( oldHead, fileSystem.getBatchCommitInfo().getSessionId(), fileSystem.getBatchCommitInfo().getName(), fileSystem.getBatchCommitInfo().getMessage() ) );
            } else {
                oldHeadsOfPendingDiffs.get( path.getFileSystem() ).put( branchName, new NotificationModel( oldHead, commitInfo.getSessionId(), commitInfo.getName(), commitInfo.getMessage() ) );
            }
        }

        if ( path.getFileSystem().isOnBatch() && !fileSystem.isHadCommitOnBatchState() ) {
            fileSystem.setHadCommitOnBatchState( hasCommit );
        }
    }

    private void notifyAllDiffs() {
        for ( Map.Entry<JGitFileSystem, Map<String, NotificationModel>> jGitFileSystemMapEntry : oldHeadsOfPendingDiffs.entrySet() ) {
            for ( Map.Entry<String, NotificationModel> branchNameNotificationModelEntry : jGitFileSystemMapEntry.getValue().entrySet() ) {
                final ObjectId newHead = JGitUtil.getTreeRefObjectId( jGitFileSystemMapEntry.getKey().gitRepo().getRepository(), branchNameNotificationModelEntry.getKey() );
                notifyDiffs( jGitFileSystemMapEntry.getKey(),
                             branchNameNotificationModelEntry.getKey(),
                             branchNameNotificationModelEntry.getValue().getSessionId(),
                             branchNameNotificationModelEntry.getValue().getUserName(),
                             branchNameNotificationModelEntry.getValue().getMessage(),
                             branchNameNotificationModelEntry.getValue().getOriginalHead(),
                             newHead );
            }
        }
        oldHeadsOfPendingDiffs.clear();
    }

    private void notifyDiffs( final JGitFileSystem fs,
                              final String _tree,
                              final String sessionId,
                              final String userName,
                              final String message,
                              final ObjectId oldHead,
                              final ObjectId newHead ) {

        final String tree;
        if ( _tree.startsWith( "refs/" ) ) {
            tree = _tree.substring( _tree.lastIndexOf( "/" ) + 1 );
        } else {
            tree = _tree;
        }

        final String host = tree + "@" + fs.getName();
        final Path root = JGitPathImpl.createRoot( fs, "/", host, false );

        final List<DiffEntry> diff = JGitUtil.getDiff( fs.gitRepo().getRepository(), oldHead, newHead );
        final List<WatchEvent<?>> events = new ArrayList<WatchEvent<?>>( diff.size() );

        for ( final DiffEntry diffEntry : diff ) {
            final Path oldPath;
            if ( !diffEntry.getOldPath().equals( DiffEntry.DEV_NULL ) ) {
                oldPath = JGitPathImpl.create( fs, "/" + diffEntry.getOldPath(), host, null, false );
            } else {
                oldPath = null;
            }

            final Path newPath;
            if ( !diffEntry.getNewPath().equals( DiffEntry.DEV_NULL ) ) {
                JGitPathInfo pathInfo = resolvePath( fs.gitRepo(), tree, diffEntry.getNewPath() );
                newPath = JGitPathImpl.create( fs, "/" + pathInfo.getPath(), host, pathInfo.getObjectId(), false );
            } else {
                newPath = null;
            }

            events.add( new WatchEvent() {
                @Override
                public Kind kind() {
                    switch ( diffEntry.getChangeType() ) {
                        case ADD:
                        case COPY:
                            return StandardWatchEventKind.ENTRY_CREATE;
                        case DELETE:
                            return StandardWatchEventKind.ENTRY_DELETE;
                        case MODIFY:
                            return StandardWatchEventKind.ENTRY_MODIFY;
                        case RENAME:
                            return StandardWatchEventKind.ENTRY_RENAME;
                        default:
                            throw new RuntimeException();
                    }
                }

                @Override
                public int count() {
                    return 1;
                }

                @Override
                public Object context() {
                    return new WatchContext() {

                        @Override
                        public Path getPath() {
                            return newPath;
                        }

                        @Override
                        public Path getOldPath() {
                            return oldPath;
                        }

                        @Override
                        public String getSessionId() {
                            return sessionId;
                        }

                        @Override
                        public String getMessage() {
                            return message;
                        }

                        @Override
                        public String getUser() {
                            return userName;
                        }
                    };
                }

                @Override
                public String toString() {
                    return "WatchEvent{" +
                            "newPath=" + newPath +
                            ", oldPath=" + oldPath +
                            ", sessionId='" + sessionId + '\'' +
                            ", userName='" + userName + '\'' +
                            ", message='" + message + '\'' +
                            ", changeType=" + diffEntry.getChangeType() +
                            '}';
                }
            } );
        }
        if ( !events.isEmpty() ) {
            fs.publishEvents( root, events );
        }
    }

    /**
     * Adapts a {@link SimpleAsyncExecutorService} to an {@link Executor} because SimpleAsyncExecutorService can't
     * implement Executor directly due to bugs in some older CDI implementations.
     */
    private static class ExecutorWrapper implements Executor {

        private final SimpleAsyncExecutorService simpleAsyncExecutor;

        public ExecutorWrapper( SimpleAsyncExecutorService simpleAsyncExecutor ) {
            this.simpleAsyncExecutor = checkNotNull( "simpleAsyncExecutor",
                                                     simpleAsyncExecutor );
        }

        @Override
        public void execute( Runnable command ) {
            simpleAsyncExecutor.execute( command );
        }

    }

}
