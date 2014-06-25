package org.kie.uberfire.social.activities.service;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface SocialUserServiceAPI {

    void userFollowAnotherUser( String followerUsername,
                                String followUsername );

    void userUnfollowAnotherUser( String followerUsername,
                                  String followUsername );

}
