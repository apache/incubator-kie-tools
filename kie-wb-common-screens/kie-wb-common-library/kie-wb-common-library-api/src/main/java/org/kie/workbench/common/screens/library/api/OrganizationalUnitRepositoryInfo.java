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

import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class OrganizationalUnitRepositoryInfo {

    private Collection<OrganizationalUnit> organizationalUnits;

    private OrganizationalUnit selectedOrganizationalUnit;

    private Collection<Repository> repositories;

    private Repository selectedRepository;

    public OrganizationalUnitRepositoryInfo( @MapsTo( "organizationalUnits" ) final Collection<OrganizationalUnit> organizationalUnits,
                                             @MapsTo( "selectedOrganizationalUnit" ) final OrganizationalUnit selectedOrganizationalUnit,
                                             @MapsTo( "repositories" ) final Collection<Repository> repositories,
                                             @MapsTo( "repository" ) final Repository repository ) {
        this.organizationalUnits = organizationalUnits;
        this.selectedOrganizationalUnit = selectedOrganizationalUnit;
        this.repositories = repositories;
        this.selectedRepository = repository;
    }

    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return organizationalUnits;
    }

    public OrganizationalUnit getSelectedOrganizationalUnit() {
        return selectedOrganizationalUnit;
    }

    public void setSelectedOrganizationalUnit( final OrganizationalUnit selectedOrganizationalUnit ) {
        this.selectedOrganizationalUnit = selectedOrganizationalUnit;
    }

    public Collection<Repository> getRepositories() {
        return repositories;
    }

    public Repository getSelectedRepository() {
        return selectedRepository;
    }

    public void setSelectedRepository( final Repository selectedRepository ) {
        this.selectedRepository = selectedRepository;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof OrganizationalUnitRepositoryInfo ) ) {
            return false;
        }

        final OrganizationalUnitRepositoryInfo that = (OrganizationalUnitRepositoryInfo) o;

        if ( getOrganizationalUnits() != null ? !getOrganizationalUnits().equals( that.getOrganizationalUnits() ) : that.getOrganizationalUnits() != null ) {
            return false;
        }
        if ( getSelectedOrganizationalUnit() != null ? !getSelectedOrganizationalUnit().equals( that.getSelectedOrganizationalUnit() ) : that.getSelectedOrganizationalUnit() != null ) {
            return false;
        }
        if ( getRepositories() != null ? !getRepositories().equals( that.getRepositories() ) : that.getRepositories() != null ) {
            return false;
        }
        return !( getSelectedRepository() != null ? !getSelectedRepository().equals( that.getSelectedRepository() ) : that.getSelectedRepository() != null );

    }

    @Override
    public int hashCode() {
        int result = getOrganizationalUnits() != null ? getOrganizationalUnits().hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( getSelectedOrganizationalUnit() != null ? getSelectedOrganizationalUnit().hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( getRepositories() != null ? getRepositories().hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( getSelectedRepository() != null ? getSelectedRepository().hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
