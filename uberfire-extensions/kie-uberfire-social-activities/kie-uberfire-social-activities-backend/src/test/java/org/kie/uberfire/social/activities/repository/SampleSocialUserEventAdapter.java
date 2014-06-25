package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.social.activities.model.DefaultTypes;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.FollowSocialUserEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

@ApplicationScoped
public class SampleSocialUserEventAdapter implements SocialAdapter<FollowSocialUserEvent> {


    @Override
    public Class<FollowSocialUserEvent> eventToIntercept() {
        return FollowSocialUserEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return DefaultTypes.DUMMY_EVENT;
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
        FollowSocialUserEvent event = (FollowSocialUserEvent) object;
        String adicionalInfo = "follow " + event.getFollow().getName();
        return new SocialActivitiesEvent( event.getFollower(),  ExtendedTypes.FOLLOW_USER, new Date(), adicionalInfo );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        ArrayList<SocialCommandTypeFilter> socialCommandTypeFilters = new ArrayList<SocialCommandTypeFilter>();
        socialCommandTypeFilters.add( new SocialCommandTypeFilter() {
            @Override
            public List<SocialActivitiesEvent> execute( String parameter,
                                                        List<SocialActivitiesEvent> events ) {
                List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
                Integer maxResults = new Integer( parameter );
                for ( int i = 0; i < maxResults; i++ ) {
                    newList.add( events.get( i ) );
                }
                return newList;
            }

            @Override
            public String getCommandName() {
                return "max-results";
            }
        } );

        socialCommandTypeFilters.add( new SocialCommandTypeFilter() {
            @Override
            public List<SocialActivitiesEvent> execute( String parameter,
                                                        List<SocialActivitiesEvent> events ) {
                List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
                for ( SocialActivitiesEvent event : events ) {
                    String name = event.getSocialUser().getName();
                    SocialUser socialUser = new SocialUser( name + " - filtered" );
                    SocialActivitiesEvent socialEvent = new SocialActivitiesEvent( socialUser, event.getType(), new Date() );
                    newList.add( socialEvent );
                }
                return newList;
            }

            @Override
            public String getCommandName() {
                return "another-filter";
            }
        } );
        return socialCommandTypeFilters;
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        List<String> names = new ArrayList<String>();
        names.add( "max-results" );
        names.add( "another-filter" );
        return names;
    }
}
