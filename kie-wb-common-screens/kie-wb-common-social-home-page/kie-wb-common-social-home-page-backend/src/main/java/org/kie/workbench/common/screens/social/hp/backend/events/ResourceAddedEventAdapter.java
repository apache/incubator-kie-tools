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
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.model.HomePageTypes;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.workbench.events.ResourceAddedEvent;

@ApplicationScoped
public class ResourceAddedEventAdapter implements SocialAdapter<ResourceAddedEvent> {

    @Inject
    private User loggedUser;

    @Inject
    private SocialUserRepositoryAPI socialUserRepositoryAPI;

    @Inject
    private VersionService versionService;

    @Override
    public Class<ResourceAddedEvent> eventToIntercept() {
        return ResourceAddedEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return HomePageTypes.RESOURCE_ADDED_EVENT;
    }

    @Override
    public boolean shouldInterceptThisEvent( Object event ) {
        if ( event.getClass().getSimpleName().equals( eventToIntercept().getSimpleName() ) ) {
            if ( !isASystemEvent( event ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isASystemEvent( Object _event ) {
        ResourceAddedEvent event = (ResourceAddedEvent) _event;
        final String user = event.getSessionInfo().getIdentity().getIdentifier();
        if ( user.equalsIgnoreCase( "system" ) || user.equalsIgnoreCase( "<system>" ) ) {
            return true;
        }
        return false;
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        ResourceAddedEvent event = (ResourceAddedEvent) object;
        SocialUser socialUser = socialUserRepositoryAPI.findSocialUser( event.getSessionInfo().getIdentity().getIdentifier() );
        String additionalInfo = "added ";
        String description = getCommitDescription( event );
        return new SocialActivitiesEvent( socialUser, HomePageTypes.RESOURCE_ADDED_EVENT.name(), new Date() ).withLink( event.getPath().getFileName(), event.getPath().toURI() ).withAdicionalInfo( additionalInfo ).withDescription( description );
    }

    private String getCommitDescription( ResourceAddedEvent event ) {
        if ( event.getMessage() != null ) {
            return event.getMessage();
        }
        return "";
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
