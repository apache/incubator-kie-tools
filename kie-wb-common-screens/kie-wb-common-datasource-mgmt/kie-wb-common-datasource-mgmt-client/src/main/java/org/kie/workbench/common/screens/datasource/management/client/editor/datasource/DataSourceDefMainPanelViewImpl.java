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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.*;

@Dependent
@Templated
public class DataSourceDefMainPanelViewImpl
        extends Composite
        implements DataSourceDefMainPanelView {

    @DataField ( "name-form-group" )
    private Element nameFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "name" )
    private TextBox nameTextBox;

    @DataField("name-help")
    private Element nameHelp = DOM.createSpan();

    @DataField ( "connection-url-form-group" )
    private Element connectionURLFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "connection-url" )
    private TextBox connectionURLTextBox;

    @DataField("connection-url-help")
    private Element connectionURLHelp = DOM.createSpan();

    @DataField ( "user-form-group" )
    private Element userFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "user" )
    private TextBox userTextBox;

    @DataField( "user-help" )
    private Element userHelp = DOM.createSpan();

    @DataField ( "password-form-group" )
    private Element passwordFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "password" )
    private TextBox passwordTextBox;

    @DataField( "password-help" )
    private Element passwordHelp = DOM.createSpan();

    @DataField ( "driver-form-group" )
    private Element driverFormGroup = DOM.createDiv();

    @Inject
    @DataField ( "driver-selector" )
    private Select driverSelector;

    @DataField( "driver-selector-help" )
    private Element driverSelectorHelp = DOM.createSpan();

    @Inject
    @DataField("test-connection-button")
    private Button testConnection;

    private DataSourceDefMainPanelView.Presenter presenter;

    public DataSourceDefMainPanelViewImpl( ) {
    }

    @Override
    public void init( final DataSourceDefMainPanelView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setName( final String name ) {
        this.nameTextBox.setText( name );
    }

    @Override
    public String getName() {
        return nameTextBox.getText();
    }

    public void setNameErrorMessage( final String message ) {
        setGroupOnError( nameFormGroup, true );
        setSpanMessage( nameHelp, message );
    }

    public void clearNameErrorMessage() {
        setGroupOnError( nameFormGroup, false );
        clearSpanMessage( nameHelp );
    }

    @Override
    public String getConnectionURL() {
        return connectionURLTextBox.getText();
    }

    @Override
    public void setConnectionURL( final String connectionURL ) {
        this.connectionURLTextBox.setText( connectionURL );
    }

    @Override
    public void setConnectionURLErrorMessage( String message ) {
        setGroupOnError( connectionURLFormGroup, true );
        setSpanMessage( connectionURLHelp, message );
    }

    @Override
    public void clearConnectionURLErrorMessage() {
        setGroupOnError( connectionURLFormGroup, false );
        clearSpanMessage( connectionURLHelp );
    }

    @Override
    public String getUser() {
        return userTextBox.getText();
    }

    @Override
    public void setUser( final String user ) {
        this.userTextBox.setText( user );
    }

    @Override
    public void setUserErrorMessage( String message ) {
        setGroupOnError( userFormGroup, true );
        setSpanMessage( userHelp, message );
    }

    @Override
    public void clearUserErrorMessage() {
        setGroupOnError( userFormGroup, false );
        clearSpanMessage( userHelp );
    }

    @Override
    public String getPassword() {
        return passwordTextBox.getText();
    }

    @Override
    public void setPassword( final String password ) {
        this.passwordTextBox.setText( password );
    }

    @Override
    public void setPasswordErrorMessage( String message ) {
        setGroupOnError( passwordFormGroup, true );
        setSpanMessage( passwordHelp, message );
    }

    @Override
    public void clearPasswordErrorMessage() {
        setGroupOnError( passwordFormGroup, false );
        clearSpanMessage( passwordHelp );
    }

    @Override
    public String getDriver() {
        return driverSelector.getValue();
    }

    @Override
    public void setDriver( final String driver ) {
        driverSelector.setValue( driver );
        refreshDriverSelector();
    }

    @Override
    public void setDriverErrorMessage( final String message ) {
        setGroupOnError( driverFormGroup, true );
        setSpanMessage( driverSelectorHelp, message );
    }

    @Override
    public void clearDriverErrorMessage() {
        setGroupOnError( driverFormGroup, false );
        clearSpanMessage( driverSelectorHelp );
    }

    @Override
    public void loadDriverOptions( final List<Pair<String, String>> driverOptions, final boolean addEmptyOption ) {
        driverSelector.clear();
        if ( addEmptyOption ) {
            driverSelector.add( newOption( "", "" ) );
        }
        for ( Pair<String, String> optionPair: driverOptions ) {
            driverSelector.add( newOption( optionPair.getK1(), optionPair.getK2() ));
        }
        refreshDriverSelector();
    }

    @EventHandler( "name" )
    private void onNameChange( final ChangeEvent event ) {
        presenter.onNameChange();
    }

    @EventHandler( "connection-url")
    private void onConnectionURLChange( final ChangeEvent event ) {
        presenter.onConnectionURLChange();
    }

    @EventHandler( "user" )
    private void onUserChange( final ChangeEvent event ) {
        presenter.onUserChange();
    }

    @EventHandler( "password" )
    private void onPasswordChange( final ChangeEvent event ) {
        presenter.onPasswordChange();
    }

    @EventHandler( "driver-selector" )
    private void onDriverChange( final ChangeEvent event ) {
        presenter.onDriverChange();
    }

    @EventHandler( "test-connection-button" )
    private void onTestConnection( final ClickEvent event ) {
        presenter.onTestConnection();
    }

    private Option newOption( final String text, final String value ) {
        final Option option = new Option();
        option.setValue( value );
        option.setText( text );
        return option;
    }

    private void refreshDriverSelector() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                driverSelector.refresh();
            }
        } );
    }

}