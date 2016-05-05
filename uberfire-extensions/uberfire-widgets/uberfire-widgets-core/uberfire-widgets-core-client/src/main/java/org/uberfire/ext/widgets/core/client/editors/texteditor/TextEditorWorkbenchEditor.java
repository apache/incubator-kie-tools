/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.core.client.editors.texteditor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchEditor(identifier = "TextEditor", supportedTypes = { TextResourceType.class, DotResourceType.class })
public class TextEditorWorkbenchEditor
        extends TextEditorPresenter {

    @Inject
    private Caller<VFSService> vfsServices;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @OnStartup
    public void onStartup( final Path path,
                           final PlaceRequest placeRequest ) {
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null ) {
                    view.setContent( CoreConstants.INSTANCE.EmptyEntry(),
                                     getAceEditorMode() );
                } else {
                    view.setContent( response,
                                     getAceEditorMode() );
                }
                changeTitleWidgetEvent.fire(
                        new ChangeTitleWidgetEvent(
                                placeRequest,
                                CoreConstants.INSTANCE.TextEditor() + " [" + path.getFileName() + "]" ) );

            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setDirty( false );
            }
        } ).write( path, view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return super.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.TextEditor();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

}
