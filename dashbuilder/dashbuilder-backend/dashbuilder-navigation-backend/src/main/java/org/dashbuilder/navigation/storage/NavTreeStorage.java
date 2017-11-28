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

import java.net.URI;
import java.util.HashMap;
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
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class NavTreeStorage {

    private IOService ioService;
    private NavTreeJSONMarshaller jsonMarshaller;
    private FileSystem fileSystem;
    private Path root;
    private Logger log = LoggerFactory.getLogger(NavTreeStorage.class);

    public NavTreeStorage() {
    }

    @Inject
    public NavTreeStorage(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
        this.jsonMarshaller = NavTreeJSONMarshaller.get();
    }

    @PostConstruct
    public void init() {
        try {
            this.fileSystem = ioService.newFileSystem(URI.create("default://plugins"),
                    new HashMap<String, Object>() {{
                        put("init", Boolean.TRUE);
                        put("internal", Boolean.TRUE);
                    }});
        } catch (FileSystemAlreadyExistsException e) {
            this.fileSystem = ioService.getFileSystem(URI.create( "default://plugins"));
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    protected Path getNavRootPath() {
        return root.resolve("navigation");
    }

    protected Path getNavTreePath() {
        return getNavRootPath().resolve("navtree.json");
    }

    public NavTree loadNavTree() {
        Path path = getNavTreePath();
        if (!ioService.exists(path)) {
            return null;
        }
        try {
            String json = ioService.readAllString(path);
            return jsonMarshaller.fromJson(json);
        }
        catch (Exception e) {
            log.error("Error parsing json definition: " + path.getFileName(), e);
            return null;
        }
    }

    public void saveNavTree(NavTree navTree) {
        ioService.startBatch(fileSystem);
        try {
            String json = jsonMarshaller.toJson(navTree).toString();
            Path path = getNavTreePath();
            ioService.write(path, json);
        }
        catch (Exception e) {
            log.error("Can't save the navigation tree.", e);
        }
        finally {
            ioService.endBatch();
        }
    }
}
