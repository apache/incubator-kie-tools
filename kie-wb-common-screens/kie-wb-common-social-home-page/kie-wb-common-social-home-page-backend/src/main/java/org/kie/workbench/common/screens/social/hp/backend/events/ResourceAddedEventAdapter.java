package org.kie.workbench.common.screens.social.hp.backend.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.version.VersionService;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.model.HomePageTypes;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceAddedEvent;

@ApplicationScoped
public class ResourceAddedEventAdapter implements SocialAdapter<ResourceAddedEvent> {

    @Inject
    private Identity loggedUser;

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
            return true;
        }
        return false;
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        ResourceAddedEvent event = (ResourceAddedEvent) object;
        SocialUser socialUser = socialUserRepositoryAPI.findSocialUser( event.getSessionInfo().getIdentity().getName() );
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
