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

package org.kie.workbench.common.screens.library.api;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectInfo {

    private OrganizationalUnit organizationalUnit;

    private Repository repository;

    private String branch;

    private Project project;

    public ProjectInfo( @MapsTo( "organizationalUnit" ) final OrganizationalUnit organizationalUnit,
                        @MapsTo( "repository" ) final Repository repository,
                        @MapsTo( "branch" ) final String branch,
                        @MapsTo( "project" ) final Project project ) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getBranch() {
        return branch;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ProjectInfo ) ) {
            return false;
        }

        final ProjectInfo that = (ProjectInfo) o;

        if ( getOrganizationalUnit() != null ? !getOrganizationalUnit().equals( that.getOrganizationalUnit() ) : that.getOrganizationalUnit() != null ) {
            return false;
        }
        if ( getRepository() != null ? !getRepository().equals( that.getRepository() ) : that.getRepository() != null ) {
            return false;
        }
        if ( getBranch() != null ? !getBranch().equals( that.getBranch() ) : that.getBranch() != null ) {
            return false;
        }
        return !( getProject() != null ? !getProject().equals( that.getProject() ) : that.getProject() != null );

    }

    @Override
    public int hashCode() {
        int result = getOrganizationalUnit() != null ? getOrganizationalUnit().hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( getRepository() != null ? getRepository().hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( getBranch() != null ? getBranch().hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( getProject() != null ? getProject().hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
