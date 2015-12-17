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

package org.kie.workbench.common.screens.social.hp.backend.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;

@ApplicationScoped
public class NewRepositoryEventAdapter implements SocialAdapter<NewRepositoryEvent> {

    @Inject
    private User loggedUser;

    @Inject
    private SocialUserRepositoryAPI socialUserRepositoryAPI;

    @Override
    public Class<NewRepositoryEvent> eventToIntercept() {
        return NewRepositoryEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return ExtendedTypes.NEW_REPOSITORY_EVENT;
    }

    @Override
    public boolean shouldInterceptThisEvent( Object event ) {
        if ( event.getClass().getSimpleName().equals( eventToIntercept().getSimpleName() ) ) {
            return true;
        }
        return false;
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        NewRepositoryEvent event = (NewRepositoryEvent) object;
        SocialUser socialUser = null;
        try {
            socialUser = socialUserRepositoryAPI.findSocialUser( loggedUser.getIdentifier() );
        } catch(ContextNotActiveException e) {
            //clean repository
            socialUser = new SocialUser( "system" );
        }
        String additionalInfo = "created ";
        return new SocialActivitiesEvent( socialUser, ExtendedTypes.NEW_REPOSITORY_EVENT, new Date() ).withAdicionalInfo( additionalInfo ).withLink( event.getNewRepository().getAlias(), event.getNewRepository().getUri() ).withDescription( "" );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        ArrayList<SocialCommandTypeFilter> socialCommandTypeFilters = new ArrayList<SocialCommandTypeFilter>();
        return socialCommandTypeFilters;
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        List<String> names = new ArrayList<String>();
        return names;
    }
}
