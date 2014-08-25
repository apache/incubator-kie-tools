package org.kie.uberfire.social.activities.persistence;

import com.google.gson.Gson;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class SocialUserInstancePersistence extends SocialUserCachePersistence {

    public SocialUserInstancePersistence( SocialUserServicesExtendedBackEndImpl userServicesBackend,
                                          UserServicesImpl userServices,
                                          IOService ioService,
                                          Gson gson ) {
        super( userServicesBackend,userServices,  ioService, gson );
    }

    @Override
    public void updateUsers( SocialUser... users ) {
        for ( SocialUser user : users ) {
            usersCache.put( user.getUserName(), user );
            Path userFile =userServicesBackend.buildPath( SOCIAL_FILES,user.getUserName() );
            try {
                String json = gson.toJson( user );
                ioService.write( userFile, json );
            } catch ( Exception e ) {
                throw new ErrorUpdatingUsers( e );
            }
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
}

