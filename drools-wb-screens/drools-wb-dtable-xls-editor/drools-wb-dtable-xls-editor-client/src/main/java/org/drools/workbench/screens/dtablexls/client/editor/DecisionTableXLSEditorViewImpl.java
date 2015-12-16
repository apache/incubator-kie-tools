/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.client.editor;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class DecisionTableXLSEditorViewImpl
        extends KieEditorViewImpl
        implements DecisionTableXLSEditorView {

    interface ViewBinder
            extends
            UiBinder<Widget, DecisionTableXLSEditorViewImpl> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    //This is not part of the UiBinder definition as it is created dependent upon the file-type being uploaded
    AttachmentFileWidget uploadWidget;

    @UiField
    Button downloadButton;

    @UiField
    SimplePanel uploadWidgetContainer;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    private DecisionTableXLSEditorView.Presenter presenter;

    public DecisionTableXLSEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final DecisionTableXLSEditorView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupUploadWidget( final ClientResourceType resourceTypeDefinition ) {
        uploadWidget = new AttachmentFileWidget( new String[]{ resourceTypeDefinition.getSuffix() }, true );

        uploadWidgetContainer.clear();
        uploadWidgetContainer.setWidget( uploadWidget );

        uploadWidget.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                BusyPopup.showMessage( DecisionTableXLSEditorConstants.INSTANCE.Uploading() );
                presenter.onUpload();
            }
        } );
    }

    public void setPath( final Path path ) {
        downloadButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                Window.open( URLHelper.getDownloadUrl( path ),
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );
    }

    @Override
    public void submit( final Path path ) {
        uploadWidget.submit( path,
                             URLHelper.getServletUrl(),
                             new Command() {

                                 @Override
                                 public void execute() {
                                     BusyPopup.close();
                                     notifySuccess();
                                 }

                             },
                             new Command() {

                                 @Override
                                 public void execute() {
                                     BusyPopup.close();
                                 }

                             }
                           );
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        uploadWidget.setEnabled( !isReadOnly );
    }

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

}