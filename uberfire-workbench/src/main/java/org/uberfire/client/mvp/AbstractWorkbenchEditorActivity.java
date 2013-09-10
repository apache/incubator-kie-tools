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

import java.util.HashMap;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.ContextUpdateEvent;

/**
 * Base class for Editor Activities
 */
public abstract class AbstractWorkbenchEditorActivity extends AbstractWorkbenchActivity
        implements
        WorkbenchEditorActivity {

    protected ObservablePath path;

    public AbstractWorkbenchEditorActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void launch( final AcceptItem acceptPanel,
                        final PlaceRequest place,
                        final Command callback ) {
        super.launch( place, callback );

        final PathPlaceRequest pathPlace = (PathPlaceRequest) place;

        onStartup( pathPlace.getPath(), place );

        acceptPanel.add( new UIPart( getTitle(), getTitleDecoration(), getWidget() ) );

        onOpen();
    }

    protected void fireContextUpdateEvent() {
        if ( lastContextUpdate == null ) {
            contextUpdateEvent.fire( new ContextUpdateEvent( wstatecontext.getActivePanel(), new HashMap<String, Object>( 2 ) {{
                put( "path", path );
                put( "place", place );
            }} ) );
            return;
        }
        if ( !path.equals( lastContextUpdate.getData().get( "path" ) )
                && !place.equals( lastContextUpdate.getData().get( "place" ) ) ) {
            contextUpdateEvent.fire( new ContextUpdateEvent( wstatecontext.getActivePanel(), new HashMap<String, Object>( 2 ) {{
                put( "path", path );
                put( "place", place );
            }} ) );
        }
    }

    @Override
    public void onStartup( final ObservablePath path ) {
        this.path = path;
    }

    @Override
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        this.path = path;
    }

    @Override
    public void onSave() {
        //Do nothing.  
    }

    @Override
    public boolean isDirty() {
        return false;
    }

}
