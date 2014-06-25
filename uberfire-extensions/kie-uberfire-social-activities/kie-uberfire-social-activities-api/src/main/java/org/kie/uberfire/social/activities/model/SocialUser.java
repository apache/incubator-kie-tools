package org.kie.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String email;

    private List<String> followersName = new ArrayList<String>();

    private List<String> followingName = new ArrayList<String>();

    public SocialUser() {
    }

    public SocialUser( String name ) {
        this.name = name;
    }

    public SocialUser( String name,
                       List<String> followersName,
                       List<String> followingName ) {
        this.name = name;
        this.followersName = followersName;
        this.followingName = followingName;

    }

    public void follow( SocialUser anotherUser ) {
        if ( validate( anotherUser ) ) {
            followingName.add( anotherUser.getName() );
            anotherUser.addFollower( this );
        }
    }

    private boolean validate( SocialUser anotherUser ) {
        return !this.equals( anotherUser ) && !followingName.contains( anotherUser.getName() );
    }

    public void unfollow( SocialUser anotherUser ) {
        if ( !this.equals( anotherUser ) ) {
            followingName.remove( anotherUser.getName() );
            anotherUser.removeFollower( this );
        }
    }

    private void removeFollower( SocialUser socialUser ) {
        followersName.remove( socialUser.getName() );
    }

    private void addFollower( SocialUser socialUser ) {
        this.followersName.add( socialUser.getName() );
    }

    public List<String> getFollowersName() {

        if ( followersName == null ) {
            this.followersName = new ArrayList<String>();
        }
        return followersName;
    }

    public String getName() {
        return name;
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
                "name='" + name +
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

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getEmail() {
        if ( email == null ) {
            return "";
        }
        return email;
    }
}
