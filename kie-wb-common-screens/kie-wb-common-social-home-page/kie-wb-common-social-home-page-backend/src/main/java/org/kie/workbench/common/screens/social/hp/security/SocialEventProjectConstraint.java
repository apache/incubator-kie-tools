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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialSecurityConstraint;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.security.authz.AuthorizationManager;

/**
 * A Social Events Constraint to restrict access to Social Events relating to Projects. If a User
 * is not authorized to access a Project to which a Social Event relates the Social Event is filtered.
 * This implementation delegates filtering by Organizational Unit and Repository to SocialEventRepositoryConstraint.
 * This is a performance gain to avoid building collections for authorized Organizational Unit and Repository first
 * before we filter by authorized Projects.
 * @see SocialEventRepositoryConstraint
 */
@ApplicationScoped
public class SocialEventProjectConstraint implements SocialSecurityConstraint {

    private UserCDIContextHelper userCDIContextHelper;
    private SocialEventRepositoryConstraint delegate;
    private AuthorizationManager authorizationManager;
    private KieProjectService projectService;

    public SocialEventProjectConstraint() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public SocialEventProjectConstraint(final SocialEventRepositoryConstraint delegate,
                                        final AuthorizationManager authorizationManager,
                                        final KieProjectService projectService,
                                        final UserCDIContextHelper userCDIContextHelper) {
        this.delegate = PortablePreconditions.checkNotNull("delegate",
                                                           delegate);
        this.authorizationManager = PortablePreconditions.checkNotNull("authorizationManager",
                                                                       authorizationManager);
        this.projectService = PortablePreconditions.checkNotNull("projectService",
                                                                 projectService);
        this.userCDIContextHelper = PortablePreconditions.checkNotNull("userCDIContextHelper",
                                                                       userCDIContextHelper);
    }

    @Override
    public void init() {
        if (userCDIContextHelper.thereIsALoggedUserInScope()) {
            delegate.init();
        }
    }

    @Override
    public boolean hasRestrictions(final SocialActivitiesEvent event) {
        try {
            if (!userCDIContextHelper.thereIsALoggedUserInScope()) {
                return false;
            }

            if (event.isVFSLink() || isAProjectEvent(event)) {
                final boolean isRepositoryRestricted = delegate.hasRestrictions(event);
                if (isRepositoryRestricted) {
                    return true;
                }
                final Project project = getEventProject(event);
                if (thereIsAProjectAssociatedWithThisEvent(project)) {
                    return !authorizationManager.authorize(project,
                                                           userCDIContextHelper.getUser());
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }

    private boolean thereIsAProjectAssociatedWithThisEvent(Project project) {
        return project != null;
    }

    private boolean isAProjectEvent(final SocialActivitiesEvent event) {
        return event.getLinkType().equals(SocialActivitiesEvent.LINK_TYPE.CUSTOM)
                && event.getType().equals(ProjectEventType.NEW_PROJECT.name());
    }

    Project getEventProject(final SocialActivitiesEvent event) {
        final Path path = Paths.get(event.getLinkTarget());
        final org.uberfire.backend.vfs.Path vfsPath = org.uberfire.backend.server.util.Paths.convert(path);
        return projectService.resolveProject(vfsPath);
    }
}
