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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnStartup;

public abstract class TextEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final String content,
                         final AceEditorMode mode );

        String getContent();

        void setFocus();

        void setDirty( final boolean dirty );

        boolean isDirty();

        void setReadOnly( final boolean isReadOnly );
    }

    @Inject
    public View view;

    @Inject
    private Caller<VFSService> vfsServices;

    protected Path path;

    @OnStartup
    public void onStartup( final ObservablePath path ) {
        this.path = path;
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
                onAfterViewLoaded();
            }
        } ).readAllString( path );
    }

    /**
     * This is called after the view's content has been loaded. Sub-classes can override
     * this method to perform applicable actions after the view's content has been set.
     * The default implementation does nothing.
     */
    protected void onAfterViewLoaded() {
    }

    /**
     * This allows sub-classes to determine the Mode of the AceEditor.
     * By default the AceEditor assumes the AceEditorMode.TEXT.
     * @return
     */
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.TEXT;
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