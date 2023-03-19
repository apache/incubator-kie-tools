/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.URIBuilder;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.external.service.ComponentLoader;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.dashbuilder.navigation.storage.NavTreeStorage;
import org.dashbuilder.project.storage.ProjectStorageServices;
import org.dashbuilder.project.storage.event.ProjectUpdated;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.rpc.SessionInfo;

@Service
@ApplicationScoped
public class DataTransferServicesImpl implements DataTransferServices {

    static final String VERSION_FILE = "VERSION";
    static final String EXPORT_ZIP = "export.zip";

    static final String AUTO_EXPORT_FILE_PROP = "dashbuilder.export.location";

    public static final String VERSION = "1.0.0";
    private static final String DASHBOARD_LATEST = "dashboard-latest";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferServicesImpl.class);
    private DataSetDefRegistryCDI dataSetDefRegistryCDI;
    private SessionInfo sessionInfo;
    private Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    private Event<PluginAdded> pluginAddedEvent;
    private Event<NavTreeChangedEvent> navTreeChangedEvent;
    private byte[] buffer = new byte[1024];
    private ComponentLoader externalComponentLoader;
    private LayoutComponentHelper layoutComponentsHelper;
    private String dashbuilderLocation;
    private String exportDir;
    private ProjectStorageServices projectStorageServices;
    private Optional<Path> autoExportPath = Optional.empty();

    public DataTransferServicesImpl() {
        // empty constructor
    }

    @Inject
    public DataTransferServicesImpl(final ProjectStorageServices projectStorageServices,
                                    final DataSetDefRegistryCDI dataSetDefRegistryCDI,
                                    final SessionInfo sessionInfo,
                                    final Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent,
                                    final Event<PluginAdded> pluginAddedEvent,
                                    final Event<NavTreeChangedEvent> navTreeChangedEvent,
                                    final ComponentLoader externalComponentLoader,
                                    final LayoutComponentHelper layoutComponentsHelper) {
        this.projectStorageServices = projectStorageServices;
        this.dataSetDefRegistryCDI = dataSetDefRegistryCDI;
        this.sessionInfo = sessionInfo;
        this.dataSetDefRegisteredEvent = dataSetDefRegisteredEvent;
        this.pluginAddedEvent = pluginAddedEvent;
        this.navTreeChangedEvent = navTreeChangedEvent;
        this.externalComponentLoader = externalComponentLoader;
        this.layoutComponentsHelper = layoutComponentsHelper;
    }

    @PostConstruct
    public void init() {
        dashbuilderLocation = System.getProperty(DB_STANDALONE_LOCATION_PROP);
        exportDir = System.getProperty(EXPORT_LOCATION_PROP);
        autoExportPath = Optional.ofNullable(System.getProperty(AUTO_EXPORT_FILE_PROP)).filter(p -> !p.isBlank()).map(Paths::get);
        autoExportPath.ifPresent(this::doAutoExport);
    }

    @Override
    public String doExport(DataTransferExportModel exportModel) throws java.io.IOException {
        Predicate<Path> readmeFilter = p -> p.toString().toLowerCase().endsWith("readme.md");
        Predicate<Path> datasetsFilter = def -> true;
        Predicate<Path> pagesFilter = page -> true;
        projectStorageServices.removeTempContent(EXPORT_ZIP);
        var zipLocation = projectStorageServices.createTempPath(EXPORT_ZIP);
        var fos = Files.newOutputStream(zipLocation);
        var zos = new ZipOutputStream(fos);
        var exportNavigation = true;

        if (!exportModel.isExportAll()) {
            datasetsFilter = filterDatasets(exportModel.getDatasetDefinitions());
            pagesFilter = filterPages(exportModel.getPages());
            exportNavigation = exportModel.isExportNavigation();
        }

        zipFiles(projectStorageServices.listAllDataSetsContent(),
                 readmeFilter.or(datasetsFilter),
                 p -> ProjectStorageServices.getDatasetsExportPath()
                        .resolve(p.getFileName())
                        .toString(),
                zos);
        createReadme(ProjectStorageServices.getDatasetsExportPath().getParent(), zos);

        zipFiles(projectStorageServices.listPerspectivesPlugins(),
                readmeFilter.or(pagesFilter),
                p -> ProjectStorageServices.getPerspectivesExportPath()
                        .resolve(p.getParent().getFileName())
                        .resolve(p.getFileName()).toString(),
                zos);
        createReadme(ProjectStorageServices.getPerspectivesExportPath(), zos);
        exportNavigation(exportNavigation, zos);
        createReadme(ProjectStorageServices.getNavigationExportPath().getParent(), zos);
        exportComponents(exportModel, zos);
        createVersionInformation(zos);

        zos.close();
        fos.close();

        return zipLocation.toString();
    }

    @Override
    public List<String> doImport() throws Exception {
        var importZip = projectStorageServices.getTempPath(IMPORT_FILE_NAME);
        var imported = importFiles(importZip);
        Files.delete(importZip);
        return imported;
    }

    @Override
    public ExportInfo exportInfo() {
        var pages = projectStorageServices.listPerspectives()
                .keySet()
                .stream()
                .map(p -> p.getParent().getFileName().toString())
                .collect(Collectors.toList());

        var datasetsDefs = projectStorageServices.listDataSets()
                .values()
                .stream()
                .map(this::parseDataSetDefinition)
                .filter(DataSetDef::isPublic)
                .collect(Collectors.toList());

        return new ExportInfo(datasetsDefs, pages, isExternalServerConfigured());
    }

    @Override
    public String generateExportUrl(DataTransferExportModel exportModel) throws Exception {
        if (!isExternalServerConfigured()) {
            throw new RuntimeException("External Server is not configured.");
        }
        try {
            var path = this.doExport(exportModel);
            var destination = new StringBuilder().append(exportDir)
                    .append(File.separator)
                    .append(DASHBOARD_LATEST)
                    .append(".zip")
                    .toString();
            FileUtils.deleteQuietly(new File(destination));
            Files.copy(Paths.get(path), Paths.get(destination));
            return new URIBuilder(dashbuilderLocation).addParameter("import", DASHBOARD_LATEST).toString();
        } catch (Exception e) {
            LOGGER.error("Error generating model link.", e);
            throw new RuntimeException("Error generating model link.", e);
        }

    }

    private void exportComponents(DataTransferExportModel exportModel, ZipOutputStream zos) {
        if (externalComponentLoader.isExternalComponentsEnabled()) {
            var componentsPath = externalComponentLoader.getExternalComponentsDir();
            if (componentsPath != null && exists(componentsPath)) {
                var componentsBasePath = Paths.get(componentsPath);
                Predicate<String> pagesComponentsFilter = page -> exportModel.isExportAll() || exportModel.getPages()
                        .contains(page);
                layoutComponentsHelper.findComponentsInTemplates(pagesComponentsFilter)
                        .stream()
                        .map(c -> componentsBasePath.resolve(c))
                        .filter(Files::exists)
                        .forEach(componentPath -> {
                            zipComponentFiles(componentsBasePath,
                                    componentPath,
                                    zos,
                                    p -> p.toFile().isFile());
                        });
            }
        }
    }

    private void exportNavigation(boolean exportNavigation, ZipOutputStream zos) throws java.io.IOException {
        if (exportNavigation) {
            var p = projectStorageServices.createTempPath("navigation");
            var nav = projectStorageServices.getNavigation();
            if (nav.isPresent()) {
                Files.writeString(p, nav.get());
                zipFile(p.toFile(),
                        ProjectStorageServices.getNavigationExportPath()
                                .resolve(ProjectStorageServices.NAV_TREE_FILE_NAME)
                                .toString(),
                        zos);
            }
            Files.delete(p);
        }
    }

    private void createReadme(Path path, ZipOutputStream zos) throws java.io.IOException {
        var p = projectStorageServices.createTempPath(ProjectStorageServices.README);
        zipFile(p.toFile(),
                path.resolve(ProjectStorageServices.README).toString(),
                zos);
        Files.delete(p);
    }

    private void createVersionInformation(ZipOutputStream zos) throws java.io.IOException {
        var p = projectStorageServices.createTempPath("version");
        Files.writeString(p, VERSION);
        zipFile(p.toFile(), VERSION_FILE, zos);
        Files.delete(p);
    }

    private void zipFiles(Map<Path, String> files,
                          Predicate<Path> filter,
                          Function<Path, String> pathBuilder,
                          ZipOutputStream zos) {
        files.keySet()
                .stream()
                .filter(filter)
                .forEach(p -> zipFile(p.toFile(), pathBuilder.apply(p), zos));
    }

    private boolean isExternalServerConfigured() {
        return exportDir != null && dashbuilderLocation != null;
    }

    private List<String> importFiles(Path path) throws Exception {
        var tempPath = Files.createTempDirectory("dashbuilder_importing").toString();
        var imported = new ArrayList<String>();
        var destDir = new File(tempPath);

        try (var zis = new ZipInputStream(new FileInputStream(path.toFile()))) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                var newFile = unzipFile(destDir, zipEntry, zis);

                if (!isComponent(zipEntry)) {
                    importDashboardFile(newFile, tempPath).ifPresent(imported::add);
                }

                if (isComponent(zipEntry) &&
                    externalComponentLoader.getExternalComponentsDir() != null &&
                    externalComponentLoader.isExternalComponentsEnabled()) {
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
    
    void autoExport(@ObservesAsync ProjectUpdated projectUpdated) {
        autoExportPath.ifPresent(this::doAutoExport);
    }

    private void doAutoExport(Path autoExportPath) {
        try {
            LOGGER.info("Auto exporting project.");
            var path = doExport(DataTransferExportModel.exportAll());
            Files.move(Paths.get(path), autoExportPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.error("Error during auto export: " + e.getMessage());
            LOGGER.debug("Error exporting project", e);
        }
    }

    private boolean isComponent(ZipEntry zipEntry) {
        return zipEntry.getName() != null && zipEntry.getName().startsWith(COMPONENTS_EXPORT_PATH);
    }

    private String importComponentFile(String entryName, File newFile) throws java.io.IOException {
        var externalComponentsDir = externalComponentLoader.getExternalComponentsDir();
        var destination = Paths.get(externalComponentsDir, entryName.replaceAll(COMPONENTS_EXPORT_PATH, ""));
        Files.createDirectories(destination.getParent());
        Files.copy(newFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    private Optional<String> importDashboardFile(File newFile, String tempPath) throws java.io.IOException {
        Optional<String> addedFile = Optional.empty();
        var filePath = newFile.toPath().toString().replaceAll(tempPath, "");

        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        var content = Files.readString(newFile.toPath());

        if (filePath.startsWith(ProjectStorageServices.getDatasetsExportPath().toString())) {
            if (filePath.endsWith(ProjectStorageServices.DATASET_EXT)) {
                projectStorageServices.saveDataSet(newFile.getName(), content);
                fireDatasetEvent(content);
            } else {
                projectStorageServices.saveDataSetContent(newFile.getName(), content);
            }
            addedFile = Optional.of(filePath);
        }

        if (filePath.startsWith(ProjectStorageServices.getPerspectivesExportPath().toString()) &&
            filePath.endsWith(ProjectStorageServices.PERSPECTIVE_LAYOUT)) {
            var perspectiveName = newFile.toPath().getParent().getFileName().toString();
            projectStorageServices.savePerspective(perspectiveName, content);
            firePerspectiveEvent(perspectiveName);
            addedFile = Optional.of(filePath);
        }

        if (filePath.endsWith(NavTreeStorage.NAV_TREE_FILE_NAME)) {
            projectStorageServices.saveNavigation(content);
            fireNavigationEvent(content);
            addedFile = Optional.of(filePath);
        }

        return addedFile;
    }

    private void fireDatasetEvent(String json) {
        try {
            var newDef = dataSetDefRegistryCDI.getDataSetDefJsonMarshaller().fromJson(json);
            dataSetDefRegisteredEvent.fire(new DataSetDefRegisteredEvent(newDef));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void firePerspectiveEvent(String perspectiveName) {
        var pluginPath = PathFactory.newPath(perspectiveName, perspectiveName);
        var plugin = new Plugin(perspectiveName, PluginType.PERSPECTIVE_LAYOUT, pluginPath);
        pluginAddedEvent.fire(new PluginAdded(plugin, sessionInfo));
    }

    private void fireNavigationEvent(String navigation) {
        navTreeChangedEvent.fire(new NavTreeChangedEvent(NavTreeJSONMarshaller.get().fromJson(navigation)));
    }

    private File unzipFile(File destinationDir, ZipEntry zipEntry, ZipInputStream zis) throws java.io.IOException {
        var destFile = new File(destinationDir, zipEntry.getName());
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

    private void zipComponentFiles(Path componentsRoot,
                                   Path componentRoot,
                                   ZipOutputStream zos,
                                   Predicate<Path> pathTest) {

        try {
            Files.walk(componentRoot).forEach(path -> {
                try {
                    if (pathTest.test(path)) {
                        var file = path.toFile();
                        var relativePath = componentsRoot.relativize(path);
                        var location = Paths.get(COMPONENTS_EXPORT_PATH, relativePath.toString());
                        zipFile(file, location.toString(), zos);
                    }

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
        } catch (java.io.IOException e) {
            LOGGER.error("Error walking component directory.", e);
        }

    }

    private void zipFile(File file, String path, ZipOutputStream zos) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(path);
                zos.putNextEntry(zipEntry);

                int length;
                while ((length = fis.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
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
