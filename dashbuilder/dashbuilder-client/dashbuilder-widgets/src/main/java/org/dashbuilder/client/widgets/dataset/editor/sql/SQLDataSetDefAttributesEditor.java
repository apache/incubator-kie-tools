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

import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.dashbuilder.dataset.service.SQLProviderServices;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>SQL Data Set specific attributes editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class SQLDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.SQLDataSetDefAttributesEditor {

    public interface View extends UberView<SQLDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(DropDownEditor.View dataSource, ValueBoxEditor.View dbSchema,
                         ValueBoxEditor.View dbTable, ValueBoxEditor.View dbSQL);

        /**
         * <p>Enables edition for sql attribute <code>dbTable</code></p>
         */
        void table();

        /**
         * <p>Enables edition for sql attribute <code>dbSQL</code></p>
         */
        void query();
    }

    Caller<SQLProviderServices> sqlProviderServices;
    DropDownEditor dataSource;
    ValueBoxEditor<String> dbSchema;
    ValueBoxEditor<String> dbTable;
    ValueBoxEditor<String> dbSQL;
    public View view;
    private boolean isQuery;

    @Inject
    public SQLDataSetDefAttributesEditor(final DropDownEditor dataSource,
                                         final ValueBoxEditor<String> dbSchema,
                                         final ValueBoxEditor<String> dbTable,
                                         final ValueBoxEditor<String> dbSQL,
                                         final Caller<SQLProviderServices> sqlProviderServices,
                                         final View view) {
        this.dataSource = dataSource;
        this.dbSchema = dbSchema;
        this.dbTable = dbTable;
        this.dbSQL = dbSQL;
        this.sqlProviderServices = sqlProviderServices;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        // Initialize the SQL specific attributes editor view.
        view.init(this);
        view.initWidgets(dataSource.view, dbSchema.view, dbTable.view, dbSQL.view);

        dataSource.setSelectHint(DataSetEditorConstants.INSTANCE.sql_datasource_selectHint());
        sqlProviderServices.call((List<SQLDataSourceDef> list) -> onSqlDataSourcesLoad(list)).getDataSourceDefs();

        dataSource.addHelpContent(DataSetEditorConstants.INSTANCE.sql_datasource(),
                DataSetEditorConstants.INSTANCE.sql_datasource_description(),
                Placement.RIGHT); //bottom placement would interfere with the dropdown
        dbSchema.addHelpContent(DataSetEditorConstants.INSTANCE.sql_schema(),
                DataSetEditorConstants.INSTANCE.sql_schema_description(),
                Placement.BOTTOM);
        dbTable.addHelpContent(DataSetEditorConstants.INSTANCE.sql_table(),
                DataSetEditorConstants.INSTANCE.sql_table_description(),
                Placement.BOTTOM);
        dbSQL.addHelpContent(DataSetEditorConstants.INSTANCE.sql_query(),
                DataSetEditorConstants.INSTANCE.sql_query_description(),
                Placement.BOTTOM);

        // Use query editor by default.
        onSelectQuery();
    }

    private DropDownEditor.Entry toDropDownEntry(SQLDataSourceDef d) {
        return dataSource.newEntry(d.getName(), d.getDescription());
    }

    private void onSqlDataSourcesLoad(List<SQLDataSourceDef> list) {
        List<DropDownEditor.Entry> entries = list.stream().map(this::toDropDownEntry).collect(Collectors.toList());
        dataSource.setEntries(entries);
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    @Override
    public DropDownEditor dataSource() {
        return dataSource;
    }

    @Override
    public ValueBoxEditor<String> dbSchema() {
        return dbSchema;
    }

    @Override
    public ValueBoxEditor<String> dbTable() {
        return dbTable;
    }

    @Override
    public ValueBoxEditor<String> dbSQL() {
        return dbSQL;
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final SQLDataSetDef value) {
        if (value != null && value.getDbTable() != null) {
            onSelectTable();
        } else {
            onSelectQuery();
        }
    }

    @Override
    public void setDelegate(final EditorDelegate<SQLDataSetDef> delegate) {
        // No delegation required.
    }

    public boolean isUsingQuery() {
        return this.isQuery;
    }

    void onSelectTable() {
        view.table();
        isQuery = false;
    }

    void onSelectQuery() {
        view.query();
        isQuery = true;
    }    
}
