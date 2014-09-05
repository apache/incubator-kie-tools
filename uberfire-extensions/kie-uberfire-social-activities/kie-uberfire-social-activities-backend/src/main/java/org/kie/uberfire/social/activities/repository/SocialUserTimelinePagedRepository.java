package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserTimelinePagedRepositoryAPI;

@Service
@ApplicationScoped
public class SocialUserTimelinePagedRepository extends SocialPageRepository implements SocialUserTimelinePagedRepositoryAPI {

    @Override
    public PagedSocialQuery getUserTimeline( SocialUser socialUser,
                                             SocialPaged socialPaged ) {
        return getUserTimeline( socialUser, socialPaged, new HashMap() );
    }

    @Override
    public PagedSocialQuery getUserTimeline( SocialUser socialUser,
                                             SocialPaged socialPaged,
                                             Map commandsMap ) {

        List<SocialActivitiesEvent> userEvents = new ArrayList<SocialActivitiesEvent>();

        socialPaged = setupQueryDirection( socialPaged );

        if ( socialPaged.isANewQuery() ) {
            socialPaged = searchForRecentEvents( socialUser, socialPaged, userEvents );
        }
        if ( !foundEnoughtEvents( socialPaged, userEvents ) ) {
            socialPaged = searchForStoredEvents( socialUser, socialPaged, userEvents );
        }

        userEvents = filterTimelineWithAdapters( commandsMap, userEvents );

        checkIfICanGoForward( socialPaged, userEvents );

        PagedSocialQuery query = new PagedSocialQuery( userEvents, socialPaged );

        return query;
    }

    private SocialPaged searchForStoredEvents( SocialUser socialUser,
                                               SocialPaged socialPaged,
                                               List<SocialActivitiesEvent> events ) {
        if ( socialPaged.firstFileRead() ) {
            readMostRecentFile( socialUser, socialPaged, events );
        } else {
            readCurrentFile( socialUser, socialPaged, events );

        }
        if ( !foundEnoughtEvents( socialPaged, events ) && readSomething( socialPaged ) ) {
            readMoreFiles( socialPaged, socialUser, events );
        }
        return socialPaged;
    }

    private boolean readSomething( SocialPaged socialPaged ) {
        return socialPaged.lastFileReaded() != null && !socialPaged.lastFileReaded().isEmpty();
    }

    private void readMoreFiles( SocialPaged socialPaged,
                                SocialUser socialUser,
                                List<SocialActivitiesEvent> events ) {
        String nextFileToRead = socialPaged.getNextFileToRead();
        if ( thereIsMoreFilesToRead( nextFileToRead ) ) {
            addEventsToTimeline( socialUser, socialPaged, events, nextFileToRead );
            if ( !foundEnoughtEvents( socialPaged, events ) ) {
                readMoreFiles( socialPaged, socialUser, events );
            }
        }
    }

    private void readCurrentFile( SocialUser socialUser,
                                  SocialPaged socialPaged,
                                  List<SocialActivitiesEvent> events ) {
        String lastFileReaded = socialPaged.lastFileReaded();
        addEventsToTimeline( socialUser, socialPaged, events, lastFileReaded );
    }

    private void addEventsToTimeline( SocialUser socialUser,
                                      SocialPaged socialPaged,
                                      List<SocialActivitiesEvent> events,
                                      String lastFileReaded ) {
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline( socialUser, lastFileReaded );
        setNumberOfEventsOnFile( socialPaged, socialUser, lastFileReaded );
        addEvents( socialPaged, events, timeline );
    }

    private void setNumberOfEventsOnFile( SocialPaged socialPaged,
                                          SocialUser socialUser,
                                          String lastFileReaded ) {
        socialPaged.setNumberOfEventsOnFile( getSocialTimelinePersistenceAPI().getNumberOfEventsOnFile( socialUser, lastFileReaded ) );
    }

    private void readMostRecentFile( SocialUser socialUser,
                                     SocialPaged socialPaged,
                                     List<SocialActivitiesEvent> events ) {
        Integer userMostRecentFileIndex = getSocialTimelinePersistenceAPI().getUserMostRecentFileIndex( socialUser );
        if ( thereIsNothingToRead( userMostRecentFileIndex ) ) {
            return;
        }
        List<SocialActivitiesEvent> timeline = getSocialTimelinePersistenceAPI().getTimeline( socialUser, userMostRecentFileIndex.toString() );
        socialPaged.setLastFileReaded( userMostRecentFileIndex.toString() );
        readEvents( socialPaged, events, timeline );
    }

    private boolean thereIsNothingToRead( Integer userMostRecentFileIndex ) {
        return userMostRecentFileIndex < 0;
    }

    private SocialPaged searchForRecentEvents( SocialUser socialUser,
                                               SocialPaged socialPaged,
                                               List<SocialActivitiesEvent> events ) {
        List<SocialActivitiesEvent> freshEvents = getSocialTimelinePersistenceAPI().getRecentEvents( socialUser );
        Collections.reverse( freshEvents );
        searchEvents( socialPaged, events, freshEvents );
        return socialPaged;
    }

}
