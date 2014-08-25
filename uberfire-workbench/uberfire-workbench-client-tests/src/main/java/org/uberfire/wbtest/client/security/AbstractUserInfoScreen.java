package org.uberfire.wbtest.client.security;

import javax.annotation.PostConstruct;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractUserInfoScreen extends AbstractTestScreenActivity {

    protected AbstractUserInfoScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    VerticalPanel panel = new VerticalPanel();

    Label userLabel = new Label( "Not initialized" );
    Label rolesLabel = new Label( "Not initialized" );
    Label groupsLabel = new Label( "Not initialized" );

    @PostConstruct
    private void createLabels() {
        panel.add( userLabel );
        panel.add( rolesLabel );
        panel.add( groupsLabel );

        userLabel.ensureDebugId( "SecurityStatusScreen-userLabel" );
        rolesLabel.ensureDebugId( "SecurityStatusScreen-rolesLabel" );
        groupsLabel.ensureDebugId( "SecurityStatusScreen-groupsLabel" );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

    public void updateLabels( User user ) {
        userLabel.setText( user.getIdentifier() );
        rolesLabel.setText( user.getRoles().toString() );
        groupsLabel.setText( user.getGroups().toString() );
    }
}
