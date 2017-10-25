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
package org.guvnor.common.services.project.context;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An event raised when the Project Context changes
 */
@Portable
public class ProjectContextChangeEvent {

    private final OrganizationalUnit ou;
    private final Repository repository;
    private final String branch;
    private final Project project;
    private final Package pkg;

    public ProjectContextChangeEvent() {
        ou = null;
        repository = null;
        project = null;
        pkg = null;
        branch = null;
    }

    public ProjectContextChangeEvent(final OrganizationalUnit ou) {
        this(ou,
             null,
             null);
    }

    public ProjectContextChangeEvent(final OrganizationalUnit ou,
                                     final Repository repository,
                                     final String branch) {
        this(ou,
             repository,
             branch,
             null);
    }

    public ProjectContextChangeEvent(final OrganizationalUnit ou,
                                     final Repository repository,
                                     final String branch,
                                     final Project project) {
        this(ou,
             repository,
             branch,
             project,
             null);
    }

    public ProjectContextChangeEvent(final OrganizationalUnit ou,
                                     final Repository repository,
                                     final String branch,
                                     final Project project,
                                     final Package pkg) {
        this.ou = ou;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
        this.pkg = pkg;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return ou;
    }

    public Repository getRepository() {
        return repository;
    }

    public Project getProject() {
        return project;
    }

    public Package getPackage() {
        return pkg;
    }

    public String getBranch() {
        return this.branch;
    }
}
