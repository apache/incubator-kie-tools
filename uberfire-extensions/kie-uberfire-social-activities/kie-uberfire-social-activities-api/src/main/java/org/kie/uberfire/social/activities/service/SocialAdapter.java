package org.kie.uberfire.social.activities.service;


import java.util.List;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;

public interface SocialAdapter<T> {

    Class<T> eventToIntercept();

    SocialEventType socialEventType();

    boolean shouldInterceptThisEvent( Object event );

    SocialActivitiesEvent toSocial( Object object );

    List<SocialCommandTypeFilter> getTimelineFilters();

    List<String> getTimelineFiltersNames();
}
