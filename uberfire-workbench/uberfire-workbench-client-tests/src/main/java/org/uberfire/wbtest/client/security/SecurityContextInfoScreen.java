package org.uberfire.wbtest.client.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.client.local.api.SecurityContext;
import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

/**
 * Screen that shows details about the User object obtained via the security context cache.
 */
@Dependent
@Named("org.uberfire.wbtest.client.security.SecurityContextInfoScreen")
public class SecurityContextInfoScreen extends AbstractUserInfoScreen {

    @Inject
    public SecurityContextInfoScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    Label cookieLabel = new Label( Cookies.getCookie( UserCookieEncoder.USER_COOKIE_NAME ) );

    Button refreshButton = new Button( "Refresh Security Status" );

    @Inject SecurityContext securityContext;

    @PostConstruct
    public void setup() {
        panel.add( cookieLabel );

        cookieLabel.ensureDebugId( "SecurityStatusScreen-cookieLabel" );
        refreshButton.ensureDebugId( "SecurityStatusScreen-refreshButton" );

        refreshButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                updateLabels();
            }
        } );

        updateLabels();
    }

    public void updateLabels() {
        cookieLabel.setText( Cookies.getCookie( UserCookieEncoder.USER_COOKIE_NAME ) );
        super.updateLabels( securityContext.getCachedUser() );
    }
}
