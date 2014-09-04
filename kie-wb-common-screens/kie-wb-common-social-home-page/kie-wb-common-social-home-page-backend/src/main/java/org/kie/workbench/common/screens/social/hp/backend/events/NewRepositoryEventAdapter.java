package org.kie.workbench.common.screens.social.hp.backend.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.uberfire.security.Identity;

@ApplicationScoped
public class NewRepositoryEventAdapter implements SocialAdapter<NewRepositoryEvent> {

    @Inject
    private Identity loggedUser;

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
            socialUser = socialUserRepositoryAPI.findSocialUser( loggedUser.getName() );
        } catch(ContextNotActiveException e) {
            //clean repository
            socialUser = new SocialUser( "system" );
        }
        String additionalInfo = "created ";
        return new SocialActivitiesEvent( socialUser, ExtendedTypes.NEW_REPOSITORY_EVENT, new Date() ).withAdicionalInfo( additionalInfo ).withLink( event.getNewRepository().getAlias(), event.getNewRepository().getUri() );
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
