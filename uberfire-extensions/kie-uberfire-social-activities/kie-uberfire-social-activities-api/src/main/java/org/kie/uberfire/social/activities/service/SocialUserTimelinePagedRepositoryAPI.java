package org.kie.uberfire.social.activities.service;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.model.SocialPaged;

@Remote
public interface SocialUserTimelinePagedRepositoryAPI {

    PagedSocialQuery getUserTimeline( SocialUser socialUser,
                                      SocialPaged socialPaged );

    PagedSocialQuery getUserTimeline( SocialUser socialUser,
                                      SocialPaged socialPaged,
                                      Map commandsMap );
}
