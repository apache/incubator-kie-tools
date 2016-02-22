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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class NewDependencyPopupViewImpl
        extends BaseModal
        implements NewDependencyPopupView {

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    TextBox groupIdTextBox;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    TextBox versionTextBox;

    @UiField
    HelpBlock groupIdHelpBlock;

    @UiField
    HelpBlock artifactIdHelpBlock;

    @UiField
    HelpBlock versionHelpBlock;

    @UiField
    FormGroup groupIdFromGroup;

    @UiField
    FormGroup artifactIdFromGroup;

    @UiField
    FormGroup versionFromGroup;

    private NewDependencyPopup presenter;

    public NewDependencyPopupViewImpl() {
        setTitle( ProjectEditorResources.CONSTANTS.Dependency() );
        setBody( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKCancelButtons(
                     new com.google.gwt.user.client.Command() {
                         @Override
                         public void execute() {
                             presenter.onOkClicked();
                         }
                     },
                     new com.google.gwt.user.client.Command() {
                         @Override
                         public void execute() {
                             hide();
                         }
                     }
             )
           );
    }

    @Override
    public void setPresenter( final NewDependencyPopup presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void invalidGroupId( final String errorMessage ) {
        groupIdHelpBlock.setText( errorMessage );
    }

    @Override
    public void invalidArtifactId( final String errorMessage ) {
        artifactIdHelpBlock.setText( errorMessage );
    }

    @Override
    public void invalidVersion( final String errorMessage ) {
        versionHelpBlock.setText( errorMessage );
    }

    @Override
    public void setGroupIdValidationState( final ValidationState validationState ) {
        groupIdFromGroup.setValidationState( validationState );
    }

    @Override
    public void setArtifactIdValidationState( final ValidationState validationState ) {
        artifactIdFromGroup.setValidationState( validationState );
    }

    @Override
    public void setVersionValidationState( final ValidationState validationState ) {
        versionFromGroup.setValidationState( validationState );
    }

    @Override
    public void clean() {
        groupIdTextBox.clear();
        artifactIdTextBox.clear();
        versionTextBox.clear();
    }

    @UiHandler( "groupIdTextBox" )
    public void onGroupIdChange( final ValueChangeEvent<String> event ) {
        presenter.onGroupIdChange( event.getValue() );
    }

    @UiHandler( "artifactIdTextBox" )
    public void onArtifactIdChange( final ValueChangeEvent<String> event ) {
        presenter.onArtifactIdChange( event.getValue() );
    }

    @UiHandler( "versionTextBox" )
    public void onVersionChange( final ValueChangeEvent<String> event ) {
        presenter.onVersionChange( event.getValue() );
    }

    @UiHandler( "groupIdTextBox" )
    public void onGroupIdChangeTyped( final KeyUpEvent event ) {
        presenter.onGroupIdChange( groupIdTextBox.getText() );
    }

    @UiHandler( "artifactIdTextBox" )
    public void onArtifactIdChangeTyped( final KeyUpEvent event ) {
        presenter.onArtifactIdChange( artifactIdTextBox.getText() );
    }

    @UiHandler( "versionTextBox" )
    public void onVersionChange( final KeyUpEvent event ) {
        presenter.onVersionChange( versionTextBox.getText() );
    }

    interface Binder extends UiBinder<Form, NewDependencyPopupViewImpl> {
    }

}
