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

package org.ext.uberfire.social.activities.model;

import org.junit.Test;
import org.ext.uberfire.social.activities.model.SocialUser;

import static org.junit.Assert.assertTrue;

public class SocialUserTest {

    @Test
    public void userCannotFollowHimself(){
        SocialUser user1 = new SocialUser( "user1" );

        user1.follow( user1 );
        assertTrue(user1.getFollowersName().isEmpty());
        assertTrue(user1.getFollowingName().isEmpty());
    }

    @Test
    public void followBasicTest(){
        SocialUser user1 = new SocialUser( "user1" );
        SocialUser user2 = new SocialUser( "user2" );

        user1.follow( user2 );

        assertTrue( user1.getFollowingName().contains( user2.getUserName() ) );
        assertTrue( user2.getFollowersName().contains( user1.getUserName() ) );
    }

    @Test
    public void unfollowTest(){
        SocialUser user1 = new SocialUser( "user1" );
        SocialUser user2 = new SocialUser( "user2" );

        user1.follow( user2 );

        assertTrue( user1.getFollowingName().contains( user2.getUserName() ) );
        assertTrue( user2.getFollowersName().contains( user1.getUserName() ) );

        user1.unfollow( user2 );
        assertTrue(user1.getFollowingName().isEmpty());
        assertTrue(user2.getFollowersName().isEmpty());
    }


}
