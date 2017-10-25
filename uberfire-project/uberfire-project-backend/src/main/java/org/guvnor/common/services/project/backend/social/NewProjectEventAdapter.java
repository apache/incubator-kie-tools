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

package org.guvnor.common.services.project.backend.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.repository.SocialUserRepository;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class NewProjectEventAdapter implements SocialAdapter<NewProjectEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NewProjectEventAdapter.class);

    @Inject
    private SocialUserRepository socialUserRepository;

    @Inject
    private RepositoryService repositoryService;

    @Override
    public Class<NewProjectEvent> eventToIntercept() {
        return NewProjectEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return ProjectEventType.NEW_PROJECT;
    }

    @Override
    public boolean shouldInterceptThisEvent(Object event) {
        return event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName());
    }

    @Override
    public SocialActivitiesEvent toSocial(Object object) {

        SocialActivitiesEvent socialActivitiesEvent;
        NewProjectEvent event = (NewProjectEvent) object;

        Path repositoryRootPath = event.getProject().getRootPath();
        Repository repository = null;
        String repositoryAlias = null;

        try {
            repositoryRootPath = Paths.convert(Paths.convert(repositoryRootPath).getRoot());
            repository = repositoryService.getRepository(repositoryRootPath);
            repositoryAlias = repository.getAlias();
        } catch (Exception e) {
            logger.error("It was not possible to establish the repository for project root path: " + event.getProject().getRootPath(),
                         e);
            logger.error("Social event won't be fired for this project.");
        }

        socialActivitiesEvent = new SocialActivitiesEvent(
                socialUserRepository.findSocialUser(event.getIdentity()),
                socialEventType().name(),
                new Date()
        )
                .withDescription(event.getProject().getProjectName());

        if (repositoryAlias != null) {
            socialActivitiesEvent.withLink(event.getProject().getProjectName(),
                                           event.getProject().getRootPath().toURI(),
                                           SocialActivitiesEvent.LINK_TYPE.CUSTOM)
                    .withParam("repositoryAlias",
                               repository.getAlias())
                    .withParam("currentBranch",
                               "get the branch form the link");
        }
        socialActivitiesEvent.withAdicionalInfo(getAdditionalInfo(event));

        return socialActivitiesEvent;
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo(NewProjectEvent event) {
        return "added";
    }
}