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
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialSecurityConstraint;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class SocialEventRepositoryConstraint implements SocialSecurityConstraint {

    private OrganizationalUnitService organizationalUnitService;

    private ConfiguredRepositories configuredRepositories;

    private UserCDIContextHelper userCDIContextHelper;

    protected AuthorizationManager authorizationManager;

    protected Set<Repository> authorizedRepos = new HashSet<Repository>();

    public SocialEventRepositoryConstraint() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public SocialEventRepositoryConstraint(final OrganizationalUnitService organizationalUnitService,
                                           final AuthorizationManager authorizationManager,
                                           final ConfiguredRepositories configuredRepositories,
                                           final UserCDIContextHelper userCDIContextHelper) {

        this.organizationalUnitService = organizationalUnitService;
        this.authorizationManager = authorizationManager;
        this.configuredRepositories = configuredRepositories;
        this.userCDIContextHelper = userCDIContextHelper;
    }

    @Override
    public void init() {
        if (userCDIContextHelper.thereIsALoggedUserInScope()) {
            authorizedRepos = getAuthorizedRepositories();
        }
    }

    public boolean hasRestrictions(SocialActivitiesEvent event) {
        try {
            if (!userCDIContextHelper.thereIsALoggedUserInScope()) {
                return false;
            }
            if (event.isVFSLink() || isAProjectEvent(event)) {
                Repository repository = getEventRepository(event);
                final boolean userHasAccessToRepo = authorizedRepos.contains(repository);
                return !userHasAccessToRepo;
            } else {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }

    Repository getEventRepository(SocialActivitiesEvent event) {
        final Path path = Paths.get(event.getLinkTarget());
        final FileSystem fileSystem = path.getFileSystem();
        return configuredRepositories.getRepositoryByRepositoryFileSystem(fileSystem);
    }

    private boolean isAProjectEvent(SocialActivitiesEvent event) {
        return event.getLinkType().equals(SocialActivitiesEvent.LINK_TYPE.CUSTOM)
                && event.getType().equals(ProjectEventType.NEW_PROJECT.name());
    }

    public Set<Repository> getAuthorizedRepositories() {
        final Set<Repository> authorizedRepos = new HashSet<Repository>();
        for (OrganizationalUnit ou : getAuthorizedOrganizationUnits()) {
            final Collection<Repository> repositories = ou.getRepositories();
            for (final Repository repository : repositories) {
                if (authorizationManager.authorize(repository,
                                                   userCDIContextHelper.getUser())) {
                    authorizedRepos.add(repository);
                }
            }
        }
        return authorizedRepos;
    }

    private Collection<OrganizationalUnit> getAuthorizedOrganizationUnits() {
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
