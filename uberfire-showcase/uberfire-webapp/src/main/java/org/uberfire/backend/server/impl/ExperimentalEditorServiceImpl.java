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

package org.uberfire.backend.server.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.shared.experimental.ExperimentalAssetRemoved;
import org.uberfire.shared.experimental.ExperimentalEditorService;
import org.uberfire.spaces.SpacesAPI;

@Service
@ApplicationScoped
public class ExperimentalEditorServiceImpl implements ExperimentalEditorService {

    public static final String STORAGE_PATH = "content";

    public static final String SEPARATOR = "/";

    public static final String EXTENSION = ".exp";

    private SpacesAPI spaces;

    private IOService ioService;

    private FileSystem fileSystem;

    private Event<ExperimentalAssetRemoved> assetRemovedEvent;

    ExperimentalEditorServiceImpl() {
        // Zero argument constructor for CDI proxies
    }

    @Inject
    public ExperimentalEditorServiceImpl(final SpacesAPI spaces, @Named("configIO") final IOService ioService, final Event<ExperimentalAssetRemoved> assetRemovedEvent) {
        this.spaces = spaces;
        this.ioService = ioService;
        this.assetRemovedEvent = assetRemovedEvent;
    }

    @PostConstruct
    public void init() {
        initializeFileSystem();
    }

    @Override
    public List<Path> listAll() {

        org.uberfire.java.nio.file.Path path = fileSystem.getPath(STORAGE_PATH);

        final List<Path> result = new ArrayList<>();

        ioService.newDirectoryStream(path, entry -> entry.getFileName().toString().endsWith(EXTENSION)).forEach(assetPath -> result.add(Paths.convert(assetPath)));

        return result;
    }

    @Override
    public Path create(String assetName) {

        String path = STORAGE_PATH + SEPARATOR + assetName + EXTENSION;

        org.uberfire.java.nio.file.Path fsPath = fileSystem.getPath(path);

        if (!ioService.exists(fsPath)) {
            ioService.write(fsPath, "");
            return Paths.convert(fsPath);
        }

        throw new FileAlreadyExistsException(fsPath.toString());
    }

    @Override
    public String load(Path path) {

        org.uberfire.java.nio.file.Path fsPath = Paths.convert(path);

        if (ioService.exists(fsPath)) {
            return ioService.readAllString(fsPath);
        }

        return null;
    }

    @Override
    public void save(Path path, String content) {
        org.uberfire.java.nio.file.Path fsPath = Paths.convert(path);

        if (ioService.exists(fsPath)) {
            ioService.write(fsPath, content);
        }
    }

    @Override
    public void delete(Path path, String comment) {
        final org.uberfire.java.nio.file.Path fsPath = Paths.convert(path);

        try {
            ioService.startBatch(fsPath.getFileSystem());

            ioService.delete(Paths.convert(path));

            assetRemovedEvent.fire(new ExperimentalAssetRemoved(path));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
    }

    protected void initializeFileSystem() {
        final URI fileSystemURI = spaces.resolveFileSystemURI(SpacesAPI.Scheme.DEFAULT, SpacesAPI.DEFAULT_SPACE, "experimental");

        try {
            Map<String, Object> options = new HashMap<>();

            options.put("init", Boolean.TRUE);
            options.put("internal", Boolean.TRUE);

            fileSystem = ioService.newFileSystem(fileSystemURI, options);
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(fileSystemURI);
        }
    }
}
