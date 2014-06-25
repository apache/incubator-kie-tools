package org.kie.uberfire.social.activities.adapters;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialCommandUserFilter;

@ApplicationScoped
public class SampleUserCommand implements SocialCommandUserFilter {

    @Override
    public List<SocialActivitiesEvent> execute( String parameterValue,
                                                List<SocialActivitiesEvent> events ) {
        List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
        for ( SocialActivitiesEvent event : events ) {
            String name = event.getSocialUser().getName();
            SocialUser socialUser = new SocialUser( name.toUpperCase() );
            SocialActivitiesEvent socialEvent = new SocialActivitiesEvent( socialUser, event.getType(), event.getTimestamp() );
            newList.add( socialEvent );
        }
        return newList;
    }

    @Override
    public String getCommandName() {
        return "sampleUserCommand";
    }
}
