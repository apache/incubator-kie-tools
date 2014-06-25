package org.kie.uberfire.social.activities.service;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialPaged;

@Remote
public interface SocialTypeTimelinePagedRepositoryAPI {

    PagedSocialQuery getEventTimeline( String adapterName,
                                       SocialPaged socialPage );

    PagedSocialQuery getEventTimeline( String adapterName,
                                       SocialPaged socialPaged,
                                       Map commandsMap );

    PagedSocialQuery getEventTimeline( SocialAdapter type,
                                       SocialPaged socialPaged );

    PagedSocialQuery getEventTimeline( SocialAdapter type,
                                       SocialPaged socialPaged,
                                       Map commandsMap );
}
