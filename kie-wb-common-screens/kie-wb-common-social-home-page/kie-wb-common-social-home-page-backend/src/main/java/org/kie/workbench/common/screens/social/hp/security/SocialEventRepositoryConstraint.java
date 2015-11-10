/*
 * Copyright 2015 JBoss Inc
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

import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.service.SocialSecurityConstraint;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.security.authz.AuthorizationManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@ApplicationScoped
public class SocialEventRepositoryConstraint implements SocialSecurityConstraint {

    private OrganizationalUnitService organizationalUnitService;

    private AuthorizationManager authorizationManager;

    private RepositoryServiceImpl repositoryService;

    private User identity;

    private Set<Repository> authorizedRepos = new HashSet<Repository>();

    @Inject
    public SocialEventRepositoryConstraint( final OrganizationalUnitService organizationalUnitService,
                                            final AuthorizationManager authorizationManager,
                                            final RepositoryServiceImpl repositoryService,
                                            final User identity ) {

        this.organizationalUnitService = organizationalUnitService;
        this.authorizationManager = authorizationManager;
        this.repositoryService = repositoryService;
        this.identity = identity;
    }

    @Override
    public void init() {
        authorizedRepos = getAuthorizedRepositories();
    }

    public boolean hasRestrictions( SocialActivitiesEvent event ) {
        if ( event.isVFSLink() || isAProjectEvent( event ) ) {
            Repository repository = getEventRepository( event );
            final boolean userHasAccessToRepo = authorizedRepos.contains( repository );
            return !userHasAccessToRepo;
        } else {
            return false;
        }
    }

    Repository getEventRepository( SocialActivitiesEvent event ) {
        final Path path = Paths.get( event.getLinkTarget() );
        final FileSystem fileSystem = path.getFileSystem();
        return repositoryService.getRepository( fileSystem );
    }

    private boolean isAProjectEvent( SocialActivitiesEvent event ) {
        return event.getLinkType().equals( SocialActivitiesEvent.LINK_TYPE.CUSTOM )
                && event.getType().equals( ProjectEventType.NEW_PROJECT.name() );
    }

    Set<Repository> getAuthorizedRepositories() {
        final Set<Repository> authorizedRepos = new HashSet<Repository>();
        for ( OrganizationalUnit ou : getAuthorizedOrganizationUnits() ) {
            final Collection<Repository> repositories = ou.getRepositories();
            for ( final Repository repository : repositories ) {
                if ( authorizationManager.authorize( repository,
                        identity ) ) {
                    authorizedRepos.add( repository );
                }
            }
        }
        return authorizedRepos;
    }

    private Collection<OrganizationalUnit> getAuthorizedOrganizationUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Collection<OrganizationalUnit> authorizedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        for ( OrganizationalUnit ou : organizationalUnits ) {
            if ( authorizationManager.authorize( ou,
                    identity ) ) {
                authorizedOrganizationalUnits.add( ou );
            }
        }
        return authorizedOrganizationalUnits;
    }

}
