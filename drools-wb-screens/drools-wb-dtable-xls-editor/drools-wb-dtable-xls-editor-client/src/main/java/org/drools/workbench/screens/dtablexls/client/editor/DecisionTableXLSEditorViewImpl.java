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

package org.drools.workbench.screens.dtablexls.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Well;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.dtablexls.client.resources.DecisionTableXLSResources;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.workbench.events.NotificationEvent;

public class DecisionTableXLSEditorViewImpl
        extends Composite
        implements DecisionTableXLSEditorView {

    private final Button uploadButton = new Button( DecisionTableXLSEditorConstants.INSTANCE.Upload() );
    private final Button downloadButton = new Button( DecisionTableXLSEditorConstants.INSTANCE.Download() );

    private final VerticalPanel layout = new VerticalPanel();
    private final FormStyleLayout ts = new FormStyleLayout( getIcon(),
                                                            DecisionTableXLSEditorConstants.INSTANCE.DecisionTable() );

    @Inject
    private AttachmentFileWidget uploadWidget;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @PostConstruct
    public void init() {
        layout.setWidth( "100%" );
        layout.add( ts );
        initWidget( layout );
        setWidth( "100%" );
    }

    public void setPath( final Path path ) {
        //Upload widgets
        final Well uploadWell = new Well();
        final HorizontalPanel uploadContainer = new HorizontalPanel();
        uploadContainer.add( new Label( DecisionTableXLSEditorConstants.INSTANCE.UploadNewVersion() + ":" ) );
        uploadContainer.add( uploadWidget );
        uploadContainer.add( uploadButton );
        uploadWell.add( uploadContainer );

        ts.addRow( uploadWell );
        uploadButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                BusyPopup.showMessage( DecisionTableXLSEditorConstants.INSTANCE.Uploading() );
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
        } );

        //Download widgets
        final Well downloadWell = new Well();
        final HorizontalPanel downloadContainer = new HorizontalPanel();
        downloadContainer.add( new Label( DecisionTableXLSEditorConstants.INSTANCE.DownloadCurrentVersion() + ":" ) );
        downloadContainer.add( downloadButton );
        downloadWell.add( downloadContainer );
        ts.addRow( downloadWell );

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
    public void setReadOnly( final boolean isReadOnly ) {
        uploadButton.setEnabled( !isReadOnly );
    }

    private Image getIcon() {
        Image image = new Image( DecisionTableXLSResources.INSTANCE.images().decisionTableIconLarge() );
        image.setAltText( DecisionTableXLSEditorConstants.INSTANCE.DecisionTable() );
        return image;
    }

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }
}
