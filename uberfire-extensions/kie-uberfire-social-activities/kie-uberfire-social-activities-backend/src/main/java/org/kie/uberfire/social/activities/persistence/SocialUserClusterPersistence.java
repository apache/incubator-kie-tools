package org.kie.uberfire.social.activities.persistence;

import com.google.gson.Gson;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class SocialUserClusterPersistence extends SocialUserCachePersistence {

    private final SocialUserClusterMessaging socialUserClusterMessaging;

    public SocialUserClusterPersistence( SocialUserServicesExtendedBackEndImpl userServicesBackend,
                                         UserServicesImpl userServices,
                                         IOService ioService,
                                         Gson gson,
                                         SocialUserClusterMessaging socialUserClusterMessaging ) {
        super( userServicesBackend, userServices, ioService, gson );
        this.socialUserClusterMessaging = socialUserClusterMessaging;
    }

    @Override
    public void updateUsers( SocialUser... users ) {
        for ( SocialUser user : users ) {
            usersCache.put( user.getName(), user );
            Path userFile =userServicesBackend.buildPath( SOCIAL_FILES,user.getName() );
            try {
                String json = gson.toJson( user );
                ioService.write( userFile, json );
            } catch ( Exception e ) {
                throw new ErrorUpdatingUsers( e );
            }
            socialUserClusterMessaging.notify( user );
        }
    }

    @Override
    String syncUserNamesCacheAndFile( String userName ) {
        if ( usersNamesCache.contains( userName ) ) {
            return userName;
        } else {
            usersNamesCache.add( userName );
            SocialUser socialUser = createOrRetrieveUserData( userName );
            usersCache.put( userName, socialUser );
            writeUserNamesOnFile( usersNamesCache );
            return userName;
        }
    }

    public void sync( SocialUser user ) {
        if (!usersNamesCache.contains( user.getName() )){
            usersNamesCache.add( user.getName() );
        }
        usersCache.put( user.getName(), user );
    }


}

