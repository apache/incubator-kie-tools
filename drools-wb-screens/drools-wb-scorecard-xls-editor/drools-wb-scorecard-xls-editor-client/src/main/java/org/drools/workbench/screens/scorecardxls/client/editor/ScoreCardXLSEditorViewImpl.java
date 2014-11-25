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

package org.drools.workbench.screens.scorecardxls.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Well;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.scorecardxls.client.resources.ScoreCardXLSEditorResources;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

public class ScoreCardXLSEditorViewImpl
        extends KieEditorViewImpl
        implements ScoreCardXLSEditorView {

    private AttachmentFileWidget uploadWidget;

    private final Button downloadButton = new Button( ScoreCardXLSEditorConstants.INSTANCE.Download() );

    private final VerticalPanel layout = new VerticalPanel();
    private final FormStyleLayout ts = new FormStyleLayout( getIcon(),
                                                            ScoreCardXLSEditorConstants.INSTANCE.ScoreCard() );

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private ScoreCardXLSResourceType resourceType;

    private ScoreCardXLSEditorView.Presenter presenter;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo;

    @PostConstruct
    public void init() {
        uploadWidget = new AttachmentFileWidget( new String[]{ resourceType.getSuffix() }, true );
        layout.setWidth( "100%" );
        layout.add( ts );
        initWidget( layout );
        setWidth( "100%" );
    }

    @Override
    public void init( final ScoreCardXLSEditorView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setConcurrentUpdateSessionInfo( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
        this.concurrentUpdateSessionInfo = eventInfo;
    }

    public void setPath( final Path path ) {
        //Upload widgets
        final Well uploadWell = new Well();
        final HorizontalPanel uploadContainer = new HorizontalPanel();
        uploadContainer.add( new Label( ScoreCardXLSEditorConstants.INSTANCE.UploadNewVersion() + ":" ) );
        uploadContainer.add( uploadWidget );
        uploadWell.add( uploadContainer );

        ts.addRow( uploadWell );
        uploadWidget.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                BusyPopup.showMessage( ScoreCardXLSEditorConstants.INSTANCE.Uploading() );

                if ( concurrentUpdateSessionInfo != null ) {
                    newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                         concurrentUpdateSessionInfo.getIdentity(),
                                         new org.uberfire.mvp.Command() {
                                             @Override
                                             public void execute() {
                                                 submit( path );
                                             }
                                         },
                                         new org.uberfire.mvp.Command() {
                                             @Override
                                             public void execute() {
                                                 //cancel?
                                             }
                                         },
                                         new org.uberfire.mvp.Command() {
                                             @Override
                                             public void execute() {
                                                 concurrentUpdateSessionInfo = null;
                                                 presenter.reload();
                                             }
                                         }
                                       ).show();
                } else {
                    submit( path );
                }
            }
        } );

        //Download widgets
        final Well downloadWell = new Well();
        final HorizontalPanel downloadContainer = new HorizontalPanel();
        downloadContainer.add( new Label( ScoreCardXLSEditorConstants.INSTANCE.DownloadCurrentVersion() + ":" ) );
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

    private void submit( final Path path ) {
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
        concurrentUpdateSessionInfo = null;
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        uploadWidget.setEnabled( !isReadOnly );
    }

    private Image getIcon() {
        Image image = new Image( ScoreCardXLSEditorResources.INSTANCE.images().scoreCardIconLarge() );
        image.setAltText( ScoreCardXLSEditorConstants.INSTANCE.ScoreCard() );
        return image;
    }

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
}
