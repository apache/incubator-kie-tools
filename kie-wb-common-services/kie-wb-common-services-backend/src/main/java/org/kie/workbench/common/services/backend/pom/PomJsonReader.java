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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
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
 * "repositories-add":[
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
 *  "repositories-remove":[
 *     {
 *       "id":"productization-repository", "url":"http://download.lab.bos.redhat.com/brewroot/repos/jb-ip-6.1-build/latest/maven/"
 *     }
 *   ],
 *
 *   "repositories-update-urls":[
 *     {
 *       "id":"guvnor-m2-repo", "url":"http://127.0.0.1:8080/business-central/maven3/"
 *     }
 *   ],
 * "pluginRepositories-add":[
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
 * "pluginRepositories-remove":[],
 * "pluginRepositories-update-urls":[]
 * }
 * </pre>
 */
public class PomJsonReader {

    private final Logger logger = LoggerFactory.getLogger(PomJsonReader.class);
    private String jsonPomFile;
    private String DEPENDENCIES = "dependencies";
    private String REPOSITORIES_ADD = "repositories-add";
    private String REPOSITORIES_REMOVE = "repositories-remove";
    private String REPOSITORIES_UPDATE_URLS = "repositories-update-urls";
    private String PLUGIN_REPOSITORIES_ADD = "pluginRepositories-add";
    private String PLUGIN_REPOSITORIES_REMOVE = "pluginRepositories-remove";
    private String PLUGIN_REPOSITORIES_UPDATE_URLS = "pluginRepositories-update-urls";
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

    public JSONDTO readDepsAndRepos(Model model) {
        List<Dependency> deps = updateDeps();
        List<RepositoryKey> repos = getRepos(model, REPOSITORIES_REMOVE, REPOSITORIES_UPDATE_URLS, REPOSITORIES_ADD);
        List<RepositoryKey> pluginRepos = getRepos(model, PLUGIN_REPOSITORIES_REMOVE, PLUGIN_REPOSITORIES_UPDATE_URLS, PLUGIN_REPOSITORIES_ADD);
        return new JSONDTO(deps, repos, pluginRepos);
    }

    private List<RepositoryKey> getRepos(Model model, String remove, String update, String add){
        Set<RepositoryKey> currentKeys = getSetKeys(model.getPluginRepositories());
        Set<RepositoryKey> reposToRemove = readReposAsAKeys(remove);
        Set<RepositoryKey> reposToUpdate  = readReposUpdate(update);
        Set<RepositoryKey> reposToAdd  = readReposAsAKeys(add);

        if(reposToRemove.size() >0) {
            currentKeys.removeAll(reposToRemove);
        }
        if(reposToUpdate.size() >0){
            update(currentKeys, reposToUpdate);
        }
        if(reposToAdd.size() >0){
            currentKeys.addAll(reposToAdd);
        }
        return new ArrayList<>(currentKeys);
    }

    private void update(Set<RepositoryKey> currentRepositories, Set<RepositoryKey> updates){
        for(RepositoryKey udate: updates){
            for(RepositoryKey repo: currentRepositories){
                if(repo.getRepository().getId().equals(udate.getRepository().getId())){
                    repo.getRepository().setUrl(udate.getRepository().getUrl());
                }
            }
        }
    }

    private Set<RepositoryKey> getSetKeys(List<Repository> currentRepositories){
        Set<RepositoryKey> currentKeys = new HashSet<>();
        for(Repository repo: currentRepositories){
            currentKeys.add(new RepositoryKey(repo));
        }
        return currentKeys;
    }

    private Set<RepositoryKey> readReposAsAKeys(String repoName) {
        JsonArray repositories = pomObject.getJsonArray(repoName);
        Set<RepositoryKey> repos = new HashSet<>(repositories.size());
        for (int i = 0; i < repositories.size(); i++) {
            Repository repo = repoName.endsWith("-add") ? getRepository(repositories, i) : getRepositoryUpdate(repositories, i);
            if (!repo.getId().isEmpty()) {
                repos.add(new RepositoryKey(repo));
            }
        }
        return repos;
    }

    private Set<RepositoryKey> readReposUpdate(String repoName) {
        JsonArray repositories = pomObject.getJsonArray(repoName);
        Set<RepositoryKey> repos = new HashSet<>(repositories.size());
        for (int i = 0; i < repositories.size(); i++) {
            Repository repo = getRepositoryUpdate(repositories, i);
            if (!repo.getId().isEmpty()) {
                repos.add(new RepositoryKey(repo));
            }
        }
        return repos;
    }

    private List<Dependency> updateDeps() {
        JsonArray dependencies = pomObject.getJsonArray(DEPENDENCIES);
        List<Dependency> deps = new ArrayList<>(dependencies.size());
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = getDependency(dependencies, i);
            if (!dependency.getGroupId().isEmpty()) {
                deps.add(dependency);
            }
        }
        return deps;
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

    private Repository getRepositoryUpdate(JsonArray pluginRepositories, int i) {
        Repository repo = new Repository();
        repo.setId(pluginRepositories.getJsonObject(i).getString("id"));
        repo.setUrl(pluginRepositories.getJsonObject(i).getString("url"));
        return repo;
    }
}
