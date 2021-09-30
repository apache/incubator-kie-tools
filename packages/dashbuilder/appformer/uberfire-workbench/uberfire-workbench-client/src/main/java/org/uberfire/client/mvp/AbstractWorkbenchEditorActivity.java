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
package org.uberfire.client.mvp;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

/**
 * Implementation of behaviour common to all workbench editor activities. Concrete implementations are typically not
 * written by hand; rather, they are generated from classes annotated with {@link WorkbenchEditor}.
 */
public abstract class AbstractWorkbenchEditorActivity extends AbstractWorkbenchActivity implements WorkbenchEditorActivity {

    protected ObservablePath path;

    public AbstractWorkbenchEditorActivity(final PlaceManager placeManager) {
        super(placeManager);
    }

    /**
     * Overrides the default implementation by redirecting calls that are {@link PathPlaceRequest} instances to
     * {@link #onStartup(ObservablePath, PlaceRequest)}. Non-path place requests are handed up to the super impl.
     */
    @Override
    public final void onStartup(final PlaceRequest place) {
        final Path path = place.getPath();
        if (path != null) {
            if (path instanceof ObservablePath) {
                onStartup((ObservablePath) path,
                          place);
            } else if (this.isDynamic()) {
                onStartup(path,
                          place);
            }
        } else {
            super.onStartup(place);
        }
    }

    void onStartup(final Path path,
                   final PlaceRequest place) {

        onStartup(new PathPlaceRequest(path).getPath(),
                  place);
    }

    @Override
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.onStartup(place);
        this.path = path;

    }


    @Override
    public void onOpen() {
        super.onOpen();
    }

    @Override
    public void onSave() {
        //Do nothing.
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void onFocus() {
        super.onFocus();
    }


    /**
     * Returns the locking strategy for this editor activity, defaulting to
     * pessimistic. This method is overridden for generated activities returning
     * the strategy configured at {@link WorkbenchEditor}.
     */
    protected LockingStrategy getLockingStrategy() {
        return LockingStrategy.FRAMEWORK_PESSIMISTIC;
    }
}