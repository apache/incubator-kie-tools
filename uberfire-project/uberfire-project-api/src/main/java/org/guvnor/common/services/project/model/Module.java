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
package org.guvnor.common.services.project.model;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.util.URIUtil;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * An item representing a module.
 * Each module has a pom.xml file and a folder it belongs into, it can have a parent and child modules.
 * Note that the child and parent might not be in the same repository.
 */
@Portable
public class Module {

    protected Path rootPath;
    protected Path pomXMLPath;
    protected Collection<String> modules = new ArrayList<>();
    protected POM pom;

    // only loaded when listing projects
    private int numberOfAssets;

    public Module() {
        //For Errai-marshalling
    }

    public Module(final Path rootPath,
                  final Path pomXMLPath,
                  final POM pom) {
        this(rootPath,
             pomXMLPath);
        this.pom = checkNotNull("pom", pom);
    }

    public Module(final Path rootPath,
                  final Path pomXMLPath,
                  final POM pom,
                  final Collection<String> modules) {
        this(rootPath,
             pomXMLPath,
             pom);
        this.modules = modules;
    }

    public Module(Path rootPath,
                  Path pomXMLPath) {
        this.rootPath = checkNotNull("rootPath", rootPath);
        this.pomXMLPath = checkNotNull("pomXMLPath", pomXMLPath);
    }

    public int getNumberOfAssets() {
        return numberOfAssets;
    }

    public void setNumberOfAssets(int numberOfAssets) {
        this.numberOfAssets = numberOfAssets;
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public Path getPomXMLPath() {
        return this.pomXMLPath;
    }

    public String getModuleName() {
        if (pom != null && pom.getName() != null) {
            return pom.getName();
        } else if (pom != null && pom.getGav() != null && pom.getGav().getArtifactId() != null && !pom.getGav().getArtifactId().trim().isEmpty()) {
            return pom.getGav().getArtifactId();
        } else {
            return getRootPath().getFileName();
        }
    }

    public String getIdentifier() {
        return getRootPath().toURI();
    }

    public String getEncodedIdentifier() {
        return URIUtil.encodeQueryString(getIdentifier());
    }

    public Collection<String> getModules() {
        return modules;
    }

    public POM getPom() {
        return pom;
    }

    public void setPom(POM pom) {
        this.pom = checkNotNull("pom", pom);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.rootPath != null ? this.rootPath.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.pomXMLPath != null ? this.pomXMLPath.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.pom != null ? this.pom.hashCode() : 0);
        hash = ~~hash;
        hash = 17 * hash + (this.modules != null ? this.modules.hashCode() : 0);
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
        final Module other = (Module) obj;
        if (this.rootPath != other.rootPath && (this.rootPath == null || !this.rootPath.equals(other.rootPath))) {
            return false;
        }
        if (this.pomXMLPath != other.pomXMLPath && (this.pomXMLPath == null || !this.pomXMLPath.equals(other.pomXMLPath))) {
            return false;
        }
        if ((this.pom == null) ? (other.pom != null) : !this.pom.equals(other.pom)) {
            return false;
        }
        if (this.modules != other.modules && (this.modules == null || !this.modules.equals(other.modules))) {
            return false;
        }
        return true;
    }
}
