/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.backend.pom;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.guvnor.structure.pom.DependencyType;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Paths;

public class PomJsonReaderDefault implements PomJsonReader {

    private final Logger logger = LoggerFactory.getLogger(PomJsonReaderDefault.class);
    private JsonObject pomObject;

    public PomJsonReaderDefault(InputStream in) {
        try (JsonReader reader = Json.createReader(in)) {
            pomObject = reader.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(),
                         e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public PomJsonReaderDefault(String path,
                                String jsonName) {
        String jsonPath = path + jsonName;
        if (!Files.exists(Paths.get(jsonPath))) {
            throw new RuntimeException("no " + jsonName + " in the provided path :" + path);
        }

        try (FileInputStream fis = new FileInputStream(jsonPath);
             JsonReader reader = Json.createReader(fis)) {
            pomObject = reader.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<DependencyType, List<DynamicPomDependency>> readDeps() {
        JsonArray dependencies = pomObject.getJsonArray("dependencies");
        Map<DependencyType, List<DynamicPomDependency>> mapping = new HashMap<>(dependencies.size());
        for (int i = 0; i < dependencies.size(); i++) {
            JsonObject depType = dependencies.getJsonObject(i);
            String type = depType.getString("type");
            JsonArray deps = depType.getJsonArray("deps");
            ArrayList<DynamicPomDependency> dynamic = new ArrayList<>(deps.size());
            for (int k = 0; k < deps.size(); k++) {
                JsonObject dep = deps.getJsonObject(i);
                DynamicPomDependency dynamicDep = new DynamicPomDependency(
                        dep.getString("groupId"),
                        dep.getString("artifactId"),
                        dep.getString("version"),
                        dep.getString("scope")
                );
                dynamic.add(dynamicDep);
            }

            mapping.put(DependencyType.valueOf(type),
                        dynamic);
        }
        return mapping;
    }
}
