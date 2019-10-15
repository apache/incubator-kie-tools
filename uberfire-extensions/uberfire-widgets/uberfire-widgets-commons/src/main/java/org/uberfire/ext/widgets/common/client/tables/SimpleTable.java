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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.table.client.ColumnChangedHandler;
import org.uberfire.ext.widgets.table.client.UberfireSimpleTable;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 * User preferences are persisted. If you need a client only version
 * of this widget take a look at UberfireSimpleTable.
 */
public class SimpleTable<T>
        extends UberfireSimpleTable<T> {

    private static Binder uiBinder = GWT.create(Binder.class);
    private GridPreferencesStore gridPreferencesStore;
    private boolean persistPreferencesOnChange = true;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    public SimpleTable() {
        super();
    }

    public SimpleTable(final ProvidesKey<T> providesKey) {
        super(providesKey);
    }

    public SimpleTable(final ProvidesKey<T> providesKey,
                       final GridGlobalPreferences gridGlobalPreferences) {

        super(providesKey);
        if (gridGlobalPreferences != null) {
            this.gridPreferencesStore = new GridPreferencesStore(gridGlobalPreferences);
        }
    }

    public void setPersistPreferencesOnChange(boolean persistPreferencesOnChange) {
        this.persistPreferencesOnChange = persistPreferencesOnChange;
    }

    public boolean isPersistingPreferencesOnChange() {
        return persistPreferencesOnChange;
    }

    protected void setupColumnPicker() {
        columnPicker = new ColumnPicker<T>(dataGrid,
                                           gridPreferencesStore);

        columnPicker.addColumnChangedHandler(new ColumnChangedHandler() {

            @Override
            public void beforeColumnChanged() {
            }

            @Override
            public void afterColumnChanged() {
                afterColumnChangedHandler();
            }
        });
    }

    protected void afterColumnChangedHandler() {
        if (gridPreferencesStore != null && preferencesService != null) {
            List<GridColumnPreference> columnsState = getColumnPicker().getColumnsState();
            gridPreferencesStore.resetGridColumnPreferences();
            for (GridColumnPreference gcp : columnsState) {
                gridPreferencesStore.addGridColumnPreference(gcp);
            }
            if (isPersistingPreferencesOnChange()) {
                saveGridPreferences();
            }
        }
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi(this);
    }

    public void setPreferencesService(final Caller<UserPreferencesService> preferencesService) {
        this.preferencesService = preferencesService;
    }

    public GridPreferencesStore getGridPreferencesStore() {
        return this.gridPreferencesStore;
    }

    public void setGridPreferencesStore(final GridPreferencesStore gridPreferences) {
        // I need to update my local copy of the preferences 
        //   if I would like to compare with the current state for changes
        this.gridPreferencesStore = gridPreferences;
        getColumnPicker().setGridPreferencesStore(gridPreferences);
    }

    public void saveGridPreferences() {
        if (gridPreferencesStore != null) {
            gridPreferencesStore.setPreferenceKey(gridPreferencesStore.getGlobalPreferences().getKey());
            saveGridToUserPreferences();
        }
    }

    public void saveGridToUserPreferences(){
        if (preferencesService!=null && gridPreferencesStore != null) {
            gridPreferencesStore.setType(UserPreferencesType.GRIDPREFERENCES);
            preferencesService.call(response -> {
            }).saveUserPreferences(gridPreferencesStore);
        }
    }

    public void storeColumnToPreferences() {
        List<GridColumnPreference> columnsState = getColumnPicker().getColumnsState();
        gridPreferencesStore.resetGridColumnPreferences();
        for (GridColumnPreference gcp : columnsState) {
            gridPreferencesStore.addGridColumnPreference(gcp);
        }
        saveGridPreferences();
    }

    protected ColumnPicker getColumnPicker() {
        return (ColumnPicker) columnPicker;
    }

    interface Binder
            extends
            UiBinder<Widget, SimpleTable> {

    }
}
