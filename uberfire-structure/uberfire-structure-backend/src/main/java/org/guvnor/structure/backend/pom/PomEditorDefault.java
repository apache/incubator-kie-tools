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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.StandardOpenOption;

public class PomEditorDefault implements PomEditor {

    private static final String DELIMITER = ":";
    private final Logger logger = LoggerFactory.getLogger(PomEditorDefault.class);

    private MavenXpp3Reader reader;
    private MavenXpp3Writer writer;

    public PomEditorDefault() {
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
    }

    public boolean addDependency(DynamicPomDependency dep,
                                 Path pomPath) {
        if (dep == null || !isGroupIDValid(dep) || !isArtifactIDValid(dep)) {
            return false;
        }

        try {
            Dependency pomDep = getMavenDependency(dep);
            org.uberfire.java.nio.file.Path filePath = Paths.get(pomPath.toURI());
            Model model = getPOMModel(filePath);
            Map<String, String> keys = getKeysFromDeps(model.getDependencies());
            String keyDep = getKeyFromDep(dep);
            if (!keys.containsKey(keyDep)) {
                model.getDependencies().add(pomDep);
            } else {
                //override dep version with the version contained in the json
                String versionKey = keys.get(keyDep);
                List<Dependency> modelDeps = model.getDependencies();
                for (Dependency modelDep : modelDeps) {
                    if (modelDep.getGroupId().equals(dep.getGroupID()) && modelDep.getArtifactId().equals(dep.getArtifactID())) {
                        modelDep.setVersion(versionKey);
                    }
                }
                return false;
            }
            writePOMModelOnFS(filePath,
                              model);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),
                         ex);
            return false;
        }
        return true;
    }

    public boolean addDependencies(List<DynamicPomDependency> deps,
                                   Path pomPath) {
        if (deps.isEmpty()) {
            return false;
        }
        boolean result = false;
        try {
            org.uberfire.java.nio.file.Path filePath = Paths.get(pomPath.toURI());
            Model model = getPOMModel(filePath);
            Map<String, String> keys = getKeysFromDeps(model.getDependencies());

            for (DynamicPomDependency dep : deps) {
                String keyDep = getKeyFromDep(dep);
                if (!keys.containsKey(keyDep)) {
                    Dependency pomDep = getMavenDependency(dep);
                    model.getDependencies().add(pomDep);
                    result = true;
                } else {
                    //override dep version with the version contained in the json
                    List<Dependency> modelDeps = model.getDependencies();
                    for (Dependency modelDep : modelDeps) {
                        if (modelDep.getGroupId().equals(dep.getGroupID()) && modelDep.getArtifactId().equals(dep.getArtifactID()) && !modelDep.getVersion().equals(dep.getVersion())) {
                            modelDep.setVersion(dep.getVersion());
                            result = true;
                        }
                    }
                }
            }
            if (result) {
                writePOMModelOnFS(filePath,
                                  model);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),
                         ex);
            result = false;
        }
        return result;
    }

    private String getKeyFromDep(DynamicPomDependency dep) {
        StringBuilder sb = new StringBuilder();
        sb.append(dep.getGroupID()).append(DELIMITER).append(dep.getArtifactID());
        return sb.toString();
    }

    private Map<String, String> getKeysFromDeps(List<Dependency> deps) {
        Map<String, String> depsMap = new HashMap(deps.size());
        for (Dependency dep : deps) {
            StringBuilder sb = new StringBuilder();
            sb.append(dep.getGroupId()).append(DELIMITER).append(dep.getArtifactId());
            depsMap.put(sb.toString(),
                        dep.getVersion());
        }
        return depsMap;
    }

    private void writePOMModelOnFS(org.uberfire.java.nio.file.Path filePath,
                                   Model model) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(baos,
                     model);
        Files.write(filePath,
                    baos.toByteArray(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
    }

    private Model getPOMModel(org.uberfire.java.nio.file.Path filePath) throws IOException, XmlPullParserException {
        return reader.read(new ByteArrayInputStream(Files.readAllBytes(filePath)));
    }

    private Dependency getMavenDependency(DynamicPomDependency dep) {
        Dependency pomDep = new Dependency();
        pomDep.setGroupId(dep.getGroupID());
        pomDep.setArtifactId(dep.getArtifactID());
        if (!dep.getVersion().isEmpty()) {
            pomDep.setVersion(dep.getVersion());
        }
        return pomDep;
    }

    private boolean isGroupIDValid(DynamicPomDependency dep) {
        return dep.getGroupID() != null && !dep.getGroupID().isEmpty();
    }

    private boolean isArtifactIDValid(DynamicPomDependency dep) {
        return dep.getArtifactID() != null && !dep.getArtifactID().isEmpty();
    }
}
