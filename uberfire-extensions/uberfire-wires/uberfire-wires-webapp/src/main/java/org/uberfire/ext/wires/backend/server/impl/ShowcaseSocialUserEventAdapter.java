/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.backend.server.impl;

import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.uberfire.ext.wires.shared.social.ShowcaseSocialUserEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class ShowcaseSocialUserEventAdapter implements SocialAdapter<ShowcaseSocialUserEvent> {

    private AtomicLong counter = new AtomicLong();

    @Inject
    private SocialUserRepositoryAPI socialUserRepositoryAPI;

    @Override
    public Class<ShowcaseSocialUserEvent> eventToIntercept() {
        return ShowcaseSocialUserEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return DefaultTypes.DUMMY_EVENT;
    }

    @Override
    public boolean shouldInterceptThisEvent( Object event ) {
        return event.getClass().getCanonicalName().equals( eventToIntercept().getCanonicalName() ) ? true : false;
    }

    public void onEvent( @Observes ShowcaseSocialUserEvent event ) {
        //Include @Observes only so events are propagated to server side.
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        final ShowcaseSocialUserEvent event = (ShowcaseSocialUserEvent) object;
        SocialUser socialUser = null;
        try {
            socialUser = socialUserRepositoryAPI.findSocialUser( event.getUsername() );
        } catch ( ContextNotActiveException e ) {
            //clean repository
            socialUser = new SocialUser( "system" );
        }
        final String desc = String.format( "new social event (%d)", counter.incrementAndGet() );
        return new SocialActivitiesEvent( socialUser, DefaultTypes.DUMMY_EVENT, new Date() )
                .withAdicionalInfo( "edited" )
                .withDescription( desc )
                .withLink( String.format( "Main$%d.java", counter.get() ), "file", SocialActivitiesEvent.LINK_TYPE.CUSTOM )
                .withParam( "scheme", "http" );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }
}