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

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

/**
 * Implementation of behaviour common to all workbench editor activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchEditor}.
 */
public abstract class AbstractWorkbenchEditorActivity extends AbstractWorkbenchActivity implements WorkbenchEditorActivity {

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

        acceptPanel.add( new UIPart( getTitle(), getTitleDecoration(), getWidget() ) );
    }

    /**
     * Overrides the default implementation by redirecting calls that are {@link PathPlaceRequest} instances to
     * {@link #onStartup(ObservablePath, PlaceRequest)}. Non-path place requests are handed up to the super impl.
     */
    @Override
    public final void onStartup( PlaceRequest place ) {
        if ( place instanceof PathPlaceRequest ) {
            onStartup( ((PathPlaceRequest) place).getPath(), place );
        } else {
            super.onStartup( place );
        }
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
