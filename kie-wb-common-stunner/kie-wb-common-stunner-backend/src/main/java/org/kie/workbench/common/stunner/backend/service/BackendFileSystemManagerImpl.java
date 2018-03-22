/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.service;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

@Startup(value = StartupType.EAGER)
@ApplicationScoped
public class BackendFileSystemManagerImpl {

    public static final String VFS_PRO = "default";
    public static final String VFS_ROOT = "stunner";
    public static final String VFS_ROOT_PATH = VFS_PRO + "://" + VFS_ROOT;

    private final BackendFileSystemManager backendFileSystemManager;
    private final IOService ioService;
    private FileSystem fileSystem;
    private Path root;

    @PostConstruct
    public void init() {
        initFileSystem();
    }

    // CDI proxy.
    protected BackendFileSystemManagerImpl() {
        this(null,
             null);
    }

    @Inject
    public BackendFileSystemManagerImpl(final BackendFileSystemManager backendFileSystemManager,
                                        final @Named("ioStrategy") IOService ioService) {
        this.backendFileSystemManager = backendFileSystemManager;
        this.ioService = ioService;
    }

    public String getPathRelativeToApp(final String path) {
        return backendFileSystemManager.getPathRelativeToApp(path);
    }

    public void findAndDeployFiles(final File directory,
                                   final org.uberfire.java.nio.file.Path targetPath) {
        backendFileSystemManager.findAndDeployFiles(directory,
                                                    targetPath);
    }

    public void findAndDeployFiles(final File directory,
                                   final FilenameFilter filter,
                                   final org.uberfire.java.nio.file.Path targetPath) {
        backendFileSystemManager.findAndDeployFiles(directory,
                                                    filter,
                                                    targetPath);
    }

    public IOService getIoService() {
        return ioService;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public Path getRootPath() {
        return root;
    }

    private void initFileSystem() {
        try {
            fileSystem = ioService.newFileSystem(URI.create(VFS_ROOT_PATH),
                                                 new HashMap<String, Object>() {{
                                                     put("init",
                                                         Boolean.TRUE);
                                                     put("internal",
                                                         Boolean.TRUE);
                                                 }});
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(URI.create(VFS_ROOT_PATH));
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }
}
