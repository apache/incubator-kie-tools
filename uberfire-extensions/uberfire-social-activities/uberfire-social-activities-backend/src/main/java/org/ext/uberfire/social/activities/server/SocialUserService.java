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

package org.ext.uberfire.social.activities.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.ext.uberfire.social.activities.events.SocialUserFollowedEvent;
import org.ext.uberfire.social.activities.events.SocialUserUnFollowedEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.ext.uberfire.social.activities.service.SocialUserServiceAPI;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SocialUserService implements SocialUserServiceAPI {

    @Inject
    private Event<SocialUserFollowedEvent> followedEvent;

    @Inject
    private Event<SocialUserUnFollowedEvent> unFollowedEvent;

    @Inject
    @Named("socialUserPersistenceAPI")
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Override
    public void userFollowAnotherUser(String followerUsername,
                                      String followUsername) {
        SocialUser follower = socialUserPersistenceAPI.getSocialUser(followerUsername);
        SocialUser follow = socialUserPersistenceAPI.getSocialUser(followUsername);
        follower.follow(follow);
        socialUserPersistenceAPI.updateUsers(follower,
                                             follow);
        followedEvent.fire(new SocialUserFollowedEvent(follower,
                                                       follow));
    }

    @Override
    public void userUnfollowAnotherUser(String followerUsername,
                                        String followUsername) {
        SocialUser follower = socialUserPersistenceAPI.getSocialUser(followerUsername);
        SocialUser user = socialUserPersistenceAPI.getSocialUser(followUsername);
        follower.unfollow(user);
        socialUserPersistenceAPI.updateUsers(follower,
                                             user);
        unFollowedEvent.fire(new SocialUserUnFollowedEvent(follower,
                                                           user));
    }

    @Override
    public void update(SocialUser... users) {
        socialUserPersistenceAPI.updateUsers(users);
    }
}
