/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
