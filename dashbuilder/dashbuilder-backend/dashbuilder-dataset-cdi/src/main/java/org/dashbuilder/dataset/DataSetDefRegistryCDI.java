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
package org.dashbuilder.dataset;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.config.Config;
import org.dashbuilder.dataprovider.DataSetProviderRegistryCDI;
import org.dashbuilder.dataprovider.csv.CSVFileStorage;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.exception.ExceptionManager;
import org.dashbuilder.scheduler.SchedulerCDI;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

/**
 * Data set definition registry implementation which stores data sets under GIT
 * <p>It's provided as an extension to the default in-memory based registry and it's
 * also the default CDI implementation available.</p>
 */
@ApplicationScoped
public class DataSetDefRegistryCDI extends DataSetDefRegistryImpl implements CSVFileStorage {

    public static final String DATASET_EXT = ".dset";
    public static final String CSV_EXT = ".csv";

    protected int maxCsvLength;
    protected IOService ioService;
    protected ExceptionManager exceptionManager;
    protected UUIDGenerator uuidGenerator;
    protected Event<DataSetDefModifiedEvent> dataSetDefModifiedEvent;
    protected Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    protected Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent;
    protected Event<DataSetStaleEvent> dataSetStaleEvent;

    protected FileSystem fileSystem;
    protected Path root;

    public DataSetDefRegistryCDI() {
        super();
    }

    @Inject
    public DataSetDefRegistryCDI(@Config("10485760" /* 10 Mb */) int maxCsvLength,
                                 @Named("ioStrategy") IOService ioService,
                                 @Named("datasetsFS") FileSystem fileSystem,
                                 DataSetProviderRegistryCDI dataSetProviderRegistry,
                                 SchedulerCDI scheduler,
                                 ExceptionManager exceptionManager,
                                 Event<DataSetDefModifiedEvent> dataSetDefModifiedEvent,
                                 Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent,
                                 Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent,
                                 Event<DataSetStaleEvent> dataSetStaleEvent) {

        super(dataSetProviderRegistry,
              scheduler);
        this.uuidGenerator = DataSetCore.get().getUuidGenerator();
        this.maxCsvLength = maxCsvLength;
        this.ioService = ioService;
        this.fileSystem = fileSystem;
        this.exceptionManager = exceptionManager;
        this.dataSetDefModifiedEvent = dataSetDefModifiedEvent;
        this.dataSetDefRegisteredEvent = dataSetDefRegisteredEvent;
        this.dataSetDefRemovedEvent = dataSetDefRemovedEvent;
        this.dataSetStaleEvent = dataSetStaleEvent;
    }

    @PostConstruct
    public void init() {
        initFileSystem();
        deleteTempFiles();
        registerDataSetDefs();
    }

    public DataSetDefJSONMarshaller getDataSetDefJsonMarshaller() {
        return DataSetCore.get().getDataSetDefJSONMarshaller();
    }

    @Override
    protected void onDataSetDefStale(DataSetDef def) {
        dataSetStaleEvent.fire(new DataSetStaleEvent(def));
    }

    @Override
    protected void onDataSetDefModified(DataSetDef olDef,
                                        DataSetDef newDef) {
        dataSetDefModifiedEvent.fire(new DataSetDefModifiedEvent(olDef,
                                                                 newDef));
    }

    @Override
    protected void onDataSetDefRegistered(DataSetDef newDef) {
        dataSetDefRegisteredEvent.fire(new DataSetDefRegisteredEvent(newDef));
    }

    @Override
    protected void onDataSetDefRemoved(DataSetDef oldDef) {
        dataSetDefRemovedEvent.fire(new DataSetDefRemovedEvent(oldDef));
    }

    protected void initFileSystem() {
        root = fileSystem.getRootDirectories().iterator().next();
    }

    protected void registerDataSetDefs() {
        for (DataSetDef def : listDataSetDefs()) {
            super.dataSetDefMap.put(def.getUUID(),
                                    new DataSetDefEntry(def));
        }
    }

    public org.uberfire.backend.vfs.Path resolveVfsPath(DataSetDef def) {
        return convert(resolveNioPath(def));
    }

    protected Path resolveNioPath(DataSetDef def) {
        return getDataSetsPath().resolve(def.getUUID() + DATASET_EXT);
    }

    @Override
    public void registerDataSetDef(DataSetDef def,
                                   String subjectId,
                                   String message) {

        if (def.getUUID() == null) {
            final String uuid = uuidGenerator.newUuid();
            def.setUUID(uuid);
        }

        if (subjectId == null || message == null) {
            ioService.startBatch(fileSystem);
        } else {
            ioService.startBatch(fileSystem,
                                 new CommentedOption(subjectId,
                                                     message));
        }

        try {
            String defJson = getDataSetDefJsonMarshaller().toJsonString(def);
            Path defPath = resolveNioPath(def);
            ioService.write(defPath,
                            defJson);

            // CSV specific
            if (def instanceof CSVDataSetDef) {
                saveCSVFile((CSVDataSetDef) def);
            }
            super.registerDataSetDef(def,
                                     subjectId,
                                     message);
        } catch (Exception e) {
            throw exceptionManager.handleException(
                    new Exception("Can't register the data set definition\n" + def,
                                  e));
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public DataSetDef removeDataSetDef(String uuid,
                                       String subjectId,
                                       String message) {
        DataSetDef def = getDataSetDef(uuid);
        if (def == null) {
            return null;
        }
        return removeDataSetDef(def,
                                subjectId,
                                message);
    }

    public void removeDataSetDef(org.uberfire.backend.vfs.Path path,
                                 String subjectId,
                                 String comment) {
        DataSetDef def = loadDataSetDef(path);
        if (def != null) {
            removeDataSetDef(def,
                             subjectId,
                             comment);
        }
    }

    public DataSetDef removeDataSetDef(DataSetDef def,
                                       String subjectId,
                                       String message) {
        Path defPath = resolveNioPath(def);

        if (ioService.exists(defPath)) {
            if (subjectId == null || message == null) {
                ioService.startBatch(fileSystem);
            } else {
                ioService.startBatch(fileSystem,
                                     new CommentedOption(subjectId,
                                                         message));
            }
            try {
                ioService.deleteIfExists(defPath,
                                         StandardDeleteOption.NON_EMPTY_DIRECTORIES);

                // CSV specific
                if (def instanceof CSVDataSetDef) {
                    deleteCSVFile((CSVDataSetDef) def);
                }
            } finally {
                ioService.endBatch();
            }
        }
        return super.removeDataSetDef(def.getUUID(),
                                      subjectId,
                                      message);
    }

    public Collection<DataSetDef> listDataSetDefs() {
        final Collection<DataSetDef> result = new ArrayList<>();

        if (ioService.exists(root)) {
            walkFileTree(checkNotNull("root",
                                      root),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);
                                     checkNotNull("attrs",
                                                  attrs);

                                     if (file.getFileName().toString().endsWith(DATASET_EXT) && attrs.isRegularFile()) {
                                         String json = ioService.readAllString(file);
                                         DataSetDef def = getDataSetDefJsonMarshaller().fromJson(json);
                                         result.add(def);
                                     }
                                 } catch (final Exception e) {
                                     log.error("Data set definition read error: " + file.getFileName(),
                                               e);
                                     return FileVisitResult.TERMINATE;
                                 }
                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }
        return result;
    }

    public DataSetDef loadDataSetDef(org.uberfire.backend.vfs.Path path) {
        Path nioPath = convert(path);
        if (ioService.exists(nioPath)) {
            try {
                String json = ioService.readAllString(nioPath);
                DataSetDef def = getDataSetDefJsonMarshaller().fromJson(json);
                return def;
            } catch (Exception e) {
                String msg = "Error parsing data set JSON definition: " + path.getFileName();
                throw exceptionManager.handleException(new Exception(msg,
                                                                     e));
            }
        }
        return null;
    }

    public DataSetDef copyDataSetDef(DataSetDef def,
                                     String newName,
                                     String subjectId,
                                     String message) {
        DataSetDef clone = def.clone();
        clone.setUUID(uuidGenerator.newUuid());
        clone.setName(newName);

        if (subjectId == null || message == null) {
            ioService.startBatch(fileSystem);
        } else {
            ioService.startBatch(fileSystem,
                                 new CommentedOption(subjectId,
                                                     message));
        }
        try {
            // CSV specific
            if (def instanceof CSVDataSetDef) {
                CSVDataSetDef csvDef = (CSVDataSetDef) def;
                CSVDataSetDef csvCloneDef = (CSVDataSetDef) clone;
                Path csvPath = resolveCsvPath(csvDef);
                Path cloneCsvPath = resolveCsvPath(csvCloneDef);
                ioService.copy(csvPath,
                               cloneCsvPath);
                csvCloneDef.setFilePath(convert(cloneCsvPath).toURI());
            }
            String defJson = getDataSetDefJsonMarshaller().toJsonString(clone);
            Path clonePath = resolveNioPath(clone);
            ioService.write(clonePath,
                            defJson);

            super.registerDataSetDef(clone,
                                     subjectId,
                                     message);
            return clone;
        } catch (Exception e) {
            throw exceptionManager.handleException(
                    new Exception("Can't register the data set definition\n" + def,
                                  e));
        } finally {
            ioService.endBatch();
        }
    }

    public Path createTempFile(String fileName) {
        Path tempPath = resolveTempPath(fileName);
        return tempPath;
    }

    public void deleteTempFiles() {
        Path tempPath = getTempPath();
        if (ioService.exists(tempPath)) {
            ioService.startBatch(fileSystem,
                                 new CommentedOption("system",
                                                     "Delete temporal files"));
            try {
                walkFileTree(tempPath,
                             new SimpleFileVisitor<Path>() {

                                 @Override
                                 public FileVisitResult postVisitDirectory(Path dir,
                                                                           IOException exc) throws IOException {
                                     Files.delete(dir);
                                     return FileVisitResult.CONTINUE;
                                 }

                                 @Override
                                 public FileVisitResult visitFile(Path file,
                                                                  BasicFileAttributes attrs) throws IOException {
                                     Files.delete(file);
                                     return FileVisitResult.CONTINUE;
                                 }
                             });
            } finally {
                ioService.endBatch();
            }
        }
    }

    protected Path getDataSetsPath() {
        return root.resolve("definitions");
    }

    protected Path getTempPath() {
        return root.resolve("tmp");
    }

    protected Path resolveTempPath(String fileName) {
        return getTempPath().resolve(fileName);
    }

    protected org.uberfire.backend.vfs.Path convert(Path path) {
        return Paths.convert(path);
    }

    protected Path convert(org.uberfire.backend.vfs.Path path) {
        return Paths.convert(path);
    }

    //
    // CSV files storage
    //

    @Override
    public String getCSVString(CSVDataSetDef def) {
        Path nioPath = resolveCsvPath(def);
        if (ioService.exists(nioPath)) {
            return ioService.readAllString(nioPath);
        }
        return null;
    }

    @Override
    public InputStream getCSVInputStream(CSVDataSetDef def) {
        Path nioPath = resolveCsvTempPath(def);
        if (ioService.exists(nioPath)) {
            // In edition process ...
            return ioService.newInputStream(nioPath);
        }
        nioPath = resolveCsvPath(def);
        if (ioService.exists(nioPath)) {
            // Already created & persisted
            return ioService.newInputStream(nioPath);
        }
        return null;
    }

    @Override
    public void deleteCSVFile(CSVDataSetDef def) {
        Path csvPath = resolveCsvPath(def);

        if (ioService.exists(csvPath)) {
            ioService.deleteIfExists(csvPath,
                                     StandardDeleteOption.NON_EMPTY_DIRECTORIES);
        }
    }

    @Override
    public void saveCSVFile(CSVDataSetDef def) {
        String path = def.getFilePath();
        if (StringUtils.isBlank(path)) {
            return;
        }

        // The CSV file was uploaded from UI to the temp directory => move the file to the definitions directory
        Path csvTempPath = resolveCsvTempPath(def);
        if (ioService.exists(csvTempPath)) {
            Path csvPath = resolveCsvPath(def);
            if (ioService.exists(csvPath)) {
                // Avoid FileAlreadyExistsException on call to move (see below)
                ioService.delete(csvPath);
            }
            ioService.move(csvTempPath,
                           csvPath);
            return;
        }
        // The CSV was registered or deployed via API => Copy the file contents to the definitions directory
        File csvFile = new File(path);
        if (csvFile.exists()) {
            if (csvFile.length() > maxCsvLength) {
                String msg = "CSV file length exceeds the maximum allowed: " + maxCsvLength / 1024 + " Kb";
                throw exceptionManager.handleException(new Exception(msg));
            }

            try {
                Path defPath = resolveCsvPath(def);
                String csvContent = FileUtils.readFileToString(csvFile);
                ioService.write(defPath,
                                csvContent);
            } catch (Exception e) {
                String msg = "Error saving CSV file: " + csvFile;
                throw exceptionManager.handleException(new Exception(msg,
                                                                     e));
            }
        }
    }

    protected Path resolveCsvPath(CSVDataSetDef def) {
        return getDataSetsPath().resolve(def.getUUID() + CSV_EXT);
    }

    protected Path resolveCsvTempPath(CSVDataSetDef def) {
        return resolveTempPath(def.getUUID() + CSV_EXT);
    }

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        DataSetDef def = event.getDataSetDef();
        dataSetDefMap.put(
            def.getUUID(),
            new DataSetDefEntry(def));
    }
}
