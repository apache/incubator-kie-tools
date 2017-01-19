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

package org.ext.uberfire.social.activities.adapters;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialCommandTypeFilter;
import org.ext.uberfire.social.activities.service.SocialCommandUserFilter;

@ApplicationScoped
public class CommandTimelineFilter {

    @Inject
    @Any
    private Instance<SocialCommandUserFilter> userFilters;

    public List<SocialActivitiesEvent> executeTypeCommandsOn( SocialAdapter type,
                                                              Map commandsMap,
                                                              List<SocialActivitiesEvent> socialActivitiesEvents ) {
        for ( Object mapvalue : commandsMap.keySet() ) {
            socialActivitiesEvents = executeTypeAdapters( type, commandsMap, socialActivitiesEvents, mapvalue );
        }
        return socialActivitiesEvents;
    }

    public List<SocialActivitiesEvent> executeUserCommandsOn( List<SocialActivitiesEvent> userEvents,

                                                              Map commandsMap ) {
        if ( thereIsUserFilters() ) {
            for ( Object mapValue : commandsMap.keySet() ) {
                userEvents = executeUserAdapters( commandsMap, userEvents, mapValue );
            }
        }
        return userEvents;
    }

    private List<SocialActivitiesEvent> executeUserAdapters( Map commandsMap,
                                                             List<SocialActivitiesEvent> userEvents,
                                                             Object s ) {
        for ( SocialCommandUserFilter socialCommandUserFilter : userFilters ) {
            String key = (String) s;
            if ( theParameterIsThisUserAdapter( key, socialCommandUserFilter ) ) {
                String[] values = (String[]) commandsMap.get( s );
                if ( values.length > 0 ) {
                    String value = values[ 0 ];
                    userEvents = socialCommandUserFilter.execute( value, userEvents );
                }
            }
        }
        return userEvents;
    }

    private boolean thereIsUserFilters() {
        return userFilters != null && userFilters.iterator().hasNext();
    }

    private List<SocialActivitiesEvent> executeTypeAdapters( SocialAdapter type,
                                                             Map commandsMap,
                                                             List<SocialActivitiesEvent> socialActivitiesEvents,
                                                             Object s ) {
        if ( type.getTimelineFilters() != null ) {
            for ( SocialCommandTypeFilter socialCommandTypeFilter : (List<SocialCommandTypeFilter>) type.getTimelineFilters() ) {
                String key = (String) s;
                if ( theParameterIsThisAdapter( key, socialCommandTypeFilter ) ) {
                    String[] values = (String[]) commandsMap.get( s );
                    if ( values.length > 0 ) {
                        String value = values[ 0 ];
                        socialActivitiesEvents = socialCommandTypeFilter.execute( value, socialActivitiesEvents );
                    }
                }
            }
        }
        return socialActivitiesEvents;
    }

    private static boolean theParameterIsThisAdapter( String key,
                                                      SocialCommandTypeFilter socialCommandTypeFilter ) {
        return key.equalsIgnoreCase( socialCommandTypeFilter.getCommandName() );
    }

    private static boolean theParameterIsThisUserAdapter( String key,
                                                          SocialCommandUserFilter socialCommandUserFilter ) {
        return key.equalsIgnoreCase( socialCommandUserFilter.getCommandName() );
    }

}
