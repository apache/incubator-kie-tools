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

package org.ext.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.adapters.CommandTimelineFilter;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.service.SocialPredicate;
import org.ext.uberfire.social.activities.service.SocialTimelinePersistenceAPI;

public abstract class SocialPageRepository {

    @Inject
    SocialTimelinePersistenceAPI socialTimelinePersistenceAPI;
    @Inject
    CommandTimelineFilter commandTimelineFilter;

    protected void checkIfICanGoForward( SocialPaged socialPaged,
                                         List<SocialActivitiesEvent> events ) {
        controlForward( socialPaged, events );
    }

    private void controlForward( SocialPaged socialPaged,
                                 List<SocialActivitiesEvent> events ) {
        if ( noMoreEvents( socialPaged, events ) ) {
            socialPaged.setCanIGoForward( false );
        } else {
            socialPaged.setCanIGoForward( true );
        }
    }

    private boolean noMoreEvents( SocialPaged socialPaged,
                                  List<SocialActivitiesEvent> events ) {
        return socialPaged.isLastEventFromLastFile() || events.size() < socialPaged.getPageSize();
    }

    protected boolean thereIsMoreFilesToRead( String fileName ) {
        return fileName.equalsIgnoreCase( "" ) || !fileName.equals( "-1" );
    }

    void addEvents( SocialPaged socialPaged,
                    List<SocialActivitiesEvent> events,
                    List<SocialActivitiesEvent> timeline ) {
        for ( int i = socialPaged.lastFileIndex(); i < timeline.size(); i++ ) {
            events.add( timeline.get( i ) );
            socialPaged.updateLastFileIndex();
            if ( foundEnoughtEvents( socialPaged, events ) ) {
                break;
            }
        }
    }

    protected boolean foundEnoughtEvents( SocialPaged socialPaged,
                                          List<SocialActivitiesEvent> events ) {
        return ( events.size() >= socialPaged.getPageSize() );
    }

    void readEvents( SocialPaged socialPaged,
                     List<SocialActivitiesEvent> events,
                     List<SocialActivitiesEvent> timeline ) {
        for ( int i = 0; i < timeline.size(); i++ ) {
            events.add( timeline.get( i ) );
            socialPaged.updateLastFileIndex();
            if ( foundEnoughtEvents( socialPaged, events ) ) {
                break;
            }
        }
    }

    void searchEvents( SocialPaged socialPaged,
                       List<SocialActivitiesEvent> events,
                       List<SocialActivitiesEvent> freshEvents ) {
        for ( int i = socialPaged.freshIndex(); i < freshEvents.size(); i++ ) {
            events.add( freshEvents.get( i ) );
            socialPaged.updateFreshIndex();
            if ( foundEnoughtEvents( socialPaged, events ) ) {
                break;
            }
        }
    }

    SocialTimelinePersistenceAPI getSocialTimelinePersistenceAPI() {
        return socialTimelinePersistenceAPI;
    }

    boolean shouldExecuteAdapters( Map commandsMap,
                                   List<SocialActivitiesEvent> events ) {
        return !events.isEmpty() && commandsMap.size() > 0;
    }

    void sortListByDate( List<SocialActivitiesEvent> socialActivitiesEvents ) {
        Collections.sort( socialActivitiesEvents, new Comparator<SocialActivitiesEvent>() {
            @Override
            public int compare( SocialActivitiesEvent o1,
                                SocialActivitiesEvent o2 ) {
                return o1.getTimestamp().compareTo( o2.getTimestamp() );
            }
        } );
    }

    List<SocialActivitiesEvent> filterTimelineWithAdapters( Map commandsMap,
                                                            List<SocialActivitiesEvent> userEvents ) {
        if ( userEvents == null ) {
            userEvents = new ArrayList<SocialActivitiesEvent>();
        }
        if ( shouldExecuteAdapters( commandsMap, userEvents ) ) {
            userEvents = commandTimelineFilter.executeUserCommandsOn( userEvents, commandsMap );
        }
        return userEvents;
    }

    SocialPaged setupQueryDirection( SocialPaged socialPaged ) {
        if ( socialPaged.isBackward() ) {
            socialPaged = socialPaged.goBackToLastQuery();
        }
        socialPaged.setLastQuery( new SocialPaged( socialPaged ) );
        return socialPaged;
    }

    List<SocialActivitiesEvent> filterList( SocialPredicate<SocialActivitiesEvent> predicate,
                                                    List<SocialActivitiesEvent> freshEvents ) {
        if ( predicate == null ) {
            return freshEvents;
        }
        List<SocialActivitiesEvent> filteredList = new ArrayList<SocialActivitiesEvent>();
        for ( SocialActivitiesEvent socialActivitiesEvent : freshEvents ) {
            if ( predicate.test( socialActivitiesEvent ) ) {
                filteredList.add( socialActivitiesEvent );
            }
        }
        return filteredList;
    }
}
