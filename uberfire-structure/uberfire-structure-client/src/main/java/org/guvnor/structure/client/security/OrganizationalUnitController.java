/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.security;

import java.util.Collection;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class OrganizationalUnitController {

    public static final String ORG_UNIT_TYPE = "orgunit";
    public static final String ORG_UNIT_CREATE = "create";
    public static final String ORG_UNIT_READ = "read";
    public static final String ORG_UNIT_UPDATE = "update";
    public static final String ORG_UNIT_DELETE = "delete";

    private AuthorizationManager authorizationManager;
    private User user;

    @Inject
    public OrganizationalUnitController(AuthorizationManager authorizationManager,
                                        User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public boolean canCreateOrgUnits() {
        return authorizationManager.authorize(OrganizationalUnit.RESOURCE_TYPE,
                                              OrganizationalUnitAction.CREATE,
                                              user);
    }

    public boolean canReadOrgUnits() {
        return authorizationManager.authorize(OrganizationalUnit.RESOURCE_TYPE,
                                              OrganizationalUnitAction.READ,
                                              user);
    }

    public boolean canReadOrgUnit(final OrganizationalUnit organizationalUnit) {
        final boolean securityPermission = authorizationManager.authorize(organizationalUnit,
                                              OrganizationalUnitAction.READ,
                                              user);
        return securityPermission
                || userIsAtLeast(ContributorType.CONTRIBUTOR, organizationalUnit.getContributors());
    }

    public boolean canUpdateOrgUnit(OrganizationalUnit organizationalUnit) {
        final boolean securityPermission = authorizationManager.authorize(organizationalUnit,
                                              OrganizationalUnitAction.UPDATE,
                                              user);
        return securityPermission
                || userIsAtLeast(ContributorType.ADMIN, organizationalUnit.getContributors());
    }

    public boolean canDeleteOrgUnit(OrganizationalUnit organizationalUnit) {
        final boolean securityPermission = authorizationManager.authorize(organizationalUnit,
                                              OrganizationalUnitAction.DELETE,
                                              user);

        return securityPermission
                || userIsAtLeast(ContributorType.OWNER, organizationalUnit.getContributors());
    }

    boolean userIsAtLeast(final ContributorType type,
                          final Collection<Contributor> contributors) {
        return contributors.stream().anyMatch(c -> c.getUsername().equals(user.getIdentifier())
                && ContributorType.PRIORITY_ORDER.indexOf(c.getType()) <= ContributorType.PRIORITY_ORDER.indexOf(type));
    }

    Optional<Contributor> getUserContributor(final Collection<Contributor> contributors) {
        return contributors.stream().filter(c -> c.getUsername().equals(user.getIdentifier())).findFirst();
    }
}