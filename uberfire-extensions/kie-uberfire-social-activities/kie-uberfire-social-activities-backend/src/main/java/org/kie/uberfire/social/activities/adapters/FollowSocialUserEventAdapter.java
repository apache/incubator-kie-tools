package org.kie.uberfire.social.activities.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.FollowSocialUserEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

//@ApplicationScoped -- This is only a Sample Adapter
public class FollowSocialUserEventAdapter implements SocialAdapter<FollowSocialUserEvent> {


    @Override
    public Class<FollowSocialUserEvent> eventToIntercept() {
        return FollowSocialUserEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return ExtendedTypes.FOLLOW_USER;
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
        String adicionalInfo = "follow " + event.getFollow().getUserName();
        return new SocialActivitiesEvent( event.getFollower(),  ExtendedTypes.FOLLOW_USER, new Date() ).withAdicionalInfo( adicionalInfo );
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
                    String name = event.getSocialUser().getUserName();
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
