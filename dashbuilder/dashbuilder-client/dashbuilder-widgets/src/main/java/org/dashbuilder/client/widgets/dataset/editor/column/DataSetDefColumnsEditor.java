/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Data Set columns editor.</p>
 * <p>It's attached in the gwt editors chain to handle the <code>allColumns</code> flag.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class DataSetDefColumnsEditor implements IsWidget, org.dashbuilder.dataset.client.editor.DataSetDefColumnsEditor  {

    ColumnListEditor columnListEditor;
    List<DataColumnDef> acceptableValues;

    @Inject
    public DataSetDefColumnsEditor(final ColumnListEditor columnListEditor) {
        this.columnListEditor = columnListEditor;
    }

    @Override
    public Widget asWidget() {
        return columnListEditor.asWidget();
    }

    @Override
    public ColumnListEditor columns() {
        return columnListEditor;
    }

    @Override
    public void setAcceptableValues(final List<DataColumnDef> acceptableValues) {
        this.acceptableValues = acceptableValues;
        columnListEditor.setAcceptableValues(acceptableValues);
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final DataSetDef value) {
        checkAvailableColumns();

        /*
            This editor and sub-editors do not take care about 'allColumnsEnable' flag. 
            If all column flag is enabled, just add all the available column into the data set definition. 
         */
        if (value != null && value.isAllColumnsEnabled()) {
            final List<DataColumnDef> columns = new ArrayList<DataColumnDef>(acceptableValues.size());
            for (final DataColumnDef columnDef : acceptableValues) {
                columns.add(columnDef.clone());
            }
            value.setColumns(columns);
            value.setAllColumnsEnabled(false);
        }

        // Columns edition depends on the data set provider type.
        columnListEditor.setProviderType( value != null ? value.getProvider() : null);
    }

    @Override
    public void setDelegate(final EditorDelegate<DataSetDef> delegate) {

    }
    
    private void checkAvailableColumns() {
        if (acceptableValues == null) {
            throw new IllegalArgumentException("Must call setAcceptableValues() before setting the data set definition columns to edit.");
        }
    }

    @Override
    public void onValueRestricted(final String value) {
        columnListEditor.onValueRestricted(value);
    }

    @Override
    public void onValueUnRestricted(final String value) {
        columnListEditor.onValueUnRestricted(value);
    }
}
