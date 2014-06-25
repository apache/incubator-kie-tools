package org.kie.uberfire.social.activities.servlet;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;

public class AtomSocialTimelineConverter {

    public static String generate( List<SocialActivitiesEvent> eventTimeline, String type ) {
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        feed.setId( "tag:org.uberfire,2014:/"+ type );
        feed.setTitle( "Social Activities Feed" );
        feed.setUpdated( new Date() );
        feed.addAuthor( "Red Hat JBoss" );

        for ( SocialActivitiesEvent event : eventTimeline ) {
            Entry entry = feed.addEntry();
            entry.setTitle( event.getType() );
            entry.setSummary( event.getSocialUser().getName() + "  " + event.toString() );
            entry.setUpdated( event.getTimestamp() );
            entry.setPublished( event.getTimestamp() );
        }
        return feed.toString();
    }
}
