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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.*;

@Dependent
@Templated
public class DriverDefMainPanelViewImpl
        extends Composite
        implements DriverDefMainPanelView {

    @DataField( "name-form-group" )
    private Element nameFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "name" )
    private TextBox nameTextBox;

    @DataField("name-help")
    private Element nameHelp = DOM.createSpan();

    @DataField ( "driver-class-form-group" )
    private Element driverClassFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "driver-class" )
    private TextBox driverClassTextBox;

    @DataField("driver-class-help")
    private Element driverClassHelp = DOM.createSpan();

    @DataField( "group-id-form-group" )
    private Element groupIdFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "group-id" )
    private TextBox groupIdTextBox;

    @DataField("group-id-help")
    private Element groupIdHelp = DOM.createSpan();

    @DataField( "artifact-id-form-group" )
    private Element artifactIdFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "artifact-id" )
    private TextBox artifactIdTextBox;

    @DataField("artifact-id-help")
    private Element artifactIdHelp = DOM.createSpan();

    @DataField( "version-form-group" )
    private Element versionFormGroup =  DOM.createDiv();

    @Inject
    @DataField ( "version" )
    private TextBox versionTextBox;

    @DataField("version-help")
    private Element versionHelp = DOM.createSpan();

    private DriverDefMainPanelView.Presenter presenter;

    public DriverDefMainPanelViewImpl() {
    }

    @Override
    public void init( final DriverDefMainPanelView.Presenter presenter ) {
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

    @Override
    public void setNameErrorMessage( final String message ) {
        setGroupOnError( nameFormGroup, true );
        setSpanMessage( nameHelp, message );
    }

    @Override
    public void clearNameErrorMessage() {
        setGroupOnError( nameFormGroup, false );
        clearSpanMessage( nameHelp );
    }

    @Override
    public void setDriverClass( final String driverClass ) {
        this.driverClassTextBox.setText( driverClass );
    }

    @Override
    public String getDriverClass() {
        return driverClassTextBox.getText();
    }

    @Override
    public void setDriverClassErrorMessage( final String message ) {
        setGroupOnError( driverClassFormGroup, true );
        setSpanMessage( driverClassHelp, message );
    }

    @Override
    public void clearDriverClassErrorMessage() {
        setGroupOnError( driverClassFormGroup, false );
        clearSpanMessage( driverClassHelp );
    }

    @Override
    public void setGroupId( final String groupId ) {
        groupIdTextBox.setText( groupId );
    }

    @Override
    public String getGroupId() {
        return groupIdTextBox.getText();
    }

    @Override
    public void setGroupIdErrorMessage( final String message ) {
        setGroupOnError( groupIdFormGroup, true );
        setSpanMessage( groupIdHelp, message );
    }

    @Override
    public void clearGroupIdErrorMessage() {
        setGroupOnError( groupIdFormGroup, false );
        clearSpanMessage( groupIdHelp );
    }

    @Override
    public void setArtifactId( final String artifactId ) {
        artifactIdTextBox.setText( artifactId );
    }

    @Override
    public String getArtifactId() {
        return artifactIdTextBox.getText();
    }

    @Override
    public void setArtifactIdErrorMessage( final String message ) {
        setGroupOnError( artifactIdFormGroup, true );
        setSpanMessage( artifactIdHelp, message );
    }

    @Override
    public void clearArtifactIdErrorMessage() {
        setGroupOnError( artifactIdFormGroup, false );
        clearSpanMessage( artifactIdHelp );
    }

    @Override
    public void setVersion( final String version ) {
        versionTextBox.setText( version );
    }

    @Override
    public String getVersion() {
        return versionTextBox.getText();
    }

    @Override
    public void setVersionErrorMessage( final String message ) {
        setGroupOnError( versionFormGroup, true );
        setSpanMessage( versionHelp, message );
    }

    @Override
    public void clearVersionErrorMessage() {
        setGroupOnError( versionFormGroup, false );
        clearSpanMessage( versionHelp );
    }

    @EventHandler( "name" )
    private void onNameChange( final ChangeEvent event ) {
        presenter.onNameChange();
    }

    @EventHandler( "driver-class" )
    private void onDriverClassChange( final ChangeEvent event ) {
        presenter.onDriverClassChange();
    }

    @EventHandler( "group-id")
    private void onGroupIdChange( final ChangeEvent event ) {
        presenter.onGroupIdChange();
    }

    @EventHandler( "artifact-id")
    private void onArtifactIdChange( final ChangeEvent event ) {
        presenter.onArtifactIdChange();
    }

    @EventHandler( "version")
    private void onVersionChange( final ChangeEvent event ) {
        presenter.onVersionChange();
    }
}
