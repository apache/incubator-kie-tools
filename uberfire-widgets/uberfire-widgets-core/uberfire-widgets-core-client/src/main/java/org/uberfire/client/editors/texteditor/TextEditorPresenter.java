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

package org.uberfire.client.editors.texteditor;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnStartup;

public abstract class TextEditorPresenter {

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
    public View view;

    @Inject
    private Caller<VFSService> vfsServices;

    protected Path path;

    @OnStartup
    public void onStartup( final Path path ) {
        this.path = path;
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString(path);
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    public void onOpen() {
        view.setFocus();
    }

    public IsWidget getWidget() {
        return view;
    }

}