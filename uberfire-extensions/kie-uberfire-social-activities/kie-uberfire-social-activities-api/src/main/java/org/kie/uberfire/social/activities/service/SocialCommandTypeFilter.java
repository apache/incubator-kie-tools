package org.kie.uberfire.social.activities.service;

import java.util.List;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;

public interface SocialCommandTypeFilter {

    public List<SocialActivitiesEvent> execute( String parameterValue,
                                                List<SocialActivitiesEvent> events );

    public String getCommandName();
}
