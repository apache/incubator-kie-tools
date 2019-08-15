/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.storage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class NavTreeStorage {

    public static final String NAV_TREE_FILE_NAME = "navtree.json";
    private IOService ioService;
    private NavTreeJSONMarshaller jsonMarshaller;
    private Path root;
    private Logger log = LoggerFactory.getLogger(NavTreeStorage.class);
    private FileSystem fileSystem;

    public NavTreeStorage() {
    }

    @Inject
    public NavTreeStorage(@Named("ioStrategy") IOService ioService,
                          @Named("navigationFS") FileSystem fileSystem) {
        this.ioService = ioService;
        this.fileSystem = fileSystem;
        this.jsonMarshaller = NavTreeJSONMarshaller.get();
    }

    @PostConstruct
    public void init() {
        root = fileSystem.getRootDirectories().iterator().next();
    }

    protected Path getNavRootPath() {
        return root.resolve("navigation");
    }

    protected Path getNavTreePath() {
        return getNavRootPath().resolve(NAV_TREE_FILE_NAME);
    }

    public NavTree loadNavTree() {
        Path path = getNavTreePath();
        if (!ioService.exists(path)) {
            return null;
        }
        try {
            String json = ioService.readAllString(path);
            return jsonMarshaller.fromJson(json);
        } catch (Exception e) {
            log.error("Error parsing json definition: " + path.getFileName(),
                      e);
            return null;
        }
    }

    public void saveNavTree(NavTree navTree) {
        ioService.startBatch(fileSystem);
        try {
            String json = jsonMarshaller.toJson(navTree).toString();
            Path path = getNavTreePath();
            ioService.write(path,
                            json);
        } catch (Exception e) {
            log.error("Can't save the navigation tree.",
                      e);
        } finally {
            ioService.endBatch();
        }
    }
}
