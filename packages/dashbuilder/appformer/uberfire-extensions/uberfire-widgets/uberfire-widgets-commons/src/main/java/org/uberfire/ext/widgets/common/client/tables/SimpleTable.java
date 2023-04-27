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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
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
    private boolean persistPreferencesOnChange = true;

    public SimpleTable() {
        super();
    }

    public SimpleTable(final ProvidesKey<T> providesKey) {

        super(providesKey);
    }

    public void setPersistPreferencesOnChange(boolean persistPreferencesOnChange) {
        this.persistPreferencesOnChange = persistPreferencesOnChange;
    }

    public boolean isPersistingPreferencesOnChange() {
        return persistPreferencesOnChange;
    }

    protected void setupColumnPicker() {
        columnPicker = new ColumnPicker<T>(dataGrid);

        columnPicker.addColumnChangedHandler(new ColumnChangedHandler() {

            @Override
            public void beforeColumnChanged() {}

            @Override
            public void afterColumnChanged() {
                afterColumnChangedHandler();
            }
        });
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi(this);
    }

    protected ColumnPicker getColumnPicker() {
        return (ColumnPicker) columnPicker;
    }

    interface Binder
                     extends
                     UiBinder<Widget, SimpleTable> {

    }
}
