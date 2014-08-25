package org.kie.uberfire.social.activities.client.gravatar;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.resources.client.ImageResource;
import org.kie.uberfire.social.activities.client.AppResource;
import org.kie.uberfire.social.activities.model.SocialUser;

public class GravatarBuilder {

    public enum SIZE {
        SMALL, BIG
    }

    public static Image generate( SocialUser socialUser,
                                  SIZE size ) {
        if ( socialUser.getEmail().isEmpty() ) {
            return generateDefaultImage( size );
        } else {
            return generateGravatarImage( socialUser, size );
        }
    }

    private static Image generateGravatarImage( SocialUser socialUser,

                                                SIZE size ) {
        Image gravatarImage;
        if ( size == SIZE.SMALL ) {
            gravatarImage = new Image( new GravatarImage( socialUser.getEmail(), 30 ).getUrl() );
        } else {
            gravatarImage = new Image( new GravatarImage( socialUser.getEmail(), 200 ).getUrl() );
        }

        return gravatarImage;
    }

    private static Image generateDefaultImage( SIZE size ) {
        Image userImage;
        if ( size == SIZE.SMALL ) {
            ImageResource imageResource = AppResource.INSTANCE.images().genericAvatar30px();
            userImage = new Image( imageResource );

        } else {
            ImageResource imageResource = AppResource.INSTANCE.images().genericAvatar();
            userImage = new Image( imageResource );
            return userImage;
        }
        return userImage;
    }
}
