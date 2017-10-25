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

package org.guvnor.asset.management.backend.social;

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
import org.guvnor.asset.management.backend.social.i18n.Constants;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.structure.repositories.NewBranchEvent;

@ApplicationScoped
public class NewBranchEventAdapter
        implements SocialAdapter<NewBranchEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Inject
    private Constants constants;

    @Override
    public Class<NewBranchEvent> eventToIntercept() {
        return NewBranchEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return AssetManagementEventTypes.BRANCH_CREATED;
    }

    @Override
    public boolean shouldInterceptThisEvent(Object event) {
        if (event.getClass().getSimpleName().equals(eventToIntercept().getSimpleName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SocialActivitiesEvent toSocial(Object object) {
        NewBranchEvent event = (NewBranchEvent) object;

        return new SocialActivitiesEvent(
                socialUserRepository.systemUser(),
                AssetManagementEventTypes.BRANCH_CREATED.name(),
                new Date(event.getTimestamp())
        )
                .withLink(event.getRepositoryAlias(),
                          event.getBranchPath().toURI())
                .withAdicionalInfo(getAdditionalInfo(event.getBranchName(),
                                                     event.getRepositoryAlias()));
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo(String branch,
                                     String repository) {
        return constants.configure_repository_branch_created(branch,
                                                             repository);
    }
}
