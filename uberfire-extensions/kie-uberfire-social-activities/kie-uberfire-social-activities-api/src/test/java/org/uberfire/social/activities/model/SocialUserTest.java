package org.uberfire.social.activities.model;

import org.junit.Test;
import org.kie.uberfire.social.activities.model.SocialUser;

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
