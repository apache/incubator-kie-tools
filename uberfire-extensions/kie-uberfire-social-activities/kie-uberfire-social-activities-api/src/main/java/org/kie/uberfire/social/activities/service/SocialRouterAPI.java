package org.kie.uberfire.social.activities.service;

public interface SocialRouterAPI {

    SocialAdapter getSocialAdapterByPath( String pathInfo );

    SocialAdapter getSocialAdapter( String adapterName );
}
