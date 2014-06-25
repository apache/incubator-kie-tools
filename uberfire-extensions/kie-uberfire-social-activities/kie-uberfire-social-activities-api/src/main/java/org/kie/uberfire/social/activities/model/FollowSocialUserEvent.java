package org.kie.uberfire.social.activities.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FollowSocialUserEvent {

    private SocialUser follower;
    private SocialUser follow;

    public FollowSocialUserEvent() {

    }

    public FollowSocialUserEvent( SocialUser follower,
                                  SocialUser follow ) {
        this.follower = follower;
        this.follow = follow;
    }

    public SocialUser getFollow() {
        return follow;
    }

    public SocialUser getFollower() {
        return follower;
    }
}
