/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.backend.builder.core;

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
