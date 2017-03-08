/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.base;

import javax.enterprise.event.Observes;

import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralPreSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 * Class that all preference forms (defined by a {@link WorkbenchScreen} annotated with {@link PreferenceForm})
 * must inherit, to be able to be initialized in a proper manner, which is receiving its preference object.
 * @param <T> Preference bean type the can be edited by this form.
 */
public abstract class BasePreferenceForm<T> {

    private String id;

    private T preference;

    /**
     * Method called when the form is opened, so it can receive the current persisted preference value.
     * @param preference
     */
    public abstract void init(T preference);

    /**
     * Method called just before the preferences saving.
     */
    public abstract void beforeSave();

    /**
     * Method called when the "Undo" action is fired by the user. It is expected that the form undo any
     * unsaved changes in this method implementation.
     */
    public abstract void onUndo();

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        id = placeRequest.getParameter("id",
                                       null);
    }

    public void hierarchyItemFormInitializationEvent(@Observes HierarchyItemFormInitializationEvent event) {
        if (preference == null && event.getItemId().equals(id)) {
            preference = event.getPreference();
            init(preference);
        }
    }

    public void preSaveEvent(@Observes PreferencesCentralPreSaveEvent event) {
        beforeSave();
    }

    public void undoChangesEvent(@Observes PreferencesCentralUndoChangesEvent event) {
        onUndo();
    }

    public T getPreference() {
        return preference;
    }
}
