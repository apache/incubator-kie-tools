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

package org.guvnor.structure.organizationalunit;

import java.util.Collection;
import java.util.List;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.spaces.Space;

@Remote
public interface OrganizationalUnitService {

    OrganizationalUnit getOrganizationalUnit(final String name);

    /**
     * Get all the OUs. Security checks are omitted.
     */
    Collection<OrganizationalUnit> getAllOrganizationalUnits();

    Collection<Space> getAllUserSpaces();

    /**
     * Get only those OUs available within the current security context.
     */
    Collection<OrganizationalUnit> getOrganizationalUnits();

    OrganizationalUnit createOrganizationalUnit(final String name,
                                                final String defaultGroupId);

    OrganizationalUnit createOrganizationalUnit(final String name,
                                                final String defaultGroupId,
                                                final Collection<Repository> repositories);

    OrganizationalUnit createOrganizationalUnit(final String name,
                                                final String defaultGroupId,
                                                final Collection<Repository> repositories,
                                                final Collection<Contributor> contributors);

    OrganizationalUnit updateOrganizationalUnit(final String name,
                                                final String defaultGroupId);

    OrganizationalUnit updateOrganizationalUnit(final String name,
                                                final String defaultGroupId,
                                                final Collection<Contributor> contributors);

    void addRepository(final OrganizationalUnit organizationalUnit,
                       final Repository repository);

    void removeRepository(final OrganizationalUnit organizationalUnit,
                          final Repository repository);

    void addGroup(final OrganizationalUnit organizationalUnit,
                  final String group);

    void removeGroup(final OrganizationalUnit organizationalUnit,
                     final String group);

    void removeOrganizationalUnit(final String name);

    OrganizationalUnit getParentOrganizationalUnit(final Repository repository);

    List<OrganizationalUnit> getOrganizationalUnits(final Repository repository);

    String getSanitizedDefaultGroupId(final String proposedGroupId);

    Boolean isValidGroupId(final String proposedGroupId);
}
