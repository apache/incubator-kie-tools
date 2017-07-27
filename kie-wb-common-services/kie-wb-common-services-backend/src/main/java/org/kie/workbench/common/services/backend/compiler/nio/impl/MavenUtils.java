/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class MavenUtils {

    private static final Logger logger = LoggerFactory.getLogger(MavenUtils.class);

    private final static String POM_NAME = "pom.xml";

    public static List<Artifact> resolveDependenciesFromMultimodulePrj(List<String> pomsPaths) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        List<Artifact> deps = new ArrayList<>();
        try {
            for (String pomx : pomsPaths) {
                Path pom = Paths.get(pomx);
                Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
                if (model.getDependencyManagement() != null && model.getDependencyManagement().getDependencies() != null) {
                    createArtifacts(model.getDependencyManagement().getDependencies(),
                                    deps);
                }
                if (model.getDependencies() != null) {
                    createArtifacts(model.getDependencies(),
                                    deps);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return Collections.emptyList();
        }
        return deps;
    }

    private static void createArtifacts(List<Dependency> pomDeps,
                                        List<Artifact> deps) {
        if (pomDeps != null && pomDeps.size() > 0) {
            for (Dependency dep : pomDeps) {
                Artifact artifact = new DefaultArtifact(dep.getGroupId(),
                                                        dep.getArtifactId(),
                                                        dep.getVersion(),
                                                        dep.getScope(),
                                                        dep.getType(),
                                                        dep.getClassifier(),
                                                        new DefaultArtifactHandler());
                deps.add(artifact);
            }
        }
    }

    public static void searchPoms(Path file,
                                  List<String> pomsList) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    searchPoms(p,
                               pomsList);
                } else if (p.endsWith(POM_NAME)) {
                    pomsList.add(p.toAbsolutePath().toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
