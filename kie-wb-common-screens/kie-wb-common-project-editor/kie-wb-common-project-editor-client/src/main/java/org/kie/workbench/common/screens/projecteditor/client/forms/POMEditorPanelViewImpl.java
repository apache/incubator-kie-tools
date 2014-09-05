/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.uberfire.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class POMEditorPanelViewImpl
        extends Composite
        implements POMEditorPanelView {

    private String tabTitleLabel = ProjectEditorResources.CONSTANTS.ProjectModel();

    interface GroupArtifactVersionEditorPanelViewImplBinder
            extends
            UiBinder<Widget, POMEditorPanelViewImpl> {

    }

    private static GroupArtifactVersionEditorPanelViewImplBinder uiBinder = GWT.create( GroupArtifactVersionEditorPanelViewImplBinder.class );

    private final Event<NotificationEvent> notificationEvent;

    @UiField
    TextBox pomNameTextBox;

    @UiField
    TextArea pomDescriptionTextArea;

    @UiField(provided = true)
    GAVEditor gavEditor;

    private Presenter presenter;

    @Inject
    public POMEditorPanelViewImpl( Event<NotificationEvent> notificationEvent,
                                   GAVEditor gavEditor ) {
        this.gavEditor = gavEditor;
        initWidget( uiBinder.createAndBindUi( this ) );
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showSaveSuccessful( String fileName ) {
        notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( fileName ) ) );
    }

    @Override
    public String getTitleWidget() {
        return tabTitleLabel;
    }

    @Override
    public void setName( String projectName ) {
        pomNameTextBox.setText( projectName );
    }

    @Override
    public void setDescription( String projectDescription ) {
        pomDescriptionTextArea.setText( projectDescription );
    }

    @Override
    public void setGAV( GAV gav ) {
        gavEditor.setGAV( gav );
    }

    @Override
    public void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler ) {
        gavEditor.addGroupIdChangeHandler( changeHandler );
    }

    @Override
    public void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler ) {
        gavEditor.addArtifactIdChangeHandler( changeHandler );
    }

    @Override
    public void addVersionChangeHandler( VersionChangeHandler changeHandler ) {
        gavEditor.addVersionChangeHandler( changeHandler );
    }

    @Override
    public void setReadOnly() {
        gavEditor.setReadOnly();
    }

    @Override
    public void disableGroupID(String reason) {
        gavEditor.disableGroupID(reason);
    }

    @Override
    public void disableArtifactID(String reason) {
        gavEditor.disableArtifactID(reason);
    }

    @Override
    public void setTitleText( String titleText ) {
        tabTitleLabel = titleText;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @UiHandler("pomNameTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onNameChange( KeyUpEvent event ) {
        presenter.onNameChange( pomNameTextBox.getText() );
    }

    @UiHandler("pomDescriptionTextArea")
    public void onDescriptionChange( ValueChangeEvent<String> event ) {
        presenter.onDescriptionChange( pomDescriptionTextArea.getText() );
    }
}
