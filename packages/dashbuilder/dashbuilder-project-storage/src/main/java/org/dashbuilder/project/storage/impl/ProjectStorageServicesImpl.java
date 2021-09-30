package org.dashbuilder.project.storage.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.project.storage.ProjectStorageServices;
import org.dashbuilder.project.storage.annotations.ProjectUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProjectStorageServicesImpl implements ProjectStorageServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStorageServicesImpl.class);

    static final String DEFAULT_DB_BASE_PATH = "./.dashbuilder";
    static final String DB_BASE_PATH = "org.dashbuilder.project.location";

    private Path parent;
    private Path perspectivesPath;
    private Path dataSetsPath;
    private Path navigationPath;
    private Path tempPath;

    public ProjectStorageServicesImpl() {
        init();
    }

    @PostConstruct
    void startup() {
        init();
        createStructure();
    }

    @Override
    public Optional<String> getDataSet(String name) {
        var dsName = name;
        if (!dsName.endsWith(DATASET_EXT)) {
            dsName = name + DATASET_EXT;
        }
        var dsPath = dataSetsPath.resolve(dsName);
        return getContentFrom(dsPath);
    }

    @Override
    @ProjectUpdate
    public void saveDataSet(String name, String content) {
        if (!name.endsWith(DATASET_EXT)) {
            name = name + DATASET_EXT;
        }
        var datasetPath = dataSetsPath.resolve(name);
        try {
            overrideFile(datasetPath, content);
        } catch (IOException e) {
            LOGGER.error("Error saving data set", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @ProjectUpdate    
    public void removeDataSet(String name) {
        var path = dataSetsPath.resolve(name + DATASET_EXT);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Error removing data set", e);
        }
    }

    @Override
    public Map<Path, String> listDataSets() {
        try {
            return Files.list(dataSetsPath)
                    .filter(p -> p.toString().endsWith(DATASET_EXT))
                    .collect(Collectors.toMap(p -> p, this::read));
        } catch (IOException e) {
            throw new RuntimeException("Error listing data sets", e);
        }
    }
    
    @Override
    public Map<Path, String> listAllDataSetsContent() {
        try {
            return Files.list(dataSetsPath)
                    .collect(Collectors.toMap(p -> p, this::read));
        } catch (IOException e) {
            throw new RuntimeException("Error listing data sets and content", e);
        }
    }

    @Override
    public Optional<String> getPerspective(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        var path = perspectivesPath.resolve(name).resolve(PERSPECTIVE_LAYOUT);
        return getContentFrom(path);
    }

    @Override
    @ProjectUpdate    
    public void savePerspective(String name, String content) {
        if (content != null) { 
            var dir = perspectivesPath.resolve(name);
            try {
                Files.createDirectories(dir);
                overrideFile(dir.resolve(PERSPECTIVE_LAYOUT), content);
                overrideFile(dir.resolve(PERSPECTIVE_LAYOUT_PLUGIN), LocalDate.now().toString());
            } catch (IOException e) {
                throw new RuntimeException("Error saving layout.", e);
            }
        }
    }

    @Override
    public Map<Path, String> listPerspectivesPlugins() {
        try {
            var perspectivesPlugins = new HashMap<Path, String>();
             Files.list(perspectivesPath)
                  .filter(p -> p.toFile().isDirectory())
                  .forEach(p -> {
                      var layout = p.resolve(PERSPECTIVE_LAYOUT);
                      var layoutPlugin = p.resolve(PERSPECTIVE_LAYOUT_PLUGIN);
                      perspectivesPlugins.put(layout, read(layout));
                      perspectivesPlugins.put(layoutPlugin, read(layoutPlugin));
                  });
             return perspectivesPlugins;
        } catch (IOException e) {
            throw new RuntimeException("Error listing perspectives", e);
        }
    }
    
    @Override
    public Map<Path, String> listPerspectives() {
        try {
            return Files.list(perspectivesPath)
                    .filter(p -> p.toFile().isDirectory())
                    .map(p -> p.resolve(PERSPECTIVE_LAYOUT))
                    .filter(Files::exists)
                    .collect(Collectors.toMap(p -> p, this::read));
        } catch (IOException e) {
            throw new RuntimeException("Error listing perspectives", e);
        }
    }

    @Override
    @ProjectUpdate    
    public void removePerspective(String name) {
        if (name != null && !name.isBlank()) {
            var dir = perspectivesPath.resolve(name);
            deleteDirContent(dir);
            dir.toFile().delete();
        }
    }

    @Override
    public Path createTempPath(String name) {
        var path = tempPath.resolve(name);
        try {
            Files.deleteIfExists(path);
            return Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp path", e);
        }
    }

    @Override
    public void createTempContent(String name, String content) {
        var path = tempPath.resolve(name);
        try {
            overrideFile(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Error saving temp content", e);
        }
    }

    @Override
    public void removeTempContent(String name) {
        var path = tempPath.resolve(name);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Error removing temp content", e);
        }
    }

    @Override
    public Path getTempPath(String name) {
        return tempPath.resolve(name);
    }

    @Override
    public Optional<String> getNavigation() {
        var navigationFilePath = navigationPath.resolve(NAV_TREE_FILE_NAME);
        return getContentFrom(navigationFilePath);
    }

    @Override
    @ProjectUpdate    
    public void saveNavigation(String content) {
        var navigationFilePath = navigationPath.resolve(NAV_TREE_FILE_NAME);
        try {
            Files.writeString(navigationFilePath, content);
        } catch (IOException e) {
            throw new RuntimeException("Error saving navigation " + navigationFilePath, e);
        }
    }

    @Override
    public void saveDataSetContent(String name, String content) {
        var path = dataSetsPath.resolve(name);
        try {
            overrideFile(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Error saving data set content " + name, e);
        }
    }

    @Override
    public Optional<String> getDataSetContent(String name) {
        var path = dataSetsPath.resolve(name);
        return getContentFrom(path);
    }

    @Override
    public void removeDataSetContent(String name) {
        var path = dataSetsPath.resolve(name);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Error removing data set content", e);
        }
    }

    @Override
    public void clear() {
        if (parent == null) {
            this.init();
        }
        if (parent.toFile().exists()) {
            deleteDirContent(parent);
        }
    }

    @Override
    public void createStructure() {
        try {
            Files.createDirectories(parent);
            Files.createDirectories(perspectivesPath);
            Files.createDirectories(dataSetsPath);
            Files.createDirectories(navigationPath);
            Files.createDirectories(tempPath);
            createReadme(dataSetsPath.getParent());
            createReadme(perspectivesPath);
            createReadme(navigationPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Not able to create base directories", e);
        }
    }
    
    void init() {
        var basePath = System.getProperty(DB_BASE_PATH, DEFAULT_DB_BASE_PATH);
        parent = Paths.get(basePath);
        perspectivesPath = parent.resolve(PERSPECTIVES_PATH);
        dataSetsPath = parent.resolve(Paths.get(DATASETS_PARENT_PATH, DATASETS_PATH));
        navigationPath = parent.resolve(Paths.get(NAVIGATION_PARENT_PATH, NAVIGATION_PATH));
        tempPath = parent.resolve(Paths.get(TEMP_PATH));
    }

    private void createReadme(Path parentPath) throws IOException {
        var readme = parentPath.resolve(README);
        if (!readme.toFile().exists()) {
            Files.createFile(readme);
        }
    }

    private String read(Path p) {
        try {
            return Files.readString(p);
        } catch (IOException e) {
            throw new RuntimeException("Not able to read path content", e);
        }
    }

    private Optional<String> getContentFrom(Path path) {
        if (!path.toFile().exists()) {
            return Optional.empty();
        }
        return Optional.of(read(path));
    }

    private void overrideFile(Path dir, String content) throws IOException {
        Files.writeString(dir,
                          content,
                          StandardOpenOption.CREATE,
                          StandardOpenOption.TRUNCATE_EXISTING,
                          StandardOpenOption.WRITE);
    }

    private void deleteDirContent(Path dir) {
        try {
            if (dir.toFile().exists()) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error removing directory", e);
        }
    }

}
