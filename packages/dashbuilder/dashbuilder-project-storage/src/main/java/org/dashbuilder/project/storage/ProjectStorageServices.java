package org.dashbuilder.project.storage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public interface ProjectStorageServices {

    static final String EXPORT_BASE_PATH = "dashbuilder";

    static final String NAV_TREE_FILE_NAME = "navtree.json";

    static final String DATASET_EXT = ".dset";

    static final String DATASETS_PARENT_PATH = "datasets";
    static final String DATASETS_PATH = "definitions";

    static final String PERSPECTIVES_PATH = "perspectives";
    static final String PERSPECTIVE_LAYOUT = "perspective_layout";
    static final String PERSPECTIVE_LAYOUT_PLUGIN = PERSPECTIVE_LAYOUT + ".plugin";

    static final String NAVIGATION_PARENT_PATH = "navigation";
    static final String NAVIGATION_PATH = "navigation";
    
    static final String README = "readme.md";

    static final String TEMP_PATH = "tmp";

    Optional<String> getDataSet(String name);

    void saveDataSet(String name, String content);

    void removeDataSet(String name);

    Map<Path, String> listDataSets();
    
    Map<Path, String> listAllDataSetsContent();

    Optional<String> getDataSetContent(String name);

    void removeDataSetContent(String name);

    void saveDataSetContent(String name, String content);

    Optional<String> getPerspective(String name);

    void savePerspective(String name, String content);

    Map<Path, String> listPerspectives();
    
    Map<Path, String> listPerspectivesPlugins();
    
    void removePerspective(String name);

    Optional<String> getNavigation();

    void saveNavigation(String content);

    void createTempContent(String name, String content);

    Path createTempPath(String name);

    void removeTempContent(String name);

    Path getTempPath(String name);

    void clear();

    void createStructure();

    static Path getNavigationExportPath() {
        return Paths.get(EXPORT_BASE_PATH, NAVIGATION_PARENT_PATH, NAVIGATION_PATH);
    }

    static Path getDatasetsExportPath() {
        return Paths.get(EXPORT_BASE_PATH, DATASETS_PARENT_PATH, DATASETS_PATH);
    }

    static Path getPerspectivesExportPath() {
        return Paths.get(EXPORT_BASE_PATH, PERSPECTIVES_PATH);
    }

}
