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
import java.io.FilenameFilter;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kie.commons.data.Pair;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
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
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.base.FlexibleFileAttributeView;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

import static org.eclipse.jgit.api.ListBranchCommand.ListMode.*;
import static org.eclipse.jgit.lib.Constants.*;
import static org.kie.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.*;

public class JGitFileSystemProvider implements FileSystemProvider {

    public static final String GIT_DEFAULT_REMOTE_NAME = DEFAULT_REMOTE_NAME;
    private static final String SCHEME = "git";

    public static final String REPOSITORIES_ROOT_DIR = ".vfsgit";
    public static File FILE_REPOSITORIES_ROOT;

    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String INIT = "init";

    public static final int SCHEME_SIZE = (SCHEME + "://").length();
    public static final int DEFAULT_SCHEME_SIZE = ("default://").length();

    private final Map<String, JGitFileSystem> fileSystems = new ConcurrentHashMap<String, JGitFileSystem>();

    private boolean isDefault;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        final String value = System.getProperty("org.uberfire.vfs.git.dir");
        if (value == null || value.trim().isEmpty()) {
            FILE_REPOSITORIES_ROOT = new File(REPOSITORIES_ROOT_DIR);
        } else {
            FILE_REPOSITORIES_ROOT = new File(value.trim(), REPOSITORIES_ROOT_DIR);
        }
    }

    public JGitFileSystemProvider() {
        final String[] repos = FILE_REPOSITORIES_ROOT.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, String name) {
                return name.endsWith(DOT_GIT_EXT);
            }
        });

        if (repos != null) {
            for (final String repo : repos) {
                final File repoDir = new File(FILE_REPOSITORIES_ROOT, repo);
                if (repoDir.isDirectory()) {
                    final String name = repoDir.getName().substring(0, repoDir.getName().indexOf(DOT_GIT_EXT));
                    final JGitFileSystem fs = new JGitFileSystem(this, newRepository(repoDir), name, ALL);
                    fileSystems.put(name, fs);
                }
            }
        }
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
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env)
            throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        checkNotNull("uri", uri);
        checkCondition("uri scheme not supported", uri.getScheme().equals(getScheme()) || uri.getScheme().equals("default"));
        checkURI("uri", uri);
        checkNotNull("env", env);

        final String name = extractRepoName(uri);

        if (fileSystems.containsKey(name)) {
            throw new FileSystemAlreadyExistsException();
        }

        final Git git;
        final File repoDest = new File(FILE_REPOSITORIES_ROOT, name + DOT_GIT_EXT);
        final ListBranchCommand.ListMode listMode;
        if (env.containsKey(GIT_DEFAULT_REMOTE_NAME)) {
            final String originURI = env.get(GIT_DEFAULT_REMOTE_NAME).toString();
            git = cloneRepository(repoDest, originURI, buildCredential(env));
            listMode = ALL;
        } else {
            git = newRepository(repoDest);
            listMode = null;
        }

        final JGitFileSystem fs = new JGitFileSystem(this, git, name, listMode);
        fileSystems.put(name, fs);

        if (!env.containsKey(GIT_DEFAULT_REMOTE_NAME) && env.containsKey(INIT) && env.get(INIT).equals(Boolean.TRUE)) {
            try {
                final URI initURI = URI.create(getScheme() + "://master@" + name + "/readme.md");
                final JGitOp op = setupOp(env);
                final OutputStream stream = newOutputStream(getPath(initURI), op);
                final String init = "Repository Init Content\n" +
                        "=======================\n" +
                        "\n" +
                        "Your project description here.";
                stream.write(init.getBytes());
                stream.close();
            } catch (final Exception e) {
            }
        }

        return fs;
    }

    private JGitOp setupOp(final Map<String, ?> env) {
        return null;
    }

    @Override
    public FileSystem getFileSystem(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull("uri", uri);
        checkCondition("uri scheme not supported", uri.getScheme().equals(getScheme()) || uri.getScheme().equals("default"));
        checkURI("uri", uri);

        final JGitFileSystem fileSystem = fileSystems.get(extractRepoName(uri));

        if (fileSystem == null) {
            throw new FileSystemNotFoundException();
        }

        return fileSystem;
    }

    @Override
    public Path getPath(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull("uri", uri);
        checkCondition("uri scheme not supported", uri.getScheme().equals(getScheme()) || uri.getScheme().equals("default"));
        checkURI("uri", uri);

        final JGitFileSystem fileSystem = fileSystems.get(extractRepoName(uri));

        if (fileSystem == null) {
            throw new FileSystemNotFoundException();
        }

        try {
            return JGitPathImpl.create(fileSystem, URIUtil.decode(extractPath(uri)), extractHost(uri), false);
        } catch (final URIException e) {
            return null;
        }
    }

    @Override
    public InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        return resolveInputStream(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());
    }

    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> result = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());

        if (result.getK1().equals(PathType.DIRECTORY)) {
            throw new IOException();
        }

        try {
            final File file = File.createTempFile("gitz", "woot");
            return new FilterOutputStream(new FileOutputStream(file)) {
                public void close() throws java.io.IOException {
                    super.close();
                    String name = null;
                    String email = null;
                    String message = null;
                    TimeZone timeZone = null;
                    Date when = null;

                    if (options != null && options.length > 0) {
                        for (final OpenOption option : options) {
                            if (option instanceof JGitOp) {
                                final JGitOp op = (JGitOp) option;
                                name = op.name;
                                email = op.email;
                                message = op.message;
                                timeZone = op.timeZone;
                                when = op.when;
                                break;
                            }
                        }
                    }

                    commit(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath(), file, name, email, message, timeZone, when);
                }
            };
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public FileChannel newFileChannel(final Path path, Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> options, final ExecutorService executor, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path path, final DirectoryStream.Filter<Path> filter)
            throws NotDirectoryException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> result = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());

        if (!result.getK1().equals(PathType.DIRECTORY)) {
            throw new NotDirectoryException(path.toString());
        }

        final List<JGitPathInfo> pathContent = listPathContent(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());

        return new DirectoryStream<Path>() {
            @Override
            public void close() throws IOException {
            }

            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {
                    private Iterator<JGitPathInfo> contentIterator = pathContent.iterator();

                    @Override
                    public boolean hasNext() {
                        return contentIterator.hasNext();
                    }

                    @Override
                    public Path next() {
                        final JGitPathInfo content = contentIterator.next();
                        return JGitPathImpl.create(gPath.getFileSystem(), "/" + content.getPath(), gPath.getHost(), content.getObjectId(), gPath.isRealPath());
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
    public void createDirectory(final Path path, final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> result = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());

        if (!result.getK1().equals(NOT_FOUND)) {
            throw new FileAlreadyExistsException(path.toString());
        }

        try {
            final OutputStream outputStream = newOutputStream(path.resolve(".gitignore"));
            outputStream.write("# empty\n".getBytes());
            outputStream.close();
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createLink(final Path link, final Path existing)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final Path path)
            throws DirectoryNotEmptyException, NoSuchFileException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        if (isBranch(gPath)) {
            deleteBranch(gPath);
            return;
        }

        deleteAsset(gPath);
    }

    public void deleteAsset(final JGitPathImpl path) {
        final Pair<PathType, ObjectId> result = checkPath(path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath());

        if (result.getK1().equals(PathType.DIRECTORY)) {
            throw new DirectoryNotEmptyException(path.toString());
        }

        if (result.getK1().equals(NOT_FOUND)) {
            throw new NoSuchFileException(path.toString());
        }

        JGitUtil.delete(path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath(), null, null, "delete {" + path.getPath() + "}", null, null);
    }

    public void deleteBranch(final JGitPathImpl path) {
        final Ref branch = getBranch(path.getFileSystem().gitRepo(), path.getRefTree());

        if (branch == null) {
            throw new NoSuchFileException(path.toString());
        }

        JGitUtil.deleteBranch(path.getFileSystem().gitRepo(), branch);
    }

    @Override
    public boolean deleteIfExists(final Path path)
            throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        if (isBranch(gPath)) {
            return deleteBranchIfExists(gPath);
        }

        return deleteAssetIfExists(gPath);
    }

    public boolean deleteBranchIfExists(final JGitPathImpl path) {
        final Ref branch = getBranch(path.getFileSystem().gitRepo(), path.getRefTree());

        if (branch == null) {
            return false;
        }

        JGitUtil.deleteBranch(path.getFileSystem().gitRepo(), branch);
        return true;
    }

    public boolean deleteAssetIfExists(final JGitPathImpl path) {
        final Pair<PathType, ObjectId> result = checkPath(path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath());

        if (result.getK1().equals(PathType.DIRECTORY)) {
            throw new DirectoryNotEmptyException(path.toString());
        }

        if (result.getK1().equals(NOT_FOUND)) {
            return false;
        }

        JGitUtil.delete(path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath(), null, null, "delete {" + path.getPath() + "}", null, null);
        return true;
    }

    @Override
    public Path readSymbolicLink(final Path link)
            throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        checkNotNull("link", link);
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(final Path source, final Path target, final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("source", source);
        checkNotNull("target", target);

        final JGitPathImpl gSource = toPathImpl(source);
        final JGitPathImpl gTarget = toPathImpl(target);

        final boolean isSourceBranch = isBranch(gSource);
        final boolean isTargetBranch = isBranch(gTarget);

        if (isSourceBranch && isTargetBranch) {
            copyBranch(gSource, gTarget);
            return;
        }
        copyAsset(gSource, gTarget);
    }

    private void copyBranch(final JGitPathImpl source, final JGitPathImpl target) {
        checkCondition("source and taget should have same setup", !hasSameFileSystem(source, target));
        if (existsBranch(target)) {
            throw new FileAlreadyExistsException(target.toString());
        }
        if (!existsBranch(source)) {
            throw new NoSuchFileException(target.toString());
        }
        createBranch(source, target);
    }

    private void copyAsset(final JGitPathImpl source, final JGitPathImpl target) {
        final Pair<PathType, ObjectId> sourceResult = checkPath(source.getFileSystem().gitRepo(), source.getRefTree(), source.getPath());
        final Pair<PathType, ObjectId> targetResult = checkPath(target.getFileSystem().gitRepo(), target.getRefTree(), target.getPath());

        if (!isRoot(target) && targetResult.getK1() != NOT_FOUND) {
            throw new FileAlreadyExistsException(target.toString());
        }

        if (sourceResult.getK1() == NOT_FOUND) {
            throw new NoSuchFileException(target.toString());
        }

        if (sourceResult.getK1() == DIRECTORY) {
            copyDirectory(source, target);
            return;
        }

        copyFile(source, target);
    }

    private void copyDirectory(final JGitPathImpl source, final JGitPathImpl target) {
        final List<JGitPathImpl> directories = new ArrayList<JGitPathImpl>();
        for (final Path path : newDirectoryStream(source, null)) {
            final JGitPathImpl gPath = toPathImpl(path);
            final Pair<PathType, ObjectId> pathResult = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());
            if (pathResult.getK1() == DIRECTORY) {
                directories.add(gPath);
                continue;
            }
            final JGitPathImpl gTarget = composePath(target, (JGitPathImpl) gPath.getFileName());

            copyFile(gPath, gTarget);
        }
        for (final JGitPathImpl directory : directories) {
            createDirectory(composePath(target, (JGitPathImpl) directory.getFileName()));
        }
    }

    private JGitPathImpl composePath(final JGitPathImpl directory, final JGitPathImpl fileName) {
        if (directory.getPath().endsWith("/")) {
            return toPathImpl(getPath(URI.create(directory.toUri().toString() + fileName.toString(false))));
        }
        return toPathImpl(getPath(URI.create(directory.toUri().toString() + "/" + fileName.toString(false))));
    }

    private void copyFile(final JGitPathImpl source, final JGitPathImpl target) {

        final InputStream in = newInputStream(source);
        final OutputStream out = newOutputStream(target);

        try {
            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            try {
                out.close();
            } catch (java.io.IOException e) {
                throw new IOException(e);
            } finally {
                try {
                    in.close();
                } catch (java.io.IOException e) {
                    throw new IOException(e);
                }
            }
        }
    }

    private void createBranch(final JGitPathImpl source, final JGitPathImpl target) {
        JGitUtil.createBranch(source.getFileSystem().gitRepo(), source.getRefTree(), target.getRefTree());
    }

    private boolean existsBranch(final JGitPathImpl path) {
        return hasBranch(path.getFileSystem().gitRepo(), path.getRefTree());
    }

    private boolean isBranch(final JGitPathImpl path) {
        return path.getPath().length() == 1 && path.getPath().equals("/");
    }

    private boolean isRoot(final JGitPathImpl path) {
        return path.getPath().length() == 1 && path.getPath().equals("/");
    }

    private boolean hasSameFileSystem(final JGitPathImpl source, final JGitPathImpl target) {
        return source.getFileSystem().equals(target);
    }

    @Override
    public void move(final Path source, final Path target, final CopyOption... options)
            throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        checkNotNull("source", source);
        checkNotNull("target", target);

        throw new AtomicMoveNotSupportedException(source.toString(), source.toString(), "atomic move not supported.");
    }

    @Override
    public boolean isSameFile(final Path pathA, final Path pathB)
            throws IOException, SecurityException {
        checkNotNull("pathA", pathA);
        checkNotNull("pathB", pathB);

        final JGitPathImpl gPathA = toPathImpl(pathA);
        final JGitPathImpl gPathB = toPathImpl(pathB);

        final Pair<PathType, ObjectId> resultA = checkPath(gPathA.getFileSystem().gitRepo(), gPathA.getRefTree(), gPathA.getPath());
        final Pair<PathType, ObjectId> resultB = checkPath(gPathB.getFileSystem().gitRepo(), gPathB.getRefTree(), gPathB.getPath());

        if (resultA.getK1() == PathType.FILE && resultA.getK2().equals(resultB.getK2())) {
            return true;
        }

        return pathA.equals(pathB);
    }

    @Override
    public boolean isHidden(final Path path)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        if (gPath.getFileName() == null) {
            return false;
        }

        return toPathImpl(path.getFileName()).toString(false).startsWith(".");
    }

    @Override
    public FileStore getFileStore(final Path path)
            throws IOException, SecurityException {
        checkNotNull("path", path);

        return new JGitFileStore(toPathImpl(path).getFileSystem().gitRepo().getRepository());
    }

    @Override
    public void checkAccess(final Path path, final AccessMode... modes)
            throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        checkNotNull("path", path);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> result = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());

        if (result.getK1().equals(NOT_FOUND)) {
            throw new NoSuchFileException(path.toString());
        }
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options)
            throws NoSuchFileException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> pathResult = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());
        if (pathResult.getK1().equals(NOT_FOUND)) {
            throw new NoSuchFileException(path.toString());
        }

        if (type == BasicFileAttributeView.class || type == JGitFileAttributeView.class) {
            return (V) new JGitFileAttributeView(gPath);
        }

        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options)
            throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        final JGitPathImpl gPath = toPathImpl(path);

        final Pair<PathType, ObjectId> pathResult = checkPath(gPath.getFileSystem().gitRepo(), gPath.getRefTree(), gPath.getPath());
        if (pathResult.getK1().equals(NOT_FOUND)) {
            throw new NoSuchFileException(path.toString());
        }

        if (type == BasicFileAttributes.class || type == JGitFileAttributes.class) {
            final JGitFileAttributeView view = getFileAttributeView(path, JGitFileAttributeView.class, options);
            return (A) view.readAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotEmpty("attributes", attributes);

        final String[] s = split(attributes);
        if (s[0].length() == 0) {
            throw new IllegalArgumentException(attributes);
        }

        final FlexibleFileAttributeView view = getFileAttributeView(toPathImpl(path), s[0], options);
        if (view == null) {
            throw new UnsupportedOperationException("View '" + s[0] + "' not available");
        }

        return view.readAttributes(s[1].split(","));
    }

    @Override
    public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotEmpty("attributes", attribute);

        final String[] s = split(attribute);
        if (s[0].length() == 0) {
            throw new IllegalArgumentException(attribute);
        }
        final FlexibleFileAttributeView view = getFileAttributeView(toPathImpl(path), s[0], options);
        if (view == null) {
            throw new UnsupportedOperationException("View '" + s[0] + "' not available");
        }

        view.setAttribute(s[1], value);
    }

    private void checkURI(final String paramName, final URI uri)
            throws IllegalArgumentException {
        checkNotNull("uri", uri);

        if (uri.getAuthority() == null || uri.getAuthority().isEmpty()) {
            throw new IllegalArgumentException("Parameter named '" + paramName + "' is invalid, missing host repository!");
        }

        int atIndex = uri.getPath().indexOf("@");
        if (atIndex != -1 && !uri.getAuthority().contains("@")) {
            if (uri.getPath().indexOf("/", atIndex) == -1) {
                throw new IllegalArgumentException("Parameter named '" + paramName + "' is invalid, missing host repository!");
            }
        }

    }

    private String extractHost(final URI uri) {
        checkNotNull("uri", uri);

        int atIndex = uri.getPath().indexOf("@");
        if (atIndex != -1 && !uri.getAuthority().contains("@")) {
            return uri.getAuthority() + uri.getPath().substring(0, uri.getPath().indexOf("/", atIndex));
        }

        return uri.getAuthority();
    }

    private String extractRepoName(final URI uri) {
        checkNotNull("uri", uri);

        final String host = extractHost(uri);

        int index = host.indexOf('@');
        if (index != -1) {
            return host.substring(index + 1);
        }

        return host;
    }

    private String extractPath(final URI uri) {
        checkNotNull("uri", uri);

        final String host = extractHost(uri);

        final String path = uri.toString().substring(getSchemeSize(uri) + host.length());

        if (path.startsWith("/:")) {
            return path.substring(2);
        }

        return path;
    }

    private CredentialsProvider buildCredential(final Map<String, ?> env) {
        if (env.containsKey(USER_NAME)) {
            if (env.containsKey(PASSWORD)) {
                new UsernamePasswordCredentialsProvider(env.get(USER_NAME).toString(), env.get(PASSWORD).toString());
            }
            new UsernamePasswordCredentialsProvider(env.get(USER_NAME).toString(), "");
        }
        return UsernamePasswordCredentialsProvider.getDefault();
    }

    private JGitPathImpl toPathImpl(final Path path) {
        if (path instanceof JGitPathImpl) {
            return (JGitPathImpl) path;
        }
        throw new IllegalArgumentException("Path not supported by current provider.");
    }

    private FlexibleFileAttributeView getFileAttributeView(final JGitPathImpl path, final String name, final LinkOption... options) {
        if (name.equals("basic")) {
            return new JGitFileAttributeView(path);
        }
        return null;
    }

    private String[] split(final String attribute) {
        final String[] s = new String[2];
        final int pos = attribute.indexOf(':');
        if (pos == -1) {
            s[0] = "basic";
            s[1] = attribute;
        } else {
            s[0] = attribute.substring(0, pos);
            s[1] = (pos == attribute.length()) ? "" : attribute.substring(pos + 1);
        }
        return s;
    }

    private int getSchemeSize(final URI uri) {
        if (uri.getScheme().equals(SCHEME)) {
            return SCHEME_SIZE;
        }
        return DEFAULT_SCHEME_SIZE;
    }
}
