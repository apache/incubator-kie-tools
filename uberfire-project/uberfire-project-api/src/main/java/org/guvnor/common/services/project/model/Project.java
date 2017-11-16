/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collection;

import org.guvnor.common.services.project.security.ProjectResourceType;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.RuntimeContentResource;
import org.uberfire.util.URIUtil;

/**
 * An item representing a project
 */
@Portable
public class Project implements RuntimeContentResource,
                                Cacheable {

    public static final ProjectResourceType RESOURCE_TYPE = new ProjectResourceType();

    protected Path rootPath;
    protected Path pomXMLPath;
    protected String projectName;

    protected Collection<String> modules = new ArrayList<String>();
    private Collection<String> groups = new ArrayList<String>();

    private boolean requiresRefresh = true;

    // only loaded by ProjectService.getProjects()
    private POM pom;

    // only loaded when listing projects
    private int numberOfAssets;

    public Project() {
        //For Errai-marshalling
    }

    public Project(final Path rootPath,
                   final Path pomXMLPath,
                   final String projectName) {
        this.rootPath = PortablePreconditions.checkNotNull("rootPath",
                                                           rootPath);
        this.pomXMLPath = PortablePreconditions.checkNotNull("pomXMLPath",
                                                             pomXMLPath);
        this.projectName = PortablePreconditions.checkNotNull("projectName",
                                                              projectName);
    }

    public Project(final Path rootPath,
                   final Path pomXMLPath,
                   final String projectName,
                   final Collection<String> modules) {
        this(rootPath,
             pomXMLPath,
             projectName);
        this.modules = modules;
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public Path getPomXMLPath() {
        return this.pomXMLPath;
    }

    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getIdentifier() {
        return getRootPath().toURI();
    }

    public String getEncodedIdentifier() {
        return URIUtil.encodeQueryString(getIdentifier());
    }

    @Override
    public ResourceType getResourceType() {
        return RESOURCE_TYPE;
    }

    public Collection<String> getGroups() {
        return groups;
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    public Collection<String> getModules() {
        return modules;
    }

    public POM getPom() {
        return pom;
    }

    public void setPom(POM pom) {
        this.pom = pom;
    }

    public int getNumberOfAssets() {
        return numberOfAssets;
    }

    public void setNumberOfAssets(int numberOfAssets) {
        this.numberOfAssets = numberOfAssets;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.rootPath != null ? this.rootPath.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.pomXMLPath != null ? this.pomXMLPath.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.projectName != null ? this.projectName.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.modules != null ? this.modules.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.groups != null ? this.groups.hashCode() : 0);
        hash = ~~hash;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Project other = (Project) obj;
        if (this.rootPath != other.rootPath && (this.rootPath == null || !this.rootPath.equals(other.rootPath))) {
            return false;
        }
        if (this.pomXMLPath != other.pomXMLPath && (this.pomXMLPath == null || !this.pomXMLPath.equals(other.pomXMLPath))) {
            return false;
        }
        if ((this.projectName == null) ? (other.projectName != null) : !this.projectName.equals(other.projectName)) {
            return false;
        }
        if (this.modules != other.modules && (this.modules == null || !this.modules.equals(other.modules))) {
            return false;
        }
        if (this.groups != other.groups && (this.groups == null || !this.groups.equals(other.groups))) {
            return false;
        }
        return true;
    }
}
