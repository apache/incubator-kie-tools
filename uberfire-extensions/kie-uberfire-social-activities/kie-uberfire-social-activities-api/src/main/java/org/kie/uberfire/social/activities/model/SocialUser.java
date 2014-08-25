package org.kie.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String realName;

    private String email;

    private List<String> followersName = new ArrayList<String>();

    private List<String> followingName = new ArrayList<String>();

    public SocialUser() {
    }

    public SocialUser( String username ) {
        this.userName = username;
        this.realName = "";
        this.email = "";
    }

    public SocialUser( String username,
                       List<String> followersName,
                       List<String> followingName ) {
        this.userName = username;
        this.realName = "";
        this.email = "";
        this.followersName = followersName;
        this.followingName = followingName;
    }

    public SocialUser( String username,
                       String realName,
                       String email,
                       List<String> followersName,
                       List<String> followingName ) {
        this.userName = username;
        this.realName = realName;
        this.email = email;
        this.followersName = followersName;
        this.followingName = followingName;
    }

    public void follow( SocialUser anotherUser ) {
        if ( validate( anotherUser ) ) {
            followingName.add( anotherUser.getUserName() );
            anotherUser.addFollower( this );
        }
    }

    private boolean validate( SocialUser anotherUser ) {
        return !this.equals( anotherUser ) && !followingName.contains( anotherUser.getUserName() );
    }

    public void unfollow( SocialUser anotherUser ) {
        if ( !this.equals( anotherUser ) ) {
            followingName.remove( anotherUser.getUserName() );
            anotherUser.removeFollower( this );
        }
    }

    private void removeFollower( SocialUser socialUser ) {
        followersName.remove( socialUser.getUserName() );
    }

    private void addFollower( SocialUser socialUser ) {
        this.followersName.add( socialUser.getUserName() );
    }

    public List<String> getFollowersName() {

        if ( followersName == null ) {
            this.followersName = new ArrayList<String>();
        }
        return followersName;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getFollowingName() {
        if ( followingName == null ) {
            this.followingName = new ArrayList<String>();
        }
        return followingName;
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "userName='" + userName +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof SocialUser ) ) {
            return false;
        }

        SocialUser that = (SocialUser) o;

        if ( userName != null ? !userName.equals( that.userName ) : that.userName != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return userName != null ? userName.hashCode() : 0;
    }

    public String getEmail() {
        if ( email == null ) {
            return "";
        }
        return email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName( String realName ) {
        this.realName = realName;
    }

    public void setEmail( String email ) {
        this.email = email;
    }
}
