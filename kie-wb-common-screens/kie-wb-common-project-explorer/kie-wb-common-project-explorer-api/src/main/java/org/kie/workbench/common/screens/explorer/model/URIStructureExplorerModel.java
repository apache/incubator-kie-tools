/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.model;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class URIStructureExplorerModel {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private Project project;

    public URIStructureExplorerModel() {
    };

    public URIStructureExplorerModel( OrganizationalUnit ou,
                                      Repository repository,
                                      Project project ) {
        this.organizationalUnit = ou;
        this.repository = repository;
        this.project = project;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public Repository getRepository() {
        return repository;
    }

    public Project getProject() {
        return project;
    }
}
