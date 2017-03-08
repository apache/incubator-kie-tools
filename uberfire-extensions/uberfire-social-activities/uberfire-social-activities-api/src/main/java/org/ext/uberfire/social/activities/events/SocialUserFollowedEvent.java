/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.ext.uberfire.social.activities.events;

import java.io.Serializable;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialUserFollowedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SocialUser user;
    private final SocialUser follow;

    public SocialUserFollowedEvent(@MapsTo("user") SocialUser user,
                                   @MapsTo("follow") SocialUser follow) {
        this.user = user;
        this.follow = follow;
    }

    public SocialUser getFollow() {
        return follow;
    }

    public SocialUser getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "SocialUserFollowedEvent{" +
                "user=" + user +
                ", follow=" + follow +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SocialUserFollowedEvent that = (SocialUserFollowedEvent) o;

        if (!user.equals(that.user)) {
            return false;
        }
        return follow.equals(that.follow);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + follow.hashCode();
        return result;
    }
}
