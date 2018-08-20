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

package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.appformer.maven.integration.Aether;
import org.eclipse.aether.repository.RemoteRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.compiler.impl.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

/***
 * Utils to support Maven work
 */
public class MavenUtils {

    private static final Logger logger = LoggerFactory.getLogger(MavenUtils.class);

    public static List<Artifact> resolveDependenciesFromMultimodulePrj(List<String> pomsPaths) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Set<Artifact> deps = new HashSet<>();
        try {
            for (String pomx : pomsPaths) {
                Path pom = Paths.get(URI.create("default:///" + pomx));
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
        return new ArrayList<>(deps);
    }

    private static void createArtifacts(List<Dependency> pomDeps,
                                        Set<Artifact> deps) {
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

    public static List<String> searchPoms(Path file) {
        List<String> poms = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    poms.addAll(searchPoms(p));
                } else if (p.endsWith(CommonConstants.POM_NAME)) {
                    poms.add(p.toAbsolutePath().toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return poms;
    }

    public static String getMavenRepoDir(MavenRepos repo) {
        String repository;
        Aether defaultAether = Aether.getAether();
        switch (repo) {
            case LOCAL:
                repository = defaultAether.getLocalRepository().getUrl();
                break;
            case GLOBAL:
                repository = getGlobalRepo(defaultAether.getRepositories());
                break;
            default:
                repository = getTempRepo();
        }

        if (repository == null || repository.isEmpty()) {
            repository = getTempRepo();
        }
        if (!Files.exists(Paths.get(repository))) {
            Files.createDirectories(Paths.get(repository));
        }

        return repository;
    }

    private static String getTempRepo() {
        String tempDir = System.getProperty("java.io.tmpdir");
        StringBuffer sb = new StringBuffer();
        sb.append(tempDir).append(tempDir.endsWith(CommonConstants.SEPARATOR) ? "" : CommonConstants.SEPARATOR).append("maven/repository/");
        return sb.toString();
    }

    private static String getGlobalRepo(Collection<RemoteRepository> remoteRepos) {
        String mavenRepo = "";
        for (RemoteRepository item : remoteRepos) {
            if (item.getId().startsWith(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)) {
                mavenRepo = item.getUrl();
                break;
            }
        }
        if (mavenRepo.isEmpty()) {
            String envVar = System.getProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
            if (envVar != null) {
                mavenRepo = envVar;
            }
        }
        return mavenRepo;
    }
}
