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

package org.drools.guvnor.client.editors.texteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.backend.VFSService;
import org.drools.guvnor.client.annotations.IsDirty;
import org.drools.guvnor.client.annotations.OnClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnSave;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.vfs.Path;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

@Dependent
@WorkbenchEditor()
public class TextEditorPresenter {

    @Inject
    private PlaceManager placeManager;

    public interface View
        extends
        IsWidget {

        void setContent(String content);

        String getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @Inject
    public View                view;

    @Inject
    private Caller<VFSService> vfsServices;

    private Path               path;

    @OnStart
    public void onStart(final Path path) {
        this.path = path;
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback(Path response) {
                view.setDirty( false );
            }
        } ).write( path,
                   view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter( "path",
                                                          null );
        return "Text Editor [" + uriPath + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}