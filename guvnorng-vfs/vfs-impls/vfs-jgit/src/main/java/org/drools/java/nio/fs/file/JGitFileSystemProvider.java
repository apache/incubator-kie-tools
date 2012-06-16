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

package org.drools.java.nio.fs.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import org.drools.java.nio.IOException;
import org.drools.java.nio.channels.AsynchronousFileChannel;
import org.drools.java.nio.channels.SeekableByteChannel;
import org.drools.java.nio.file.AccessDeniedException;
import org.drools.java.nio.file.AccessMode;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileStore;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.FileSystemAlreadyExistsException;
import org.drools.java.nio.file.FileSystemNotFoundException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.attribute.BasicFileAttributeView;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileAttributeView;
import org.drools.java.nio.file.spi.FileSystemProvider;
import org.drools.java.nio.fs.base.GeneralPathImpl;
import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.awtui.AwtCredentialsProvider;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.DepthWalk.Commit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;

import com.gitblit.FileSettings;
import com.gitblit.GitBlit;
import com.gitblit.GitBlitException;
import com.gitblit.models.PathModel;
import com.gitblit.models.RefModel;
import com.gitblit.models.RepositoryModel;
import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.StringUtils;

import static org.drools.java.nio.util.Preconditions.*;

public class JGitFileSystemProvider implements FileSystemProvider {

    private final JGitFileSystem fileSystem;
    private boolean isDefault;
    
    public static final String REPOSITORIES_ROOT_DIR = ".git";

    //TODO: persistent repository list to a guvnorng info git repository or to some configuration files?
    private static Map<String, JGitRepositoryConfiguration> repositories = new HashMap<String, JGitRepositoryConfiguration>();

    public JGitFileSystemProvider() {
        //TODO: initialize repositories from a configuration file or from a git repository
        
        this.fileSystem = new JGitFileSystem(this);
    }    

    @Override
    public synchronized void forceAsDefault() {
        this.isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override public String getScheme() {
        return "jgit";
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        validateURI(uri);
        
        String rootJGitRepositoryName = getRootJGitRepositoryName(uri.getPath());
        
        String fromGitURL = (String)env.get("fromGitURL");
        if (fromGitURL == null || "".equals(fromGitURL)) {
            throw new IllegalArgumentException("fromGitURL is undefined");
        } 
        String userName = (String)env.get("username");
        String password = (String)env.get("password");
        UsernamePasswordCredentialsProvider credential = new UsernamePasswordCredentialsProvider(userName, password);
        
        if(repositories.containsKey(rootJGitRepositoryName)) {
            throw new FileSystemAlreadyExistsException("FileSystem identifed by URI: " + uri + " already exists");
        }
        
        try {
            cloneOrFetch(rootJGitRepositoryName, fromGitURL, credential);
        } catch (Exception e) {
            throw new IOException();
        }
        
        JGitRepositoryConfiguration jGitRepositoryConfiguration = new JGitRepositoryConfiguration();
        jGitRepositoryConfiguration.setRepositoryName(rootJGitRepositoryName);
        jGitRepositoryConfiguration.setFromGitURL(fromGitURL);
        jGitRepositoryConfiguration.setUserName(userName);
        jGitRepositoryConfiguration.setPassword(password);
        repositories.put(rootJGitRepositoryName, jGitRepositoryConfiguration);
        
        //TODO: Set up FileSystem more properly to represent the status of git repository
        FileSystem jGitFileSystem = new JGitFileSystem(this);
        return jGitFileSystem;
    }
    
    private void validateURI(URI uri) {
        if (!uri.getScheme().equalsIgnoreCase(getScheme())) {
            throw new IllegalArgumentException(
                    "URI does not match this provider");
        } else if (uri.getAuthority() != null) {
            throw new IllegalArgumentException("Authority component should not present");
        } else if (uri.getPath() == null) {
            throw new IllegalArgumentException("Path component is undefined");
        } else if (uri.getQuery() != null) {
            throw new IllegalArgumentException("Query component should not present");
        } else if (uri.getFragment() != null) {
            throw new IllegalArgumentException("Fragment component shoud not present");
        }        
    }
        
    @Override
    public FileSystem getFileSystem(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        //validateURI(uri);
        
        String path = getRootJGitRepositoryName(uri.getPath());

        if(!repositories.containsKey(path)) {
            throw new FileSystemNotFoundException("FileSystem identifed by URI: " + uri + " does not exist");
        }
        JGitRepositoryConfiguration jGitRepositoryConfiguration = repositories.get(path);
        String userName = (String)jGitRepositoryConfiguration.getUserName();
        String password = (String)jGitRepositoryConfiguration.getPassword();
        UsernamePasswordCredentialsProvider credential = new UsernamePasswordCredentialsProvider(userName, password);     
        try {
            cloneOrFetch(path, jGitRepositoryConfiguration.getFromGitURL(), credential);
        } catch (Exception e) {
            throw new IOException();
        }       
        
        //TODO: Set up FileSystem more properly to represent the status of git repository
        FileSystem jGitFileSystem = new JGitFileSystem(this);
        return jGitFileSystem;
    }
    
    private static String getRootJGitRepositoryName(final String path) {
        String rootJGitRepositoryName = path;
        if(rootJGitRepositoryName.startsWith("/")) {
            rootJGitRepositoryName = rootJGitRepositoryName.substring(1);
        }    
        if(rootJGitRepositoryName.indexOf("/") > 0) {
            rootJGitRepositoryName = rootJGitRepositoryName.substring(0, rootJGitRepositoryName.indexOf("/"));
        }
        return rootJGitRepositoryName;
    }
    
    private static String getPathRelativeToRootJGitRepository(final String path) {
        String rootJGitRepositoryName = getRootJGitRepositoryName(path);
        int indexOfEndOfRootJGitRepositoryName = path.indexOf(rootJGitRepositoryName) + rootJGitRepositoryName.length() + 1;
        String relativePath = indexOfEndOfRootJGitRepositoryName > path.length() ? "" : path.substring(indexOfEndOfRootJGitRepositoryName);
        if(relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }    
        return relativePath;
    }
    
    @Override
    public Path getPath(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        FileSystem fileSystem = getFileSystem(uri);
        
        return GeneralPathImpl.create(fileSystem, uri.getPath(), false);
    }

    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {        
        final String rootJGitRepositoryName = getRootJGitRepositoryName(path.toString());
        String relativePath = getPathRelativeToRootJGitRepository(path.toString());   
        Repository repo;
        try {
            repo = getRepository(rootJGitRepositoryName);
        } catch (java.io.IOException e) {
            throw new IOException();
        }
        
        byte[] byteContent = JGitUtils.getByteContent(repo, null, relativePath);
        return new ByteArrayInputStream(byteContent);
/*        
        final File file = path.toFile();
        if (!file.exists()) {
            throw new NoSuchFileException(file.toString());
        }
        try {
            return new FileInputStream(path.toFile());
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException(e.getMessage());
        }*/
    }

    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... options) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        try {
            return new FileOutputStream(path.toFile());
        } catch (FileNotFoundException e) {
            throw new IOException();
        }
    }

    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> options, final ExecutorService executor, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final File file = checkNotNull("path", path).toFile();
        if (file.exists()) {
            throw new FileAlreadyExistsException("");
        }
        try {
            file.createNewFile();
            return new SeekableByteChannel() {
                @Override public long position() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel position(long newPosition) throws IOException {
                    return null;
                }

                @Override public long size() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel truncate(long size) throws IOException {
                    return null;
                }

                @Override public int read(ByteBuffer dst) throws java.io.IOException {
                    return 0;
                }

                @Override public int write(ByteBuffer src) throws java.io.IOException {
                    return 0;
                }

                @Override public boolean isOpen() {
                    return false;
                }

                @Override public void close() throws java.io.IOException {
                }
            };
        } catch (java.io.IOException e) {
            throw new IOException();
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<Path> filter) throws NotDirectoryException, IOException, SecurityException {
        try {
            final String rootJGitRepositoryName = getRootJGitRepositoryName(dir.toString());
            String relativePath = getPathRelativeToRootJGitRepository(dir.toString());            
            Repository repo = getRepository(rootJGitRepositoryName);
            
            final List<PathModel> files = JGitUtils.getFilesInPath(repo, relativePath, null);
            
            return new DirectoryStream<Path>() {
                @Override
                public void close() throws IOException {
                }

                @Override
                public Iterator<Path> iterator() {
                    return new Iterator<Path>() {
                        private int i = 0;

                        @Override public boolean hasNext() {
                            return i < files.size();
                        }

                        @Override public Path next() {
                            if (i < files.size()) {
                                PathModel pathModel = files.get(i);
                                System.out.println(pathModel.path + "  isTree: " + pathModel.isTree());
                                i++;
                                //String uri = "jgit:///" + rootJGitRepositoryName + "/" + pathModel.path;
                                String uri = "/" + rootJGitRepositoryName + "/" + pathModel.path;
                                return GeneralPathImpl.create(getDefaultFileSystem(), uri, false);
                                //return GeneralPathImpl.createFromFile(getDefaultFileSystem(), result);
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
        } catch (java.io.IOException e) {            
            IOException i = new IOException();
            i.initCause(e);
            throw i;
        }
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("dir", dir).toFile().mkdirs();
    }

    @Override
    public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createLink(final Path link, final Path existing) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path).toFile().delete();
        //toGeneralPathImpl(path).clearCache();
    }

    @Override public boolean deleteIfExists(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        return checkNotNull("path", path).toFile().delete();
    }

    @Override public Path readSymbolicLink(final Path link) throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void copy(final Path source, final Path target, final CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void move(Path source, Path target, CopyOption... options) throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean isSameFile(Path path, Path path2) throws IOException, SecurityException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isHidden(final Path path) throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        return ((JGitlFileAttributes)getFileAttributeView(path, BasicFileAttributeView.class, null).readAttributes()).isHidden();
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException, SecurityException {
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes)
            throws UnsupportedOperationException, AccessDeniedException, IOException, SecurityException {
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        final String rootJGitRepositoryName = getRootJGitRepositoryName(path.toString());
        String relativePath = getPathRelativeToRootJGitRepository(path.toString());
        Repository repo;
        try {
            repo = getRepository(rootJGitRepositoryName);
        } catch (java.io.IOException e) {
            throw new IOException();
        }
        
        if (type == BasicFileAttributeView.class) {
            PathModel pathModel = JGitUtils.getPathModel(repo, relativePath, null);
            return (V) new JGitFileAttributeView(pathModel);
        }

        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        //TODO: Check if the path exists in git repo
/*        final GeneralPathImpl pathImpl = toGeneralPathImpl(path);
        if (!pathImpl.getAttrs().exists()) {
            throw new NoSuchFileException("");
        }*/

        if (type == BasicFileAttributes.class) {
            BasicFileAttributeView view = getFileAttributeView(path, BasicFileAttributeView.class, options);
            return (A) view.readAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {       
        final String rootJGitRepositoryName = getRootJGitRepositoryName(path.toString());
        String relativePath = getPathRelativeToRootJGitRepository(path.toString());
        Repository repo;
        try {
            repo = getRepository(rootJGitRepositoryName);
        } catch (java.io.IOException e) {
            throw new IOException();
        }
        
        if (attributes.equals("*")) {
            PathModel pathModel = JGitUtils.getPathModel(repo, relativePath, null);
            JGitlFileAttributes attrs = new JGitlFileAttributes(pathModel);
            final Map<String, Object> result = new HashMap<String, Object>();
            result.put("isRegularFile", attrs.isRegularFile());
            result.put("isDirectory", attrs.isDirectory());
            result.put("isSymbolicLink", attrs.isSymbolicLink());
            result.put("isOther", attrs.isOther());
            result.put("size", new Long(attrs.size()));
            result.put("fileKey", attrs.fileKey());
            result.put("exists", attrs.exists());
            result.put("isReadable", attrs.isReadable());
            result.put("isExecutable", attrs.isExecutable());
            result.put("isHidden", attrs.isHidden());
            //todo check why errai can't serialize it
            result.put("lastModifiedTime", null);
            result.put("lastAccessTime", null);
            result.put("creationTime", null);
            return result;
        }
        throw new IOException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private FileSystem getDefaultFileSystem() {
        return fileSystem;
    }

    public static void main(String[] args) throws Exception {
        JGitFileSystemProvider j = new JGitFileSystemProvider();
        
        Map<String, String> env = new HashMap<String, String>();
        String fromGitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        String userName = "guvnorngtestuser1";
        String password = "test1234";
        
        env.put("fromGitURL", fromGitURL);
        env.put("userName", userName);
        env.put("password", password);
        URI uri = URI.create("jgit:///guvnorng-playground");
        j.newFileSystem(uri, env);
        
        //j.newFileSystem(uri, env);
       
        FileSystem fileSystem = j.getFileSystem(uri);
        
        Repository repository = getRepository("guvnorng-playground");     
/*        
        File source = new File("sometestfiles/testfile.txt");
        System.out.println(source.getAbsolutePath());
        PathModel pathModel = new PathModel("sometestfile", "mortgagesSample/sometestfile9", source.length(), 0, "");
        String commitMessage = "test. pushed from jgit.";
        InputStream inputStream = new FileInputStream(source);
        
        UsernamePasswordCredentialsProvider credential = new UsernamePasswordCredentialsProvider("jervisliu", "uguess");
        JGitUtils.commitAndPush(repository, pathModel, inputStream, commitMessage, credential);
        
        repository = getGuvnorNGRepository();*/
        
        List<PathModel> files = JGitUtils.getFilesInPath(repository, null, null);
        for (PathModel p : files) {
            System.out.println("name: " + p.name);
            System.out.println("path: " + p.path);
            System.out.println("isTree: " + p.isTree());
        }
        
        List<PathModel> files1 = JGitUtils.getFilesInPath(repository, "mortgagesSample", null);
        for (PathModel p : files1) {
            System.out.println(p.name);
            System.out.println(p.path);
            System.out.println("isTree: " + p.isTree());
        }     
        
        String contentA = JGitUtils.getStringContent(repository, null, "mortgagesSample/MortgageModel.model.drl");
        System.out.println(contentA);
    }

    public static Repository getRepository(String repositoryName) throws java.io.IOException {
        // bare repository, ensure .git suffix
        if (!repositoryName.toLowerCase().endsWith(Constants.DOT_GIT_EXT)) {
            repositoryName += Constants.DOT_GIT_EXT;
        }
        return new FileRepository(new File(REPOSITORIES_ROOT_DIR, repositoryName));
    } 
    
    private static void cloneOrFetch(String name, String fromUrl, CredentialsProvider credentialsProvider) throws Exception {
        System.out.print("Fetching " + name + "... ");
        //AwtCredentialsProvider credential = new AwtCredentialsProvider();
        JGitUtils.cloneRepository(new File(REPOSITORIES_ROOT_DIR), name, fromUrl, true, credentialsProvider);
        System.out.println("Fetching done.");
    }
    
    private static void showRemoteBranches(String repositoryName) {
        try {
            FileSettings settings = new FileSettings("my.properties");
            GitBlit.self().configureContext(settings, true);
            RepositoryModel model = GitBlit.self().getRepositoryModel(repositoryName);
            model.showRemoteBranches = true;
            GitBlit.self().updateRepositoryModel(model.name, model, false);
        } catch (GitBlitException g) {
            g.printStackTrace();
        }
    }    
}
