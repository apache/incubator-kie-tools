/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.social.hp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialSecurityConstraint;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class SocialEventOUConstraint implements SocialSecurityConstraint {

    private OrganizationalUnitService organizationalUnitService;

    private AuthorizationManager authorizationManager;

    private RepositoryServiceImpl repositoryService;

    private UserCDIContextHelper userCDIContextHelper;

    private Collection<OrganizationalUnit> authorizedOrganizationalUnits = new HashSet<OrganizationalUnit>();

    public SocialEventOUConstraint() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public SocialEventOUConstraint(final OrganizationalUnitService organizationalUnitService,
                                   final AuthorizationManager authorizationManager,
                                   final RepositoryServiceImpl repositoryService,
                                   final UserCDIContextHelper userCDIContextHelper) {

        this.organizationalUnitService = organizationalUnitService;
        this.authorizationManager = authorizationManager;
        this.repositoryService = repositoryService;
        this.userCDIContextHelper = userCDIContextHelper;
    }

    @Override
    public void init() {
        if (userCDIContextHelper.thereIsALoggedUserInScope()) {
            authorizedOrganizationalUnits = getAuthorizedOrganizationUnits();
        }
    }

    public boolean hasRestrictions(SocialActivitiesEvent event) {
        try {
            if (!userCDIContextHelper.thereIsALoggedUserInScope()) {
                return false;
            }

            if (isOUSocialEvent(event)) {
                return hasRestrictionsForThisOU(event);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean hasRestrictionsForThisOU(SocialActivitiesEvent event) {
        for (OrganizationalUnit authorizedOrganizationalUnit : authorizedOrganizationalUnits) {
            if (authorizedOrganizationalUnit.getName().equals(event.getLinkTarget())) {
                return false;
            }
        }
        return true;
    }

    boolean isOUSocialEvent(SocialActivitiesEvent event) {
        if (event.getLinkType().equals(SocialActivitiesEvent.LINK_TYPE.CUSTOM)) {
            if (isAOUSocialEvent(event)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAOUSocialEvent(SocialActivitiesEvent event) {
        for (OrganizationalUnitEventType organizationalUnitEventType : OrganizationalUnitEventType.values()) {
            if (event.getType().equals(organizationalUnitEventType.name())) {
                return true;
            }
        }
        return false;
    }

    Collection<OrganizationalUnit> getAuthorizedOrganizationUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Collection<OrganizationalUnit> authorizedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        for (OrganizationalUnit ou : organizationalUnits) {
            if (authorizationManager.authorize(ou,
                                               userCDIContextHelper.getUser())) {
                authorizedOrganizationalUnits.add(ou);
            }
        }
        return authorizedOrganizationalUnits;
    }
}
