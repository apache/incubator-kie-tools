package org.kie.uberfire.social.activities.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.SocialUser;

@Remote
public interface SocialUserRepositoryAPI {

    SocialUser findSocialUser( String userName );

    List<SocialUser> findAllUsers();
}
