/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class POM {

    private static final String MODEL_VERSION = "4.0.0";

    private GAV parent;
    private GAV gav;
    private String name;
    private String description;
    private String url;

    private String packaging;

    private Build build;

    private List<Dependency> dependencies = new ArrayList<>();
    private List<MavenRepository> repositories = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    public POM() {
        this.gav = new GAV();
    }

    // Kept this for backwards compatibility
    public POM(final GAV gav) {
        this(null,
             null,
             null,
             gav,
             false);
    }

    public POM(final String name,
               final String description,
               final String url,
               final GAV gav
               ) {
        this(name,
             description,
             url,
             gav,
             false);
    }

    public POM(final String name,
               final String description,
               final String url,
               final GAV gav,
               final boolean multiModule) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.gav = gav;
        if (multiModule) {
            packaging = "pom";
        }
    }

    public GAV getGav() {
        return gav;
    }

    public Dependencies getDependencies() {
        return new Dependencies(dependencies);
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void addRepository(MavenRepository mavenRepository) {
        repositories.add(mavenRepository);
    }

    public List<MavenRepository> getRepositories() {
        return repositories;
    }

    public String getModelVersion() {
        return MODEL_VERSION;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GAV getParent() {
        return parent;
    }

    public void setParent(GAV parent) {
        this.parent = parent;
    }

    public List<String> getModules() {
        return modules;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public boolean isMultiModule() {
        return "pom".equals(packaging);
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getPackaging() {
        return packaging;
    }

    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        POM pom = (POM) o;

        if (packaging != null ? !packaging.equals(pom.packaging) : pom.packaging != null) {
            return false;
        }
        if (dependencies != null ? !dependencies.equals(pom.dependencies) : pom.dependencies != null) {
            return false;
        }
        if (description != null ? !description.equals(pom.description) : pom.description != null) {
            return false;
        }
        if (gav != null ? !gav.equals(pom.gav) : pom.gav != null) {
            return false;
        }
        if (modules != null ? !modules.equals(pom.modules) : pom.modules != null) {
            return false;
        }
        if (name != null ? !name.equals(pom.name) : pom.name != null) {
            return false;
        }
        if (parent != null ? !parent.equals(pom.parent) : pom.parent != null) {
            return false;
        }
        if (repositories != null ? !repositories.equals(pom.repositories) : pom.repositories != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (gav != null ? gav.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (packaging != null ? packaging.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (repositories != null ? repositories.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (modules != null ? modules.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
