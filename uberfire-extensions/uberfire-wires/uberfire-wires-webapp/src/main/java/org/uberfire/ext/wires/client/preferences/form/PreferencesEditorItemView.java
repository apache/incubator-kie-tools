/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.client.preferences.form;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PreferencesEditorItemView implements IsElement,
                                                  PreferencesEditorItemPresenter.View {

    private PreferencesEditorItemPresenter presenter;

    @DataField("preference-label")
    Element preferenceLabel = DOM.createLabel();

    @Inject
    @DataField("preference-value")
    TextInput preferenceValue;

    @Inject
    @DataField("user-related-scopes")
    Div userRelatedScopes;

    @Inject
    @DataField("component-related-scopes")
    Div componentRelatedScopes;

    @Inject
    @DataField("user-button")
    Button userButton;

    @Inject
    @DataField("all-users-button")
    Button allUsersButton;

    @Inject
    @DataField("component-button")
    Button componentButton;

    @Inject
    @DataField("entire-application-button")
    Button entireApplicationButton;

    @Inject
    public PreferencesEditorItemView() {
        super();
    }

    @Override
    public void init( final PreferencesEditorItemPresenter presenter ) {
        this.presenter = presenter;

        userButton.setTitle( "Only for me" );
        allUsersButton.setTitle( "For all users" );
        entireApplicationButton.setTitle( "For the entire application" );
    }

    @Override
    public void setKey( final String label ) {
        preferenceLabel.setInnerHTML( label );
    }

    @Override
    public void setValue( final Object value ) {
        preferenceValue.setValue( (String) value );
    }

    @Override
    public void setComponentTitle( final String componentTitle ) {
        componentButton.setTitle( "Only for " + componentTitle );
    }

    @Override
    public Object getNewPreferenceValue() {
        return preferenceValue.getValue();
    }

    public void setViewMode( ViewMode viewMode ) {
        if ( viewMode.equals( ViewMode.COMPONENT ) || presenter.getViewMode().equals( ViewMode.GLOBAL ) ) {
            userRelatedScopes.getStyle().setProperty( "display", "none" );
        }

        if ( viewMode.equals( ViewMode.USER ) || presenter.getViewMode().equals( ViewMode.GLOBAL ) ) {
            componentRelatedScopes.getStyle().setProperty( "display", "none" );
        }
    }

    @Override
    public void setUserScopeSelected( final boolean selected ) {
        userButton.setDisabled( selected );
    }

    @Override
    public void setAllUsersScopeSelected( final boolean selected ) {
        allUsersButton.setDisabled( selected );
    }

    @Override
    public void setComponentScopeSelected( final boolean selected ) {
        componentButton.setDisabled( selected );
    }

    @Override
    public void setEntireApplicationScopeSelected( final boolean selected ) {
        entireApplicationButton.setDisabled( selected );
    }

    @Override
    public boolean isUserScopeSelected() {
        return userButton.getDisabled();
    }

    @Override
    public boolean isAllUsersScopeSelected() {
        return allUsersButton.getDisabled();
    }

    @Override
    public boolean isComponentScopeSelected() {
        return componentButton.getDisabled();
    }

    @Override
    public boolean isEntireApplicationScopeSelected() {
        return entireApplicationButton.getDisabled();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("user-button")
    public void onUserButtonClick( final Event event ) {
        userButton.setDisabled( true );
        allUsersButton.setDisabled( false );
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("all-users-button")
    public void onAllUsersButtonClick( final Event event ) {
        userButton.setDisabled( false );
        allUsersButton.setDisabled( true );
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("component-button")
    public void onComponentButtonClick( final Event event ) {
        componentButton.setDisabled( true );
        entireApplicationButton.setDisabled( false );
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("entire-application-button")
    public void onEntireApplicationButtonClick( final Event event ) {
        componentButton.setDisabled( false );
        entireApplicationButton.setDisabled( true );
    }
}
