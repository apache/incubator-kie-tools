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
package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.dataset.editor.column.DataSetDefColumnsEditor;
import org.dashbuilder.client.widgets.dataset.event.FilterChangedEvent;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * <p>Data Set columns and filter editor presenter.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefColumnsFilterEditor implements IsWidget,
                                                      org.dashbuilder.dataset.client.editor.DataSetDefColumnsFilterEditor {

    public interface View extends UberView<DataSetDefColumnsFilterEditor> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(IsWidget columnsEditorView,
                         DataSetDefFilterEditor.View dataSetFilterEditorView);

        void setMaxHeight(final String maxHeight);
    }

    DataSetDefColumnsEditor columnsEditor;
    DataSetDefFilterEditor dataSetFilterEditor;
    public View view;

    @Inject
    public DataSetDefColumnsFilterEditor(final DataSetDefColumnsEditor columnsEditor,
                                         final DataSetDefFilterEditor dataSetFilterEditor,
                                         final View view) {
        this.columnsEditor = columnsEditor;
        this.dataSetFilterEditor = dataSetFilterEditor;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(columnsEditor.asWidget(),
                         dataSetFilterEditor.view);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setMaxHeight(final String maxHeight) {
        view.setMaxHeight(maxHeight);
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefColumnsEditor columnListEditor() {
        return columnsEditor;
    }

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefFilterEditor dataSetFilter() {
        return dataSetFilterEditor;
    }

    @Override
    public void setAcceptableValues(final List<DataColumnDef> acceptableValues) {
        columnsEditor.setAcceptableValues(acceptableValues);
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final DataSetDef value) {
        if (value != null && value.getDataSetFilter() != null) {
            updateColumnsRestrictedByFilter(null,
                                            value.getDataSetFilter());
        }
    }

    @Override
    public void setDelegate(final EditorDelegate<DataSetDef> delegate) {

    }

    private void updateColumnsRestrictedByFilter(final DataSetFilter oldFilter,
                                                 final DataSetFilter f) {
        final List<String> oldFilterColumns = getFilterColumnIds(oldFilter);
        final List<String> newFilterColumns = getFilterColumnIds(f);

        // Check columns removed from filter.
        if (!oldFilterColumns.isEmpty()) {
            for (final String oldFilterColumn : oldFilterColumns) {
                final boolean isRemoved = !newFilterColumns.contains(oldFilterColumn);
                if (isRemoved) {
                    columnsEditor.onValueUnRestricted(oldFilterColumn);
                }
            }
        }

        // Check columns removed from filter.
        if (!newFilterColumns.isEmpty()) {
            for (final String newFilterColumn : newFilterColumns) {
                columnsEditor.onValueRestricted(newFilterColumn);
            }
        }
    }

    private List<String> getFilterColumnIds(final DataSetFilter filter) {
        final List<String> result = new ArrayList<String>();
        if (filter != null) {
            List<ColumnFilter> columnFilters = filter.getColumnFilterList();
            if (columnFilters != null && !columnFilters.isEmpty()) {
                for (final ColumnFilter cFilter : columnFilters) {
                    result.add(cFilter.getColumnId());
                }
            }
        }
        return result;
    }

    /**
     * Listen to filter changed event in order to restrict or enable again columns used in it.
     *
     * @param filterChangedEvent The event.
     */
    void onFilterChangedEvent(@Observes FilterChangedEvent filterChangedEvent) {
        checkNotNull("filterChangedEvent",
                     filterChangedEvent);
        if (filterChangedEvent.getContext().equals(dataSetFilterEditor)) {
            final DataSetFilter old = filterChangedEvent.getOldFilter();
            final DataSetFilter f = filterChangedEvent.getFilter();
            updateColumnsRestrictedByFilter(old,
                                            f);
        }
    }
}
