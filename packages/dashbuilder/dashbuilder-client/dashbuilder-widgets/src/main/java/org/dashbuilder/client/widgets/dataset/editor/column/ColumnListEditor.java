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
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.dataset.editor.driver.DataColumnDefDriver;
import org.dashbuilder.client.widgets.dataset.event.ColumnsChangedEvent;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.editor.DataColumnDefEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Data Set column list editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class ColumnListEditor implements IsWidget, org.dashbuilder.dataset.client.editor.ColumnListEditor {

    public interface View extends UberView<ColumnListEditor> {
        
        View insert(int index, org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor.View columnEditorView,
                    boolean selected, boolean enabled, String altText);
        View remove(int index);
        View clear();
    }

    SyncBeanManager beanManager;
    DataColumnDefDriver dataColumnDefDriver;
    Event<ColumnsChangedEvent> columnsChangedEvent;
    public View view;

    ListEditor<DataColumnDef, DataColumnDefEditor> listEditor;
    List<DataColumnDef> acceptableColumns;
    final List<String> restrictedColumns = new LinkedList<String>();
    DataSetProviderType providerType;

    @Inject
    public ColumnListEditor(final SyncBeanManager beanManager,
                            final DataColumnDefDriver dataColumnDefDriver,
                            final Event<ColumnsChangedEvent> columnsChangedEvent,
                            final View view) {
        this.beanManager = beanManager;
        this.dataColumnDefDriver = dataColumnDefDriver;
        this.columnsChangedEvent = columnsChangedEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        listEditor = ListEditor.of(createDataColumnDefEditorSource());
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /**
     * Set all data set available columns (not only the current used ones)
     * 
     * @param acceptableValues Acceptable values for the editor.
     */
    @Override
    public void setAcceptableValues(final List<DataColumnDef> acceptableValues) {

        clear();

        // Register column editor for each available column of the data set.
        this.acceptableColumns = new LinkedList<DataColumnDef>(acceptableValues);
        int index = 0;
        for (final DataColumnDef columnDef : this.acceptableColumns ) {
            org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor editor = createDummyColumnEditor(columnDef);
            view.insert(index, editor.view, false, true, null);
            index++;
        }
    }

    /**
     * Set the column that cannot be removed from the list, as it's used by the filter.
     * @param value The column id.
     */
    @Override
    public void onValueRestricted(final String value) {
        this.restrictedColumns.add(value);
        setEditorEnabled(value, false, DataSetEditorConstants.INSTANCE.columnIsUsedInFilter());
    }

    /**
     * Set the column that can be removed again from the list, as it's no longer used by the filter.
     * @param value The column id.
     */
    @Override
    public void onValueUnRestricted(final String value) {
        this.restrictedColumns.remove(value);
        
        // Check single column used in data set -> it cannot be unselected.
        if (listEditor.getList().size() == 1) {
            setEditorEnabled(0 ,false, DataSetEditorConstants.INSTANCE.dataSetMustHaveAtLeastOneColumn());
        } else {
            setEditorEnabled(value, true, null);
        }
    }

    @Override
    public void setProviderType(final DataSetProviderType type) {
        this.providerType = type;
    }


    public void clear() {
        acceptableColumns = null;
        providerType = null;
        view.clear();
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public DataColumnDefEditor createEditorForTraversal() {
        return listEditor.createEditorForTraversal();
    }

    @Override
    public String getPathElement(final DataColumnDefEditor subEditor) {
        return listEditor.getPathElement(subEditor);
    }

    @Override
    public void setEditorChain(final EditorChain<DataColumnDef, DataColumnDefEditor> chain) {
        listEditor.setEditorChain(chain);
    }

    @Override
    public void setDelegate(final EditorDelegate<List<DataColumnDef>> delegate) {
        listEditor.setDelegate(delegate);
    }

    @Override
    public void showErrors(final List<EditorError> errors) {

    }

    @Override
    public void flush() {
        listEditor.flush();
    }

    @Override
    public void onPropertyChange(final String... paths) {
        listEditor.onPropertyChange(paths);
    }

    @Override
    public void setValue(final List<DataColumnDef> value) {
        listEditor.setValue(value);
    }

    /*************************************************************
     ** VIEW CALLBACK METHODS **
     *************************************************************/
    
    void onColumnSelect(final int index, final boolean selected) {
        final DataColumnDef columnDef = acceptableColumns.get(index);
        if (selected) {
            listEditor.getList().add(columnDef.clone());
        } else {
            listEditor.getList().remove(columnDef);
        }
        columnsChangedEvent.fire(new ColumnsChangedEvent(this, listEditor.getList()));
    }
    
    /*************************************************************
     ** PRIVATE EDITOR METHODS **
     *************************************************************/

    private void replace(final int index, 
                         final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor columnEditor,
                         final boolean selected, final boolean enabled, final String altText) {
        view.remove(index);
        columnEditor.isEditMode(selected && enabled);
        view.insert(index, columnEditor.view, selected, enabled, altText);
    }

    private int getAvailableColumnIndex(final String id) {
        int x = 0;
        for (final DataColumnDef column : acceptableColumns) {
            if (column.getId().equals(id)) return x;
            x++;
        }
        return -1;
    }

    DataColumnDefEditorSource createDataColumnDefEditorSource() {
        return new DataColumnDefEditorSource();
    }

    class DataColumnDefEditorSource extends EditorSource<DataColumnDefEditor> {

        @Override
        public DataColumnDefEditor create(final int index) {
            final DataColumnDef column = listEditor.getList().get(index);
            final int localIndex = acceptableColumns.indexOf(column);

            // Check single column used in data set -> it cannot be unselected.
            if (!checkSingleColumnEditorDisabled()) {
                // Enable column selection if more than one column remains on the data set.
                checkMultipleColumnsEditorEnabled();
            }
            
            // Create the new editor.
            final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor editor = createColumnEditor();
            final boolean hasSingleColumn = listEditor.getList().size() == 1;
            final boolean isRestricted = restrictedColumns.contains(column.getId());
            final String tooltipText = hasSingleColumn ? DataSetEditorConstants.INSTANCE.dataSetMustHaveAtLeastOneColumn() 
                    : ( isRestricted ? DataSetEditorConstants.INSTANCE.columnIsUsedInFilter() : null );
            doSetOriginalColumnType(column.getId(), editor);
            replace(localIndex, editor, true, !isRestricted && !hasSingleColumn, tooltipText);
            return editor;
        }
        
        

        @Override
        public void dispose(DataColumnDefEditor subEditor) {
            
            // Column to be removed.
            final String columnId = subEditor.id().getValue();
            final int localIndex = getAvailableColumnIndex(columnId);

            if (localIndex > -1) {
                final DataColumnDef column = acceptableColumns.get(localIndex);

                // Dispose and remove sub-editor.
                super.dispose(subEditor);
                subEditor.removeFromParent();

                // Create a new dummy editor for the available column.
                final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor editor = createDummyColumnEditor(column);
                replace(localIndex, editor, false, true, null);

                // Disable column selection if only one column remains on the data set.
                checkSingleColumnEditorDisabled();
            }
        }
    };

    /**
     * Checks that if only single column used in data set -> it cannot be unselected. 
     */
    private boolean checkSingleColumnEditorDisabled() {
        final int size = listEditor.getList().size();
        final boolean hasEditors = !listEditor.getEditors().isEmpty();
        if (size == 1 && hasEditors) {
            setEditorEnabled(0 ,false, DataSetEditorConstants.INSTANCE.dataSetMustHaveAtLeastOneColumn());
            return true;
        }
        return false;
    }

    /**
     * Checks that if multiple columns are used in data set -> the column editors must be enabed, if the columns are not are restricted. 
     */
    private boolean checkMultipleColumnsEditorEnabled() {
        final int size = listEditor.getList().size();
        if (size == 2 && !listEditor.getEditors().isEmpty()) {
            final String cId = listEditor.getEditors().get(0).id().getValue();
            if (!restrictedColumns.contains(cId)) {
                setEditorEnabled(0, true, null);
            }
            return true;
        }
        return false;
    }

    private void setEditorEnabled(final String columnId, final boolean enabled, final String altText) {
        final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor e = getEditor(columnId);
        if (e != null) {
            setEditorEnabled(e, enabled, altText);
        }
    }
    
    private void setEditorEnabled(final int index, final boolean enabled, final String altText) {
        final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor _e = (org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor) listEditor.getEditors().get(index);
        if (_e != null) {
            setEditorEnabled(_e, enabled, altText);
        }
    }

    private void setEditorEnabled(org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor _e, final boolean enabled, final String altText) {
        final String cId = _e.id().getValue();
        final int _index = getAvailableColumnIndex(cId);
        if (_index > -1) {
            replace(_index, _e, true, enabled, altText);
        }
    }
    
    private void doSetOriginalColumnType(final String cId, final DataColumnDefEditor editor) {
        final int _index = getAvailableColumnIndex(cId);
        final DataColumnDef originalCol = acceptableColumns.get(_index);
        if (originalCol != null) {
            editor.setOriginalColumnType(originalCol.getColumnType());
        }
    }
    
    private org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor getEditor(final String columnId) {
        List<DataColumnDefEditor> editors = listEditor.getEditors();
        if (editors != null && !editors.isEmpty()) {
            for (final DataColumnDefEditor editor : editors) {
                final String cId = editor.id().getValue();
                if (columnId.equals(cId)) return (org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor) editor;
            }
        }
        return null;
    }
    
    
    private org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor createColumnEditor() {
        org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor e = beanManager.lookupBean(org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor.class).newInstance();
        e.setProviderType(providerType);
        return e;
    }

    private org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor createDummyColumnEditor(final DataColumnDef def) {
        final org.dashbuilder.client.widgets.dataset.editor.column.DataColumnDefEditor editor = createColumnEditor();
        // Column is available but not selected, do not allow edition.
        editor.isEditMode(false);
        
        // Initialze edtiro with the column attributes.
        dataColumnDefDriver.initialize(editor);
        dataColumnDefDriver.edit(def);
        
        doSetOriginalColumnType(def.getId(), editor);

        return editor;
    }

}
