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

package org.dashbuilder.transfer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.external.service.ExternalComponentLoader;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.storage.NavTreeStorage;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.type.TypeConverterUtil;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static org.dashbuilder.dataset.DataSetDefRegistryCDI.DATASET_EXT;
import static org.uberfire.ext.plugin.model.Plugin.FILE_EXT;

@ApplicationScoped
@Service
public class DataTransferServicesImpl implements DataTransferServices {

    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferServicesImpl.class);
    private IOService ioService;
    private FileSystem datasetsFS;
    private FileSystem perspectivesFS;
    private FileSystem navigationFS;
    private FileSystem systemFS;
    private DataSetDefRegistryCDI dataSetDefRegistryCDI;
    private SessionInfo sessionInfo;
    private Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    private Event<PluginAdded> pluginAddedEvent;
    private Event<NavTreeChangedEvent> navTreeChangedEvent;
    private NavTreeStorage navTreeStorage;
    private byte[] buffer = new byte[1024];
    private ExternalComponentLoader externalComponentLoader;

    public DataTransferServicesImpl() {}

    @Inject
    public DataTransferServicesImpl(
                                    final @Named("ioStrategy") IOService ioService,
                                    final @Named("datasetsFS") FileSystem datasetsFS,
                                    final @Named("perspectivesFS") FileSystem perspectivesFS,
                                    final @Named("navigationFS") FileSystem navigationFS,
                                    final @Named("systemFS") FileSystem systemFS,
                                    final DataSetDefRegistryCDI dataSetDefRegistryCDI,
                                    final SessionInfo sessionInfo,
                                    final Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent,
                                    final Event<PluginAdded> pluginAddedEvent,
                                    final Event<NavTreeChangedEvent> navTreeChangedEvent,
                                    final NavTreeStorage navTreeStorage,
                                    final ExternalComponentLoader externalComponentLoader) {

        this.ioService = ioService;
        this.datasetsFS = datasetsFS;
        this.perspectivesFS = perspectivesFS;
        this.navigationFS = navigationFS;
        this.systemFS = systemFS;
        this.dataSetDefRegistryCDI = dataSetDefRegistryCDI;
        this.sessionInfo = sessionInfo;
        this.dataSetDefRegisteredEvent = dataSetDefRegisteredEvent;
        this.pluginAddedEvent = pluginAddedEvent;
        this.navTreeChangedEvent = navTreeChangedEvent;
        this.navTreeStorage = navTreeStorage;
        this.externalComponentLoader = externalComponentLoader;
    }

    @Override
    public String doExport(DataTransferExportModel exportModel) throws java.io.IOException {
        String zipLocation = new StringBuilder().append(System.getProperty("java.io.tmpdir"))
                                                .append(File.separator)
                                                .append(FILE_PATH)
                                                .append(File.separator)
                                                .append(EXPORT_FILE_NAME)
                                                .toString()
                                                .replace("\\", "/");

        Predicate<Path> readmeFilter = p -> p.toString().toLowerCase().endsWith("readme.md");
        Predicate<Path> datasetsFilter = def -> true;
        Predicate<Path> pagesFilter = page -> true;
        boolean exportNavigation = true;

        if (!exportModel.isExportAll()) {
            datasetsFilter = filterDatasets(exportModel.getDatasetDefinitions());
            pagesFilter = filterPages(exportModel.getPages());
            exportNavigation = exportModel.isExportNavigation();
        }

        new File(zipLocation).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(zipLocation);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zipFileSystem(datasetsFS, zos, readmeFilter.or(datasetsFilter));
        zipFileSystem(perspectivesFS, zos, readmeFilter.or(pagesFilter));

        if (exportNavigation) {
            zipFileSystem(navigationFS, zos, p -> true);
        } else {
            zipFileSystem(navigationFS, zos, readmeFilter);
        }

        if (externalComponentLoader.isEnabled()) {
            String componentsPath = externalComponentLoader.getExternalComponentsDir();
    
            if (componentsPath != null && exists(componentsPath)) {
                Path componentsBasePath = Paths.get(new StringBuilder().append(SpacesAPI.Scheme.FILE)
                                                    .append("://")
                                                    .append(componentsPath)
                                                    .toString());
                externalComponentLoader.load().forEach(c -> {                
                 Path componentPath = componentsBasePath.resolve(c.getId());
                 zipComponentFiles(componentsBasePath,
                                   componentPath,
                                   zos,
                                   p -> true);
                });
            }
        }

        zipFile(createVersionFile(), "VERSION", zos);

        zos.close();
        fos.close();

        moveZipToFileSystem(zipLocation, systemFS);

        return new StringBuilder().append(SpacesAPI.Scheme.GIT)
                                  .append("://")
                                  .append(systemFS.getName())
                                  .append(File.separator)
                                  .append(FILE_PATH)
                                  .append(File.separator)
                                  .append(EXPORT_FILE_NAME)
                                  .toString()
                                  .replace("\\", "/");
    }

    @Override
    public List<String> doImport() throws Exception {
        List<String> imported = new ArrayList<>();

        Path root = Paths.get(URI.create(new StringBuilder().append(SpacesAPI.Scheme.GIT)
                                                            .append("://")
                                                            .append(systemFS.getName())
                                                            .append(File.separator)
                                                            .toString()
                                                            .replace("\\", "/")));

        String expectedPath = new StringBuilder().append(File.separator)
                                                 .append(FILE_PATH)
                                                 .append(File.separator)
                                                 .append(IMPORT_FILE_NAME)
                                                 .toString()
                                                 .replace("\\", "/");

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                if (!path.toString().equalsIgnoreCase(expectedPath)) {
                    return FileVisitResult.CONTINUE;
                }

                try {
                    imported.addAll(importFiles(path));
                    return FileVisitResult.TERMINATE;

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return FileVisitResult.TERMINATE;
                }
            }
        });

        ioService.deleteIfExists(root.resolve(expectedPath));

        return imported;
    }

    @Override
    public DataTransferAssets assetsToExport() {
        List<String> pages = listPaths(perspectivesFS,
                                       p -> p.endsWith(FILE_EXT)).stream()
                                                                 .map(p -> p.getName(p.getNameCount() - 2))
                                                                 .map(Object::toString)
                                                                 .collect(Collectors.toList());

        List<DataSetDef> datasetsDefs = listPaths(datasetsFS,
                                                  p -> p.endsWith(DATASET_EXT)).stream()
                                                                               .map(this::getDataSetFileContent)
                                                                               .map(this::parseDataSetDefinition)
                                                                               .filter(DataSetDef::isPublic)
                                                                               .collect(Collectors.toList());
        return new DataTransferAssets(datasetsDefs, pages);
    }

    private List<String> importFiles(Path path) throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir.lastIndexOf('/') == tmpDir.length() - 1 || tmpDir.lastIndexOf('\\') == tmpDir.length() - 1) {
            tmpDir = tmpDir.substring(0, tmpDir.length() - 1);
        }

        String tempPath = new StringBuilder().append(tmpDir)
                                             .append(File.separator)
                                             .append(FILE_PATH)
                                             .append(File.separator)
                                             .toString().replace("\\", "/");

        List<String> imported = new ArrayList<>();
        File destDir = new File(tempPath);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(path.toFile()))) {

            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                File newFile = unzipFile(destDir, zipEntry, zis);
                FileSystem fileSystem = getImportFileSystem(newFile, tempPath);

                if (fileSystem != null) {
                    String newFilePath = importFSFile(fileSystem.getName(), newFile, tempPath);
                    imported.add(new StringBuilder().append(fileSystem.getName())
                                                    .append(newFilePath)
                                                    .toString());
                }

                if (isComponent(zipEntry) &&
                    externalComponentLoader.getExternalComponentsDir() != null &&
                    externalComponentLoader.isEnabled()) {
                    try {
                        importComponentFile(zipEntry.getName(), newFile);
                        imported.add(zipEntry.getName());
                    } catch (Exception e) {
                        LOGGER.error("Error importing component file {}", zipEntry.getName());
                        LOGGER.debug("Component file import error.", e);
                    }
                }

            }
        }

        FileUtils.deleteDirectory(destDir);

        return imported;
    }

    private boolean isComponent(ZipEntry zipEntry) {
        return zipEntry.getName() != null && zipEntry.getName().startsWith(COMPONENTS_EXPORT_PATH);
    }

    private String importComponentFile(String entryName, File newFile) {
        String externalComponentsDir = externalComponentLoader.getExternalComponentsDir();
        externalComponentsDir = externalComponentsDir.endsWith(File.separator) ? externalComponentsDir : externalComponentsDir + "/";
        String newFileName = externalComponentsDir + entryName.replaceAll(COMPONENTS_EXPORT_PATH, "");
        copyFileContents(newFile, newFileName);
        return newFileName;
    }

    private void copyFileContents(File newFile, String newFileName) {
        File target = new File(newFileName);
        target.getParentFile().mkdirs();
        if (!target.exists()) {
            ioService.copy(Paths.get(newFile.toURI()), Paths.get(target.toURI()));
        }
    }

    private String importFSFile(String fsName, File newFile, String workingDir) throws Exception {
        URI uri = URI.create(new StringBuilder().append(SpacesAPI.Scheme.GIT)
                                                .append("://")
                                                .append(fsName)
                                                .toString()
                                                .replace("\\", "/"));

        String newFilePath = newFile.toPath()
                                    .toString()
                                    .replace("\\", "/")
                                    .replace(new StringBuilder(workingDir).append(fsName),
                                             "");

        ioService.write(Paths.get(uri).resolve(newFilePath),
                        java.nio.file.Files.readAllBytes(newFile.toPath()));

        fireEvent(newFile, workingDir, uri, newFilePath);
        return newFilePath;

    }

    private void fireEvent(File newFile, String tempPath, URI uri, String newFilePath) {
        String filePath = newFile.toURI().toString();

        if (filePath.contains(tempPath + datasetsFS.getName()) && newFilePath.endsWith(DATASET_EXT)) {
            fireDatasetEvent(uri, newFilePath);

        } else if (filePath.contains(tempPath + perspectivesFS.getName()) && newFilePath.endsWith(FILE_EXT)) {
            firePerspectiveEvent(newFile, uri, newFilePath);

        } else if (filePath.contains(tempPath + navigationFS.getName()) && newFilePath.endsWith(NavTreeStorage.NAV_TREE_FILE_NAME)) {
            fireNavigationEvent();
        }
    }

    private void fireDatasetEvent(URI uri, String newFilePath) {
        try {
            String json = ioService.readAllString(Paths.get(uri).resolve(newFilePath));
            DataSetDef newDef = dataSetDefRegistryCDI.getDataSetDefJsonMarshaller().fromJson(json);
            dataSetDefRegisteredEvent.fire(new DataSetDefRegisteredEvent(newDef));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void firePerspectiveEvent(File newFile, URI uri, String newFilePath) {
        org.uberfire.backend.vfs.Path pluginPath =
                org.uberfire.backend.server.util.Paths.convert(
                                                               Paths.get(uri).resolve(newFilePath));

        Plugin plugin = new Plugin(
                                   newFile.toPath().getParent().getFileName().toString(),
                                   TypeConverterUtil.fromPath(pluginPath),
                                   pluginPath);

        pluginAddedEvent.fire(new PluginAdded(plugin, sessionInfo));
    }

    private void fireNavigationEvent() {
        navTreeChangedEvent.fire(
                                 new NavTreeChangedEvent(
                                                         navTreeStorage.loadNavTree()));
    }

    private FileSystem getImportFileSystem(File file, String tempPath) {
        List<FileSystem> fileSystems = Arrays.asList(datasetsFS, perspectivesFS, navigationFS);
        String filePath = file.toURI().toString().replace("\\", "/");

        return fileSystems.stream()
                          .filter(fs -> filePath.contains(tempPath + fs.getName()))
                          .findFirst()
                          .orElse(null);
    }

    private void moveZipToFileSystem(String zipLocation, FileSystem fileSystem) {
        String sourceLocation = new StringBuilder()
                                                   .append(SpacesAPI.Scheme.FILE)
                                                   .append(":///")
                                                   .append(zipLocation)
                                                   .toString();

        Path source = Paths.get(URI.create(sourceLocation));

        Path target = Paths.get(URI.create(new StringBuilder().append(SpacesAPI.Scheme.GIT)
                                                              .append("://")
                                                              .append(fileSystem.getName())
                                                              .append(File.separator)
                                                              .append(FILE_PATH)
                                                              .append(File.separator)
                                                              .append(EXPORT_FILE_NAME)
                                                              .toString()
                                                              .replace("\\", "/")));

        ioService.write(target, Files.readAllBytes(source));

        Files.delete(source);
    }

    private File unzipFile(File destinationDir, ZipEntry zipEntry, ZipInputStream zis) throws java.io.IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        if (!destFile.exists()) {
            destFile.getParentFile().mkdirs();
            if (!destFile.createNewFile()) {
                throw new IOException("could not create file " + destFile.getPath());
            }
        }

        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }

        return destFile;
    }

    private void zipFileSystem(FileSystem fs, ZipOutputStream zos, Predicate<Path> pathTest) {
        Path root = fs.getRootDirectories().iterator().next();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                try {
                    if (pathTest.test(path)) {
                        String location = fs.getName() + path.toString();
                        zipFile(path.toFile(), location, zos);
                    }
                    return FileVisitResult.CONTINUE;

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return FileVisitResult.TERMINATE;
                }
            }
        });
    }

    private void zipComponentFiles(Path componentsRoot, Path componentRoot, ZipOutputStream zos, Predicate<Path> pathTest) {
        Files.walkFileTree(componentRoot, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                try {
                    if (pathTest.test(path)) {
                        File file = path.toFile();
                        Path relativePath = componentsRoot.relativize(path);
                        String location = COMPONENTS_EXPORT_PATH + relativePath.toString();
                        zipFile(file, location, zos);
                    }
                    return FileVisitResult.CONTINUE;

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return FileVisitResult.TERMINATE;
                }
            }
        });
    }

    private void zipFile(File file, String path, ZipOutputStream zos) throws java.io.IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(path);
            zos.putNextEntry(zipEntry);

            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    private File createVersionFile() throws java.io.IOException {
        File version = File.createTempFile("temp", "version");

        try (BufferedWriter out = new BufferedWriter(new FileWriter(version))) {
            out.write(VERSION);
        }

        return version;
    }

    private List<Path> listPaths(FileSystem fs, Predicate<String> pathTester) {
        List<Path> files = new ArrayList<>();
        Path root = fs.getRootDirectories().iterator().next();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                try {
                    if (pathTester.test(path.toString())) {
                        files.add(path);
                    }
                    return FileVisitResult.CONTINUE;

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return FileVisitResult.TERMINATE;
                }
            }
        });
        return files;
    }

    private String getDataSetFileContent(Path path) {
        return Files.readAllLines(path, StandardCharsets.UTF_8).stream().collect(Collectors.joining());
    }

    private DataSetDef parseDataSetDefinition(String defJson) {
        try {
            return dataSetDefRegistryCDI.getDataSetDefJsonMarshaller().fromJson(defJson);
        } catch (Exception e) {
            LOGGER.error("Error parsing dataset definition", e);
            LOGGER.debug("Json Definition: {}", defJson);
            throw new IllegalArgumentException(e);
        }
    }

    private Predicate<Path> filterPages(List<String> pages) {
        return page -> {
            if (pages.isEmpty()) {
                return false;
            }
            int nameCount = page.getNameCount();
            if (nameCount > 1) {
                return pages.stream()
                            .anyMatch(p -> page.getName(nameCount - 2).toString().equals(p));
            }
            return false;
        };
    }

    private Predicate<Path> filterDatasets(List<DataSetDef> datasets) {
        return dsPath -> {
            if (datasets.isEmpty()) {
                return false;
            }
            int nameCount = dsPath.getNameCount();
            if (nameCount > 1) {
                String fileName = dsPath.getName(nameCount - 1)
                                        .toString()
                                        .split("\\.")[0];
                return datasets.stream()
                               .anyMatch(ds -> ds.getUUID().equals(fileName));
            }
            return false;
        };
    }
    
    private boolean exists(String file) {
        return java.nio.file.Files.exists(java.nio.file.Paths.get(file));
    }
}
