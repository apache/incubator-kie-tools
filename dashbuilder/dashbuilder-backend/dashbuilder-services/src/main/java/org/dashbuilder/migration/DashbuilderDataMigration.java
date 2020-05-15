/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.migration;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.api.FileSystemUtils;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.FileSystemLockManager;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mvp.Command;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
@Startup
public class DashbuilderDataMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashbuilderDataMigration.class);

    private IOService ioService;
    private FileSystem datasetsFS;
    private FileSystem pluginsFS;
    private FileSystem perspectivesFS;
    private FileSystem navigationFS;

    public DashbuilderDataMigration() {

    }

    @Inject
    public DashbuilderDataMigration(
            final @Named("ioStrategy") IOService ioService,
            final @Named("datasetsFS") FileSystem datasetsFS,
            final @Named("pluginsFS") FileSystem pluginsFS,
            final @Named("perspectivesFS") FileSystem perspectivesFS,
            final @Named("navigationFS") FileSystem navigationFS) {

        this.ioService = ioService;
        this.datasetsFS = datasetsFS;
        this.pluginsFS = pluginsFS;
        this.perspectivesFS = perspectivesFS;
        this.navigationFS = navigationFS;
    }

    @PostConstruct
    protected void init() {
        if (this.isMigrationEnabled()) {
            runWithLock(() -> {
                migrateDatasets();
                migratePerspectives();
                migrateNavigation();
            });
        }
    }

    protected boolean isMigrationEnabled() {
        return FileSystemUtils.isGitDefaultFileSystem();
    }

    private void migrateDatasets() {
        FileSystem oldDatasetsFS = lookupFileSystem(SpacesAPI.DEFAULT_SPACE, "datasets");
        this.migrateDatasets(oldDatasetsFS, datasetsFS);
        cleanupFileSystem(oldDatasetsFS);
    }

    private FileSystem lookupFileSystem(Space space, String name) {
        FileSystem fs;

        URI uri = new SpacesAPIImpl().resolveFileSystemURI(
                SpacesAPI.Scheme.DEFAULT,
                space,
                name);

        HashMap<String, Object> env = new HashMap<>();
        env.put("init", Boolean.TRUE);
        env.put("internal", Boolean.TRUE);

        try {
            fs = ioService.newFileSystem(uri, env);
        } catch (FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(uri);
        }

        return fs;
    }

    private void cleanupFileSystem(FileSystem fs) {
        Path root = getRoot(fs);
        if (root != null && fs instanceof JGitFileSystem) {
            try {
                Path fsPath = fs.getPath("");
                Files.delete(fsPath);
            } catch (Exception e) {
                LOGGER.error("Failed to remove the datasets git repository.");
                LOGGER.debug("Error during dashbuilder migration", e);
            }
        }
    }

    private void migratePerspectives() {
        this.migratePerspectives(pluginsFS, perspectivesFS);
    }

    private void migrateNavigation() {
        this.migrateNavigation(pluginsFS, navigationFS);
    }

    public void migrateDatasets(FileSystem sourceFS, FileSystem targetFS) {
        LOGGER.info("attempt to migrate datasets");
        migrate(sourceFS,
                targetFS,
                path -> !path.getFileName().toString().equals("readme.md"));
    }

    public void migratePerspectives(FileSystem sourceFS, FileSystem targetFS) {
        LOGGER.info("attempt to migrate perspectives");
        migrate(sourceFS,
                targetFS,
                path -> path.getFileName().toString().startsWith("perspective_layout"));
    }

    public void migrateNavigation(FileSystem sourceFS, FileSystem targetFS) {
        LOGGER.info("attempt to migrate navigation");
        migrate(sourceFS,
                targetFS,
                path -> path.getFileName().toString().equals("navtree.json"));
    }

    private void migrate(FileSystem sourceFS, FileSystem targetFS, Predicate<Path> predicate) {
        Path sourceRoot = getRoot(sourceFS);
        Path targetRoot = getRoot(targetFS);

        if (sourceRoot == null) {
            LOGGER.info("source does not exists");
            return;
        }

        if (targetRoot == null) {
            LOGGER.error("target does not exists");
            return;
        }

        LOGGER.info("moving from " + sourceFS.getName() + " to " + targetFS.getName());

        Files.walkFileTree(sourceRoot, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) {
                checkNotNull("file", path);
                checkNotNull("attrs", attrs);

                Path targetPath = targetRoot.resolve(path.toString());

                if (!predicate.test(path)) {
                    LOGGER.debug("skip file " + path.toString());
                } else if (ioService.exists(targetPath)) {
                    LOGGER.debug("file " + path.toString() + " already exists on target");
                    Files.delete(path);
                } else {
                    LOGGER.debug("moving file " + path.toString());
                    Files.copy(path, targetPath);
                    Files.delete(path);
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    private Path getRoot(FileSystem fileSystem) {
        try {
            return fileSystem.getRootDirectories().iterator().next();
        } catch (Exception e) {
            LOGGER.debug("could not get filesystem root", e);
            return null;
        }
    }

    protected void runWithLock(Command command) {
        String lockName = "data-migration.lock";
        String markerName = "data-migration.done";

        TimeUnit lastAccessTimeUnit = TimeUnit.SECONDS;
        int lastAccessThreshold = 1;

        // get .niogit/dashbuilder path
        File dir = navigationFS.getPath("/")
                .toFile()
                .getParentFile();

        File marker = new File(dir, markerName);

        FileSystemLock lock = FileSystemLockManager
                .getInstance()
                .getFileSystemLock(
                        dir,
                        lockName,
                        lastAccessTimeUnit,
                        lastAccessThreshold);

        try {
            lock.lock();

            if (!marker.exists()) {
                marker.createNewFile();
                command.execute();
            }
        } catch (java.io.IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
