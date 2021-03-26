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
package org.dashbuilder.dsl.serialization.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.NotSupportedException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dsl.factory.dashboard.DashboardFactory;
import org.dashbuilder.dsl.factory.navigation.NavigationFactory;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Navigation;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.dsl.serialization.DashboardSerializer;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.dashbuilder.dsl.helper.ComponentsHelper.listComponents;
import static org.dashbuilder.dsl.helper.ComponentsHelper.listPagesComponents;

public class DashboardZipSerializer implements DashboardSerializer {

    private static final String PATH_SEPARATOR = "/";

    Logger logger = LoggerFactory.getLogger(DashboardZipSerializer.class);

    private static int MAX_ENTRIES = 10_000;
    
    private static final String CSV_EXT = ".csv";
    private static final String DATASET_EXT = ".dset";
    private static final String PLUGIN_EXT = ".plugin";
    private static final String PERSPECTIVE_LAYOUT = "perspective_layout";

    private static final String BASE_PATH = "dashbuilder/";

    private static final String LAYOUTS_PATH = BASE_PATH + "perspectives/";
    private static final String DATA_SETS_BASE = BASE_PATH + "datasets/";
    private static final String COMPONENTS_BASE = BASE_PATH + "components/";
    private static final String DATA_SETS_PATH = DATA_SETS_BASE + "definitions/";
    private static final String NAVIGATION_PATH = BASE_PATH + "navigation/navigation/navtree.json";

    private static final DataSetDefJSONMarshaller DATA_SET_MARSHALLER = new DataSetDefJSONMarshaller(new InternalDataSetProviderRegistry());

    private static final Gson gson = new GsonBuilder().create();

    @Override
    public Dashboard deserialize(InputStream model) {
        throw new NotSupportedException("Deserialize is not supported for ZIP Serializer");
    }
    
    /**
     * Does not support: CSV files and components files
     */
    Dashboard internalDeserialize(InputStream model) {
        Map<String, String> importContent = readAllEntriesContent(model);

        List<Page> pages = new ArrayList<>();
        List<DataSetDef> datasets = new ArrayList<>();
        AtomicReference<Navigation> navigationRef = new AtomicReference<>(NavigationFactory.emptyNavigation());

        importContent.forEach((path, content) -> {
            if (path.startsWith(LAYOUTS_PATH) && path.endsWith(PERSPECTIVE_LAYOUT)) {
                LayoutTemplate template = gson.fromJson(content, LayoutTemplate.class);
                pages.add(Page.create(template));
            }

            if (path.startsWith(DATA_SETS_PATH) && path.endsWith(DATASET_EXT)) {
                DataSetDef def;
                try {
                    def = DATA_SET_MARSHALLER.fromJson(content);
                    datasets.add(def);
                } catch (Exception e) {
                    logger.warn("Error reading dataset content {}", path);
                    logger.debug("Error reading dataset content.", e);
                }
            }

            if (path.startsWith(NAVIGATION_PATH)) {
                navigationRef.set(Navigation.of(NavTreeJSONMarshaller.get().fromJson(content)));
            }
        });
        return DashboardFactory.dashboard(pages, datasets, navigationRef.get(), null);
    }

    @Override
    public void serialize(Dashboard dashboard, OutputStream os) {
        ZipOutputStream zos = new ZipOutputStream(os);

        dashboard.getDataSets()
                 .stream()
                 .forEach(def -> writeDataSetDef(zos, def));

        dashboard.getPages()
                 .stream()
                 .forEach(page -> writePage(zos, page));

        dashboard.getDataSets()
                 .stream()
                 .filter(CSVDataSetDef.class::isInstance)
                 .map(CSVDataSetDef.class::cast)
                 .forEach(def -> writeCSVFile(zos, def));

        writeNavigation(zos, dashboard.getNavigation());

        writeContent(zos, DATA_SETS_BASE + "readme.md", "");

        Optional<Path> componentsPathOp = dashboard.getComponentsPath();
        if (componentsPathOp.isPresent()) {
            Path componentsPath = componentsPathOp.get();
            List<ExternalComponent> components = listComponents(componentsPath);
            listPagesComponents(dashboard.getPages()).stream()
                                                     .filter(c -> components.stream()
                                                                            .anyMatch(comp -> comp.getId().equals(c)))
                                                     .forEach(c -> writeComponent(componentsPath, c, zos));
        }

        try {
            zos.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing ZIP", e);
        }
    }

    private void writeCSVFile(ZipOutputStream zos, CSVDataSetDef def) {
        String filePath = def.getFilePath();
        if (filePath != null) {
            Path path = Paths.get(filePath);
            if (path.toFile().exists()) {
                try {
                    writeContent(zos, DATA_SETS_PATH + def.getUUID() + CSV_EXT, Files.readAllBytes(path));
                } catch (IOException e) {
                    logger.warn("Not able to write CSV file {} to the exported ZIP", filePath);
                    logger.debug("Not able to write CSV", e);
                }
            }

        }
    }

    private void writeComponent(Path componentsPath, String componentId, ZipOutputStream zos) {
        final Path componentPath = componentsPath.resolve(componentId);
        final String componentZipPathBase = COMPONENTS_BASE + componentId + PATH_SEPARATOR;
        try (Stream<Path> walker = Files.walk(componentPath, 1)) {
            walker.filter(p -> !p.toFile().isDirectory())
                  .forEach(file -> {
                      String fileName = componentZipPathBase + file.toFile().getName();
                      writeContent(zos, fileName, file);
                  });
        } catch (IOException e) {
            logger.debug("Error loading external component files.", e);
            throw new RuntimeException("Error loading components from " + componentsPath + ". Error: " + e.getMessage());
        }
    }

    private void writeNavigation(ZipOutputStream zos, Navigation navigation) {
        String content = NavTreeJSONMarshaller.get().toJson(navigation.getNavTree()).toString();
        writeContent(zos, NAVIGATION_PATH, content);
    }

    private void writePage(ZipOutputStream zos, Page page) {
        LayoutTemplate lt = page.getLayoutTemplate();
        String path = LAYOUTS_PATH + lt.getName() + PATH_SEPARATOR + PERSPECTIVE_LAYOUT;
        String pluginPath = path + PLUGIN_EXT;
        String content = gson.toJson(lt);
        writeContent(zos, path, content);
        writeContent(zos, pluginPath, new Date().toString());
    }

    private void writeDataSetDef(ZipOutputStream zos, DataSetDef def) {
        String path = DATA_SETS_PATH + def.getUUID() + DATASET_EXT;
        String content = DATA_SET_MARSHALLER.toJsonString(def);
        writeContent(zos, path, content);
    }

    private void writeContent(ZipOutputStream zos, String path, byte[] content) {
        try {
            zos.putNextEntry(new ZipEntry(path));
            zos.write(content);
            zos.closeEntry();
        } catch (IOException e) {
            logger.warn("Error writing content on path {}", path);
            logger.debug("Error writing content. ", e);
        }
    }

    private void writeContent(ZipOutputStream zos, String path, String content) {
        this.writeContent(zos, path, content.getBytes());
    }

    private void writeContent(ZipOutputStream zos, String path, Path file) {
        try {
            this.writeContent(zos, path, Files.readAllBytes(file));
        } catch (IOException e) {
            logger.info("Error reading component file {}: {}", file, e.getMessage());
            logger.debug("Error reading component file", e);
        }
    }

    Map<String, String> readAllEntriesContent(InputStream model) {
        Map<String, String> entriesContent = new HashMap<>();
        Path zipTemp;
        try {
            zipTemp = Files.createTempFile("dashboard", "zip");
            Files.copy(model, zipTemp, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp file", e);
        }

        try (ZipFile zipFile = new ZipFile(zipTemp.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int totalEntries = 0;
            while (entries.hasMoreElements()) {
                if (++totalEntries > MAX_ENTRIES) {
                    throw new IllegalArgumentException("ZIP file contains too many entries");
                }
                ZipEntry e = entries.nextElement();
                String content = new BufferedReader(new InputStreamReader(zipFile.getInputStream(e), UTF_8)).lines()
                                                                                                            .collect(Collectors.joining("\n"));
                entriesContent.put(e.getName(), content);

            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading input file", e);
        }

        try {
            Files.delete(zipTemp);
        } catch (IOException e) {
            logger.error("Temp file not deleted due an error", e);
        }
        return entriesContent;
    }

}