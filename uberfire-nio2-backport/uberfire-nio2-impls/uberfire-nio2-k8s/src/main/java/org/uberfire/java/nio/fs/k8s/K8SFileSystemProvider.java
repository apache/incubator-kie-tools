/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.fs.cloud.CloudClientFactory;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_FSOBJ_CONTENT_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_SCHEME;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceParentDirFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.deleteAndUpdateParentCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getPathByFsObjCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.isDirectory;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.isRoot;

public class K8SFileSystemProvider extends SimpleFileSystemProvider implements CloudClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(K8SFileSystemProvider.class);
    public K8SFileSystemProvider() {
        super(null, OSType.UNIX_LIKE);
        this.fileSystem = new K8SFileSystem(this, K8SFileSystem.UNIX_SEPARATOR_STRING);
    }

    @Override
    public String getScheme() {
        return K8S_FS_SCHEME;
    }

    @Override
    public InputStream newInputStream(final Path path,
                                      final OpenOption... options)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull("path", path);
        checkFileNotExistThenThrow(path, false);
        logger.info("Open InputStream to file [{}]", path);
        return Channels.newInputStream(new K8SFileChannel(toAbsoluteRealPath(path), this));
    }

    @Override
    public OutputStream newOutputStream(final Path path,
                                        final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        Path aPath = toAbsoluteRealPath(path);
        logger.info("Open OutputStream to file [{}]", aPath);
        return Channels.newOutputStream(new K8SFileChannel(aPath, this));
    }

    @Override
    public FileChannel newFileChannel(final Path path,
                                      final Set<? extends OpenOption> options,
                                      final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path,
                                              final Set<? extends OpenOption> options,
                                              final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("path", path);
        return new K8SFileChannel(toAbsoluteRealPath(path), this);
    }

    @Override
    public void createDirectory(final Path dir,
                                final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("dir",dir);
        Path aDir = toAbsoluteRealPath(dir);
        Optional<ConfigMap> directoryCm = executeCloudFunction(client -> getFsObjCM(client, aDir), KubernetesClient.class);
        if (directoryCm.isPresent()) {
            throw new FileAlreadyExistsException(aDir.toString());
        }
        
        executeCloudFunction(client -> createOrReplaceFSCM(client, 
                                                           aDir,
                                                           isRoot(aDir) ? Optional.empty()
                                                                       : createOrReplaceParentDirFSCM(client, aDir, 0L, false),
                                                           Collections.emptyMap(),
                                                           true), 
                             KubernetesClient.class);
    }

    @Override
    protected Path[] getDirectoryContent(final Path dir) {
        checkNotNull("dir", dir);
        Path aDir = toAbsoluteRealPath(dir);
        if (isRoot(aDir) &&
            !executeCloudFunction(client -> getFsObjCM(client, aDir), KubernetesClient.class).isPresent()) {
            initRoot();
        }
        ConfigMap dirCM = executeCloudFunction(client -> getFsObjCM(client, aDir), KubernetesClient.class)
                .orElseThrow(() -> new NotDirectoryException(aDir.toString()));
        if (dirCM.getData() == null || dirCM.getData().isEmpty()) {
            return new Path[0];
        }
        
        String separator = aDir.getFileSystem().getSeparator();
        String dirPathString = getPathByFsObjCM((K8SFileSystem)fileSystem, dirCM).toString();
        return dirCM.getData()
                    .keySet()
                    .stream()
                    .map(fileName -> GeneralPathImpl.create(aDir.getFileSystem(), 
                                                           (dirPathString.endsWith(separator) ? 
                                                            dirPathString :
                                                            dirPathString.concat(separator)).concat(fileName), 
                                                            false))
                    .toArray(Path[]::new);
    }
    
    private synchronized void initRoot() {
        Path root = this.fileSystem.getPath(K8SFileSystem.UNIX_SEPARATOR_STRING);
        this.createDirectory(root);
        logger.info("Root directory created.");
    }

    @Override
    public void delete(final Path path,
                       final DeleteOption... options) throws NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path);
        checkFileNotExistThenThrow(path, false);
        deleteIfExists(toAbsoluteRealPath(path), options);
    }

    @Override
    public boolean deleteIfExists(final Path path,
                                  final DeleteOption... options)
            throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path);
        Path aPath = toAbsoluteRealPath(path);
        synchronized (this) {
            try {
                return executeCloudFunction(client -> deleteAndUpdateParentCM(client, aPath), 
                                            KubernetesClient.class).get();
            } finally {
                toGeneralPathImpl(aPath).clearCache();
            }
        }
    }
    
    @Override
    public boolean isHidden(final Path path) throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        checkFileNotExistThenThrow(path, false);
        return path.getFileName().toString().startsWith(K8SFileSystemConstants.K8S_FS_HIDDEN_FILE_INDICATOR);
    }

    @Override
    public void checkAccess(final Path path,
                            AccessMode... modes)
            throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("modes", modes);
        checkFileNotExistThenThrow(path, false);
        for (final AccessMode mode : modes) {
            checkNotNull("mode", mode);
            if (mode == AccessMode.EXECUTE) {
                throw new AccessDeniedException(toAbsoluteRealPath(path).toString());
            }
        }
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException, SecurityException {
        checkNotNull("path", path);
        return new K8SFileStore(toAbsoluteRealPath(path));
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
                                                            final Class<A> type,
                                                            final LinkOption... options)
            throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("type", type);
        checkFileNotExistThenThrow(path, false);
        if (type == BasicFileAttributesImpl.class || type == BasicFileAttributes.class) {
            final K8SBasicFileAttributeView view = getFileAttributeView(toAbsoluteRealPath(path),
                                                                        K8SBasicFileAttributeView.class,
                                                                        options);
            return view.readAttributes();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <V extends FileAttributeView> V createFileAttributeView(final GeneralPathImpl path, 
                                                                      final Class<V> type) {
        if (AbstractBasicFileAttributeView.class.isAssignableFrom(type)) {
            final V newView = (V) new K8SBasicFileAttributeView(path, this);
            path.addAttrView(newView);
            return newView;
        } else {
            return null;
        }
    }
    
    @Override
    public void copy(final Path source,
                     final Path target,
                     final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("source", source);
        checkNotNull("target", target);
        checkFileExistsThenThrow(target);
        Path aSource = toAbsoluteRealPath(source);
        Path aTarget = toAbsoluteRealPath(target);

        Optional<ConfigMap> srcCMOpt = executeCloudFunction(
            client -> getFsObjCM(client, aSource), KubernetesClient.class);
        checkCondition("source must exist", srcCMOpt.isPresent());

        ConfigMap srcCM = srcCMOpt.orElseThrow(IllegalArgumentException::new);
        if (isDirectory(srcCM)) {
            throw new UnsupportedOperationException(srcCM.getMetadata().getName() + "is a directory.");
        }
        
        String content = srcCM.getData().getOrDefault(CFG_MAP_FSOBJ_CONTENT_KEY, "");
        long size = Long.parseLong(srcCM.getMetadata().getAnnotations().getOrDefault(CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY, "0"));
        executeCloudFunction(client -> createOrReplaceFSCM(client, 
                                                           aTarget,
                                                           createOrReplaceParentDirFSCM(client, aTarget, size, false),
                                                           Collections.singletonMap(CFG_MAP_FSOBJ_CONTENT_KEY, content),
                                                           false), 
                             KubernetesClient.class);
    }

    @Override
    public void move(final Path source,
                     final Path target,
                     final CopyOption... options)
            throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        Path aSource = toAbsoluteRealPath(source);
        Path aTarget = toAbsoluteRealPath(target);
        try {
            copy(aSource, aTarget);
        } catch (Exception e) {
            try {
                delete(aTarget);
            } catch (NoSuchFileException nsfe) {
                throw new IOException("Moving file failed.", e);
            } catch (Exception exp) {
                throw new IOException("Moving file failed due to these errors: Copy Source Exception [" + 
                        e.getMessage() + "]; Delete Target Exception [" + exp.getMessage() + "].");
            } 
        } 
        
        try {
            delete(aSource);
        } catch (Exception e) {
            throw new IOException("Moving file failed with clean Source Exception [" + e.getMessage() + "], " +
                    "which will leave file system in an inconsistent state.");
        }
    }
    
    @Override
    protected void checkFileNotExistThenThrow(final Path path, final boolean isLink) {
        Path aPath = toAbsoluteRealPath(path);
        executeCloudFunction(client -> getFsObjCM(client, aPath), KubernetesClient.class)
            .orElseThrow(() -> {
                logger.info("File not found [{}]", aPath.toUri().toString());
                return new NoSuchFileException(aPath.toUri().toString());
            });
    }

    @Override
    protected void checkFileExistsThenThrow(final Path path) {
        Path aPath = toAbsoluteRealPath(path);
        if (executeCloudFunction(client -> getFsObjCM(client, aPath), KubernetesClient.class).isPresent()) {
            throw new FileAlreadyExistsException(aPath.toString());
        }
    }

    protected Path toAbsoluteRealPath(final Path path) {
        if (path.isAbsolute()) {
            if (path.getParent() == null) {
                return path; // Root
            } else if (path.getParent().toString().contains(".")) {
                fileSystem.getPath(path.toRealPath().toString());
            } else {
                return path; // RealPath
            }
        }
        return fileSystem.getPath(path.toAbsolutePath().toRealPath().toString());
    }
}
