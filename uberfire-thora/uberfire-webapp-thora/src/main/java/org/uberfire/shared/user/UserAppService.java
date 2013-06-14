package org.uberfire.shared.user;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface UserAppService {

    UserProfileModel getUserProfile( String userName );

}

