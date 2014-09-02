package org.kie.uberfire.social.activities.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;

@Remote
public interface SocialTimelineRulesQueryAPI {

    List<SocialActivitiesEvent> executeAllRules();

    List<SocialActivitiesEvent> executeSpecificRule( Map<String, String> globals,
                                                     String drlName,
                                                     String maxResults );

    public List<SocialActivitiesEvent> getAllCached();

    List<SocialActivitiesEvent> getTypeCached( String... typeNames );

    List<SocialActivitiesEvent> getNEventsFromEachType( int numberOfEvents,
                                                        String... typeNames );

}
