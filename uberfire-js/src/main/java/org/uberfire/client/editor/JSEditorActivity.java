/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.editor;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import javax.enterprise.inject.Alternative;

@Alternative
public class JSEditorActivity extends AbstractWorkbenchEditorActivity {

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    private final JSNativeEditor nativeEditor;

    public JSEditorActivity( final JSNativeEditor nativeEditor,
                             final PlaceManager placeManager ) {
        super( placeManager );
        this.nativeEditor = nativeEditor;
    }

    @Override
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.onStartup( path, place );
        setupObservablePathCallBacks();
        nativeEditor.onStartup( path.toURI() );
    }

    @Override
    public void onOpen() {
        super.onOpen();
        nativeEditor.onOpen( path.toURI() );
    }

    @Override
    public void onClose() {
        super.onClose();
        nativeEditor.onClose();
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        nativeEditor.onShutdown();
    }

    @Override
    public String getIdentifier() {
        return nativeEditor.getId();
    }

    private void setupObservablePathCallBacks() {
        path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( ObservablePath.OnConcurrentUpdateEvent parameter ) {
                nativeEditor.onConcurrentUpdate();
            }
        } );
        path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( ObservablePath.OnConcurrentDelete parameter ) {
                nativeEditor.onConcurrentDelete();
            }
        } );
        path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( ObservablePath.OnConcurrentRenameEvent parameter ) {
                nativeEditor.onConcurrentRename();
            }
        } );
        path.onConcurrentCopy( new ParameterizedCommand<ObservablePath.OnConcurrentCopyEvent>() {
            @Override
            public void execute( ObservablePath.OnConcurrentCopyEvent parameter ) {
                nativeEditor.onConcurrentCopy();
            }
        } );
        path.onRename(  new Command() {
            @Override
            public void execute() {
                nativeEditor.onRename();
            }
        });
        path.onDelete( new Command() {
            @Override
            public void execute() {
                nativeEditor.onDelete();
            }
        } );
        path.onUpdate( new Command() {
            @Override
            public void execute() {
                nativeEditor.onUpdate();
            }
        } );
        path.onCopy( new Command() {
            @Override
            public void execute() {
                nativeEditor.onCopy();
            }
        } );
    }

    @Override
    public String getTitle() {
        return nativeEditor.getTitle();
    }

    @Override
    public IsWidget getWidget() {
        return new HTML( nativeEditor.getElement().getInnerHTML() );
    }

    @Override
    public String getSignatureId() {
        return null;
    }

    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @Override
    public Collection<String> getTraits() {
        return TRAITS;
    }

    public JSNativeEditor getNativeEditor() {
        return nativeEditor;
    }
}
