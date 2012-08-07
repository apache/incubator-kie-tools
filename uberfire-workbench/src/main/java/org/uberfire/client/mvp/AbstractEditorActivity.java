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
package org.uberfire.client.mvp;

import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchMenuBar;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;

/**
 *
 */
public abstract class AbstractEditorActivity
        implements
        WorkbenchEditorActivity {

    @Inject
    private PlaceManager placeManager;

    @Override
    public Position getDefaultPosition() {
        return Position.ROOT;
    }

    @Override
    public boolean onMayStop() {
        return true;
    }

    @Override
    public void onStop() {
        //Do nothing.
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {
        //Do nothing.
    }

    @Override
    public void onRevealPlace(AcceptItem acceptPanel) {
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String simplePath = placeRequest.getParameter( "path",
                                                       null );

        String uri = placeRequest.getParameter( "path:uri",
                                                null );
        String name = placeRequest.getParameter( "path:name",
                                                 null );

        final Path path;
        if ( simplePath != null && (uri == null || name == null) ) {
            path = new PathImpl( simplePath );
        } else {
            path = new PathImpl( name,
                                 uri );
        }

        onStart( path );

        acceptPanel.add( getTitle(),
                         getWidget() );
        onReveal();
    }

    @Override
    public void onStart(final Path path) {
        //Do nothing.  
    }

    @Override
    public void onReveal() {
        //Do nothing.   
    }

    public abstract String getTitle();

    //TODO {manstis} This can be deleted once the static popup menu is removed
    public abstract String getIdentifier();

    public abstract IsWidget getWidget();

    @Override
    public void onSave() {
        //Do nothing.  
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void onLostFocus() {
        //Do nothing.  
    }

    @Override
    public void onFocus() {
        //Do nothing.   
    }

    @Override
    public WorkbenchMenuBar getMenuBar() {
        return new WorkbenchMenuBar();
    }

}
