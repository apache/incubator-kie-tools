package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;

@Service
@ApplicationScoped
public class SocialUserRepository implements SocialUserRepositoryAPI {

    @Inject
    @Named( "socialUserPersistenceAPI" )
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Override
    public List<SocialUser> findAllUsers() {
        List<String> socialUsersName = socialUserPersistenceAPI.getSocialUsersName();
        List<SocialUser> users = new ArrayList<SocialUser>();
        for ( String userName : socialUsersName ) {
            users.add( socialUserPersistenceAPI.getSocialUser( userName ) );
        }
        return users;
    }

    @Override
    public SocialUser findSocialUser( String userName ) {
        return socialUserPersistenceAPI.getSocialUser( userName );
    }

    public SocialUser systemUser() {
        return socialUserPersistenceAPI.systemUser();
    }
}
