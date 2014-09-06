package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialRouterAPI;
import org.kie.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;

@Service
@ApplicationScoped
public class SocialTypeTimelinePagedRepository extends SocialPageRepository implements SocialTypeTimelinePagedRepositoryAPI {

    @Inject
    SocialRouterAPI socialRouterAPI;

    @Override
    public PagedSocialQuery getEventTimeline( String adapterName,
                                              SocialPaged socialPage ) {
        return getEventTimeline( adapterName, socialPage, new HashMap() );
    }

    @Override
    public PagedSocialQuery getEventTimeline( String adapterName,
                                              SocialPaged socialPaged,
                                              Map commandsMap ) {
        SocialAdapter socialAdapter = getSocialAdapter( adapterName );

        return getEventTimeline( socialAdapter, socialPaged, commandsMap );
    }

    SocialAdapter getSocialAdapter( String adapterName ) {
        return socialRouterAPI.getSocialAdapter( adapterName );
    }

    @Override
    public PagedSocialQuery getEventTimeline( SocialAdapter adapter,
                                              SocialPaged socialPaged ) {
        return getEventTimeline( adapter, socialPaged, new HashMap() );
    }

    @Override
    public PagedSocialQuery getEventTimeline( SocialAdapter adapter,
                                              SocialPaged socialPaged,
                                              Map commandsMap ) {

        socialPaged = setupQueryDirection( socialPaged );

        List<SocialActivitiesEvent> typeEvents = new ArrayList<SocialActivitiesEvent>();

        if ( socialPaged.isANewQuery() ) {
            socialPaged = searchForRecentEvents( adapter.socialEventType(), socialPaged, typeEvents );
        }
        if ( !foundEnoughtEvents( socialPaged, typeEvents ) ) {
            socialPaged = searchForStoredEvents( adapter.socialEventType(), socialPaged, typeEvents );
        }

        typeEvents = filterTimelineWithAdapters( commandsMap, typeEvents );

        checkIfICanGoForward( socialPaged, typeEvents );

        PagedSocialQuery query = new PagedSocialQuery( typeEvents, socialPaged );

        return query;
    }

    private SocialPaged searchForStoredEvents( SocialEventType type,
                                               SocialPaged socialPaged,
                                               List<SocialActivitiesEvent> events ) {
        if ( socialPaged.firstFileRead() ) {
            readMostRecentFile( type, socialPaged, events );
        } else {
            readCurrentFile( type, socialPaged, events );
        }
        if ( !foundEnoughtEvents( socialPaged, events ) && shouldIReadMoreFiles( socialPaged ) ) {
            readMoreFiles( socialPaged, type, events );
        }
        return socialPaged;
    }

    private boolean shouldIReadMoreFiles( SocialPaged socialPaged ) {
        return socialPaged.lastFileReaded() != null && !socialPaged.lastFileReaded().isEmpty() && thereIsMoreFilesToRead( socialPaged.lastFileReaded() );
    }

    private void readMoreFiles( SocialPaged socialPaged,
                                SocialEventType type,
                                List<SocialActivitiesEvent> events ) {
        String nextFileToRead = socialPaged.getNextFileToRead();
        if ( thereIsMoreFilesToRead( nextFileToRead ) ) {
            addEventsToTimeline( type, socialPaged, events, nextFileToRead );
            if ( !foundEnoughtEvents( socialPaged, events ) ) {
                readMoreFiles( socialPaged, type, events );
            }
        }
        checkIfICanGoForward( socialPaged, events );
    }

    private void readCurrentFile( SocialEventType type,
                                  SocialPaged socialPaged,
                                  List<SocialActivitiesEvent> events ) {
        String lastFileReaded = socialPaged.lastFileReaded();
        addEventsToTimeline( type, socialPaged, events, lastFileReaded );
    }

    private void addEventsToTimeline( SocialEventType type,
                                      SocialPaged socialPaged,
                                      List<SocialActivitiesEvent> events,
                                      String lastFileReaded ) {
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline( type, lastFileReaded );
        setNumberOfEventsOnFile( socialPaged, type, lastFileReaded );
        addEvents( socialPaged, events, timeline );
    }

    private void setNumberOfEventsOnFile( SocialPaged socialPaged,
                                          SocialEventType type,
                                          String lastFileReaded ) {
        socialPaged.setNumberOfEventsOnFile( getSocialTimelinePersistenceAPI().getNumberOfEventsOnFile( type, lastFileReaded ) );
    }

    private void readMostRecentFile( SocialEventType type,
                                     SocialPaged socialPaged,
                                     List<SocialActivitiesEvent> events ) {
        Integer userMostRecentFileIndex = getSocialTimelinePersistenceAPI().getTypeMostRecentFileIndex( type );
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline( type, userMostRecentFileIndex.toString() );
        socialPaged.setLastFileReaded( userMostRecentFileIndex.toString() );
        readEvents( socialPaged, events, timeline );
    }

    private SocialPaged searchForRecentEvents( SocialEventType type,
                                               SocialPaged socialPaged,
                                               List<SocialActivitiesEvent> events ) {
        List<SocialActivitiesEvent> freshEvents = getSocialTimelinePersistenceAPI().getRecentEvents( type );
        Collections.reverse( freshEvents );
        searchEvents( socialPaged, events, freshEvents );
        return socialPaged;
    }

}
