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

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Base class for Editor Activities
 */
public abstract class AbstractWorkbenchEditorActivity extends AbstractWorkbenchActivity
        implements
        WorkbenchEditorActivity {

    public AbstractWorkbenchEditorActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void launch( final AcceptItem acceptPanel,
                        final PlaceRequest place,
                        final Command callback ) {
        super.launch( place, callback );

        final PathPlaceRequest pathPlace = (PathPlaceRequest) place;

        onStart( pathPlace.getPath(), place );

        acceptPanel.add( getTitleWidget(), getWidget() );

        onReveal();
    }

    @Override
    public void onStart( final Path path ) {
        //Do nothing.  
    }

    @Override
    public void onStart( final Path path,
                         final PlaceRequest place ) {
        //Do nothing.  
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
