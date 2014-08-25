package org.uberfire.wbtest.client.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Screen that shows details about the User object obtained via field injection.
 */
@Dependent
@Named("org.uberfire.wbtest.client.security.InjectedUserScreen")
public class InjectedUserScreen extends AbstractUserInfoScreen {

    @Inject
    public InjectedUserScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Inject User user;

    @PostConstruct
    public void setupLabels() {
        updateLabels( user );
    }

}
