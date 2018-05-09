/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.pom;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It reads the pom-migrations.json with dependencies, repositories and plugin repositories with the following format
 *
 * <pre>
 * {
 * "dependencies":[
 * {"groupId":"junit", "artifactId":"junit", "version":"4.12", "scope":"test"},
 * ],
 * "repositories":[
 * {
 * "id":"jboss-public-repository-group",
 * "name":"JBoss Public Repository Group",
 * "url":"http://repository.jboss.org/nexus/content/groups/public/",
 * "releasesEnabled":true,
 * "releasesUpdatePolicy":"never",
 * "snapshotEnabled":true,
 * "snapshotUpdatePolicy":"never"
 * }
 * ],
 * "pluginRepositories":[
 * {
 * "id":"jboss-public-repository-group",
 * "name":"JBoss Public Repository Group",
 * "url":"http://repository.jboss.org/nexus/content/groups/public/",
 * "releasesEnabled":true,
 * "releasesUpdatePolicy":"never",
 * "snapshotEnabled":true,
 * "snapshotUpdatePolicy":"never"
 * }
 * ]
 * }
 * </pre>
 */
public class PomJsonReader {

    private final Logger logger = LoggerFactory.getLogger(PomJsonReader.class);
    private String jsonPomFile;
    private String DEPENDENCIES = "dependencies";
    private String REPOSITORIES = "repositories";
    private String PLUGIN_REPOSITORIES = "pluginRepositories";
    private JsonObject pomObject;

    public PomJsonReader(String path, String jsonName) {
        jsonPomFile = jsonName;
        if (!path.endsWith(jsonPomFile)) {
            throw new RuntimeException("no " + jsonPomFile + " in the provided path :" + path);
        }
        InputStream fis = null;
        JsonReader reader = null;
        try {
            fis = new FileInputStream(path);
            reader = Json.createReader(fis);
            pomObject = reader.readObject();
            reader.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    //suppressed
                }
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public PomJsonReader(InputStream in) {

        try (JsonReader reader = Json.createReader(in)) {
            pomObject = reader.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public JSONDTO readDepsAndRepos() {
        JsonArray dependencies = pomObject.getJsonArray(DEPENDENCIES);
        List<Dependency> deps = new ArrayList<>(dependencies.size());
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = getDependency(dependencies, i);
            if (!dependency.getGroupId().isEmpty()) {
                deps.add(dependency);
            }
        }

        JsonArray repositories = pomObject.getJsonArray(REPOSITORIES);
        List<Repository> repos = new ArrayList<>(repositories.size());
        for (int i = 0; i < repositories.size(); i++) {
            Repository repo = getRepository(repositories, i);
            repos.add(repo);
        }

        JsonArray pluginRepositories = pomObject.getJsonArray(PLUGIN_REPOSITORIES);
        List<Repository> pluginRepos = new ArrayList<>(pluginRepositories.size());
        for (int i = 0; i < pluginRepositories.size(); i++) {
            Repository repo = getRepository(pluginRepositories, i);
            pluginRepos.add(repo);
        }
        return new JSONDTO(deps, repos, pluginRepos);
    }

    public JSONDTO readDeps() {
        JsonArray dependencies = pomObject.getJsonArray(DEPENDENCIES);
        List<Dependency> deps = new ArrayList<>(dependencies.size());
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = getDependency(dependencies, i);
            if (!dependency.getGroupId().isEmpty()) {
                deps.add(dependency);
            }
        }
        return new JSONDTO(deps, Collections.emptyList(), Collections.emptyList());
    }

    private Dependency getDependency(JsonArray dependencies, int i) {
        Dependency dependency = new Dependency();
        String groupId = dependencies.getJsonObject(i).getString("groupId");
        String artifactId = dependencies.getJsonObject(i).getString("artifactId");
        String version = dependencies.getJsonObject(i).getString("version");
        String scope = dependencies.getJsonObject(i).getString("scope");

        if (groupId != null && artifactId != null) {
            dependency.setGroupId(groupId);
            dependency.setArtifactId(artifactId);
            if (version != null) {
                dependency.setVersion(version);
            }

            if (scope != null) {
                dependency.setScope(scope);
            }
        } else {
            return new Dependency();
        }
        return dependency;
    }

    private Repository getRepository(JsonArray pluginRepositories, int i) {
        Repository repo = new Repository();
        repo.setId(pluginRepositories.getJsonObject(i).getString("id"));
        repo.setName(pluginRepositories.getJsonObject(i).getString("name"));
        repo.setUrl(pluginRepositories.getJsonObject(i).getString("url"));
        RepositoryPolicy releases = new RepositoryPolicy();
        releases.setEnabled(pluginRepositories.getJsonObject(i).getBoolean("releasesEnabled"));
        releases.setUpdatePolicy(pluginRepositories.getJsonObject(i).getString("releasesUpdatePolicy"));
        RepositoryPolicy snapshots = new RepositoryPolicy();
        snapshots.setEnabled(pluginRepositories.getJsonObject(i).getBoolean("snapshotEnabled"));
        snapshots.setUpdatePolicy(pluginRepositories.getJsonObject(i).getString("snapshotUpdatePolicy"));
        repo.setSnapshots(snapshots);
        return repo;
    }
}
