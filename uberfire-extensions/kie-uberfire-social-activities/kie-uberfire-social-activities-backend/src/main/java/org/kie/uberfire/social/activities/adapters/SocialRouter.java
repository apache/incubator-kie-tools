package org.kie.uberfire.social.activities.adapters;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialRouterAPI;

@Service
@ApplicationScoped
public class SocialRouter implements SocialRouterAPI {

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepositoryAPI;

    private Map<Class, SocialAdapter> socialAdapters;

    @PostConstruct
    public void setup() throws ServletException {
        socialAdapters = socialAdapterRepositoryAPI.getSocialAdapters();
    }

    public SocialRouter() {

    }

    public String extractPath( String path ) {
        if ( path.length() > 0 && isSlashFirstChar( path ) ) {
            String newPath = path.substring( 1 );
            return newPath;
        }
        throw new NotASocialPathException();
    }

    private boolean isSlashFirstChar( String path ) {
        return path.substring( 0, 1 ).equalsIgnoreCase( "/" );
    }

    public SocialAdapter getSocialAdapterByPath( String path ) throws SocialAdapterNotFound {
        String newPath = extractPath( path );
        return getSocialAdapter( newPath );
    }

    public SocialAdapter getSocialAdapter( String adapterName ) {
        for ( Map.Entry<Class, SocialAdapter> entry : getSocialAdapters().entrySet() ) {
            SocialAdapter socialAdapter = entry.getValue();
            if ( thereIsASocialAdapter( socialAdapter ) && nameIsThisSocialAdapter( adapterName, socialAdapter ) ) {
                return socialAdapter;
            }
        }
        throw new SocialAdapterNotFound();
    }

    private boolean nameIsThisSocialAdapter( String newPath,
                                             SocialAdapter socialAdapter ) {
        return socialAdapter.socialEventType().toString().equalsIgnoreCase( newPath );
    }

    private boolean thereIsASocialAdapter( SocialAdapter socialAdapter ) {
        return socialAdapter != null && socialAdapter.socialEventType() != null;
    }

    public Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapters;
    }

    public class SocialAdapterNotFound extends RuntimeException {

    }

    private class NotASocialPathException extends RuntimeException {

    }
}
