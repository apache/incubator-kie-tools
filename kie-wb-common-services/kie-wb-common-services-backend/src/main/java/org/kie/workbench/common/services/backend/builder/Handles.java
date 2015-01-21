package org.kie.workbench.common.services.backend.builder;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.vfs.Path;

class Handles {

    public final static String RESOURCE_PATH = "src/main/resources";

    private Map<String, Path> handles = new HashMap<String, Path>();

    void put(String baseFileName, Path path) {
        handles.put(baseFileName, path);
    }

    Path get(String pathToResource) {
        return handles.get(pathToResource);
    }

    void remove(String pathToResource) {
        handles.remove(pathToResource);
    }
}
