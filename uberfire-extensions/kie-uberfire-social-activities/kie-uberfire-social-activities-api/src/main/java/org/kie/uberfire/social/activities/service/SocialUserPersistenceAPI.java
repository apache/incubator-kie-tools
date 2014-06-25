package org.kie.uberfire.social.activities.service;

import java.util.List;

import org.kie.uberfire.social.activities.model.SocialUser;

public interface SocialUserPersistenceAPI {

    List<String> getSocialUsersName();

    SocialUser getSocialUser( String userName );

    void updateUsers( SocialUser... users );

    SocialUser systemUser();

}
