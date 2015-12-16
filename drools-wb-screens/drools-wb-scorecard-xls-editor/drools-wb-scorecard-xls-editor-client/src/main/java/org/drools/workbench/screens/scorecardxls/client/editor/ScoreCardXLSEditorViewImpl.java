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

package org.drools.workbench.screens.scorecardxls.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

public class ScoreCardXLSEditorViewImpl
        extends KieEditorViewImpl
        implements ScoreCardXLSEditorView {

    interface ScoreCardXLSEditorViewBinder
            extends
            UiBinder<Widget, ScoreCardXLSEditorViewImpl> {

    }

    private static ScoreCardXLSEditorViewBinder uiBinder = GWT.create( ScoreCardXLSEditorViewBinder.class );

    @UiField(provided = true)
    AttachmentFileWidget uploadWidget;

    @UiField
    Button downloadButton;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private ScoreCardXLSResourceType resourceType;

    private ScoreCardXLSEditorView.Presenter presenter;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo;

    @PostConstruct
    public void init() {
        uploadWidget = new AttachmentFileWidget( new String[]{ resourceType.getSuffix() }, true );
        initWidget( uiBinder.createAndBindUi( this ) );
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

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

}
