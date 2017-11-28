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
package org.dashbuilder.client.widgets.dataset.editor.sql;

import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefColumnsFilterEditor;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefPreviewTable;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBackendCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefClientCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.SQLDataSetDef;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>SQL Data Set editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class SQLDataSetEditor extends DataSetEditor<SQLDataSetDef> implements org.dashbuilder.dataset.client.editor.SQLDataSetDefEditor {

    SQLDataSetDefAttributesEditor attributesEditor;
    
    @Inject
    public SQLDataSetEditor(final DataSetDefBasicAttributesEditor basicAttributesEditor,
                            final SQLDataSetDefAttributesEditor attributesEditor,
                            final DataSetDefColumnsFilterEditor columnsAndFilterEditor,
                            final DataSetDefPreviewTable previewTable,
                            final DataSetDefBackendCacheAttributesEditor backendCacheAttributesEditor,
                            final DataSetDefClientCacheAttributesEditor clientCacheAttributesEditor,
                            final DataSetDefRefreshAttributesEditor refreshEditor,
                            final DataSetClientServices clientServices,
                            final LoadingBox loadingBox,
                            final Event<ErrorEvent> errorEvent,
                            final Event<TabChangedEvent> tabChangedEvent,
                            final View view) {
        super(basicAttributesEditor, attributesEditor.view, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor,
                refreshEditor, clientServices, loadingBox, errorEvent, tabChangedEvent, view);
        this.attributesEditor = attributesEditor;
    }

    @PostConstruct
    public void init() {
        // Initialize the generic data set editor view.
        super.init();
    }

    public SQLDataSetDefAttributesEditor getAttributesEditor() {
        return attributesEditor;
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public DropDownEditor dataSource() {
        return attributesEditor.dataSource();
    }

    @Override
    public ValueBoxEditor<String> dbSchema() {
        return attributesEditor.dbSchema();
    }

    @Override
    public ValueBoxEditor<String> dbTable() {
        return attributesEditor.dbTable();
    }

    @Override
    public ValueBoxEditor<String> dbSQL() {
        return attributesEditor.dbSQL();
    }

    @Override
    public boolean isUsingQuery() {
        return attributesEditor.isUsingQuery();
    }

    @Override
    public void setValue(SQLDataSetDef value) {
        super.setValue(value);
        // As gwt editor inheritance does not work fine, try to reuse SQL attributes editor logic here. 
        attributesEditor.setValue(value);
    }
}
