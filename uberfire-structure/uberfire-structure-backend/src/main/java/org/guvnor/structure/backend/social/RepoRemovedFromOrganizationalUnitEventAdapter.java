/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.social;

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
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.social.OrganizationalUnitEventType;

@ApplicationScoped
public class RepoRemovedFromOrganizationalUnitEventAdapter
        implements SocialAdapter<RepoRemovedFromOrganizationalUnitEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Override
    public Class<RepoRemovedFromOrganizationalUnitEvent> eventToIntercept() {
        return RepoRemovedFromOrganizationalUnitEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return OrganizationalUnitEventType.REPO_REMOVED_FROM_ORGANIZATIONAL_UNIT;
    }

    @Override
    public boolean shouldInterceptThisEvent(Object event) {
        return event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName());
    }

    @Override
    public SocialActivitiesEvent toSocial(Object object) {
        RepoRemovedFromOrganizationalUnitEvent event = (RepoRemovedFromOrganizationalUnitEvent) object;

        return new SocialActivitiesEvent(
                socialUserRepository.findSocialUser(event.getUserName()),
                socialEventType().name(),
                new Date()
        )
                .withDescription(event.getOrganizationalUnit().getName())
                .withLink(event.getOrganizationalUnit().getName(),
                          event.getOrganizationalUnit().getName(),
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM)
                .withAdicionalInfo(getAdditionalInfo(event))
                .withParam("ouName",
                           event.getOrganizationalUnit().getName())
                .withParam("repositoryName",
                           event.getRepository().getAlias())
                .withParam("repositoryURI",
                           event.getRepository().getUri());
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo(RepoRemovedFromOrganizationalUnitEvent event) {
        return "updated";
    }
}
