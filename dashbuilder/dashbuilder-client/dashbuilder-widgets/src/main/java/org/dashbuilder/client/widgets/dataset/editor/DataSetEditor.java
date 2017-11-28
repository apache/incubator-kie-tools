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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.attributes.*;
import org.dashbuilder.client.widgets.dataset.event.ColumnsChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.FilterChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.DataSetDefEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.List;

import static org.dashbuilder.dataprovider.DataSetProviderType.*;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * <p>Data Set Editor presenter.</p>
 *
 * @since 0.4.0
 */
public abstract class DataSetEditor<T extends DataSetDef> implements IsWidget,
                                                                     DataSetDefEditor<T> {

    public static final String TAB_CONFIGURATION = "configuration";
    public static final String TAB_PREVIEW = "preview";
    public static final String TAB_ADVANCED = "advanced";

    public interface View extends UberView<DataSetEditor> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(DataSetDefBasicAttributesEditor.View basicAttributesEditorView,
                         IsWidget providerAttributesEditorView,
                         DataSetDefColumnsFilterEditor.View columnsAndFilterEditorView,
                         DataSetDefPreviewTable.View previewTableView,
                         DataSetDefCacheAttributesEditorView backendCacheAttributesEditorView,
                         DataSetDefCacheAttributesEditorView clientCacheAttributesEditorView,
                         DataSetDefRefreshAttributesEditor.View refreshEditorView);

        void setConfigurationTabTitle(String title);

        void showConfigurationTab();

        void addConfigurationTabItemClickHandler(final Command command);

        void showPreviewTab();

        void addPreviewTabItemClickHandler(final Command command);

        void showAdvancedTab();

        void addAdvancedTabItemClickHandler(final Command command);

        void openColumnsFilterPanel(String title);

        void closeColumnsFilterPanel(String title);

        void showErrorNotification(final SafeHtml text);

        void clearErrorNotification();
    }

    protected DataSetDefBasicAttributesEditor basicAttributesEditor;
    protected IsWidget providerAttributesEditorView;
    protected DataSetDefColumnsFilterEditor columnsAndFilterEditor;
    protected DataSetDefPreviewTable previewTable;
    protected DataSetDefBackendCacheAttributesEditor backendCacheAttributesEditor;
    protected DataSetDefClientCacheAttributesEditor clientCacheAttributesEditor;
    private DataSetDefRefreshAttributesEditor refreshEditor;
    protected DataSetClientServices clientServices;
    protected LoadingBox loadingBox;
    protected Event<ErrorEvent> errorEvent;
    protected Event<TabChangedEvent> tabChangedEvent;

    /* The Data Set Editor view. */
    public View view;
    protected DataSetDef dataSetDef;
    protected Command afterPreviewCommand;

    @Inject
    public DataSetEditor(final DataSetDefBasicAttributesEditor basicAttributesEditor,
                         final IsWidget providerAttributesEditorView,
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
        this.basicAttributesEditor = basicAttributesEditor;
        this.providerAttributesEditorView = providerAttributesEditorView;
        this.columnsAndFilterEditor = columnsAndFilterEditor;
        this.previewTable = previewTable;
        this.backendCacheAttributesEditor = backendCacheAttributesEditor;
        this.clientCacheAttributesEditor = clientCacheAttributesEditor;
        this.refreshEditor = refreshEditor;
        this.clientServices = clientServices;
        this.loadingBox = loadingBox;
        this.errorEvent = errorEvent;
        this.tabChangedEvent = tabChangedEvent;
        this.view = view;
    }

    public void init() {
        view.init(this);
        view.initWidgets(basicAttributesEditor.view,
                         providerAttributesEditorView,
                         columnsAndFilterEditor.view,
                         previewTable.view,
                         backendCacheAttributesEditor.view,
                         clientCacheAttributesEditor.view,
                         refreshEditor.view);
        view.addConfigurationTabItemClickHandler(configurationTabItemClickHandler);
        view.addPreviewTabItemClickHandler(previewTabItemClickHandler);
        view.addAdvancedTabItemClickHandler(advancedTabItemClickHandler);
        columnsAndFilterEditor.setMaxHeight("400px");
        backendCacheAttributesEditor.setRange(200d,
                                              10000d);
        clientCacheAttributesEditor.setRange(00d,
                                             4096d);
    }

    /*************************************************************
     ** PUBLIC EDITOR METHODS **
     *************************************************************/

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void showConfigurationTab() {
        afterPreviewCommand = new Command() {
            @Override
            public void execute() {
                view.showConfigurationTab();
            }
        };
    }

    public void showPreviewTab() {
        afterPreviewCommand = new Command() {
            @Override
            public void execute() {
                view.showPreviewTab();
            }
        };
    }

    public void showAdvancedTab() {
        afterPreviewCommand = new Command() {
            @Override
            public void execute() {
                view.showAdvancedTab();
            }
        };
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public void setAcceptableValues(final List<DataColumnDef> acceptableValues) {
        columnsAndFilterEditor.setAcceptableValues(acceptableValues);
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final T value) {
        this.dataSetDef = value;

        // Check specific provider type constraints.
        final DataSetProviderType type = value.getProvider() != null ? value.getProvider() : null;
        final String typeTitle = getTypeTitle(type);
        view.setConfigurationTabTitle(new StringBuffer(typeTitle).append(" ")
                                              .append(DataSetEditorConstants.INSTANCE.tab_configguration()).toString());

        final boolean isBean = type != null && DataSetProviderType.BEAN.equals(type);
        if (isBean) {
            // Bean data sets do not support backend cache, its used by its own nature...
            backendCacheAttributesEditor = null;
        }

        // Load the preview table and update filter editor when having the resulting data set.
        doPreview(true);
    }

    @Override
    public void setDelegate(final EditorDelegate<T> delegate) {
        // No delegation required.
    }

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefBasicAttributesEditor basicAttributesEditor() {
        return basicAttributesEditor;
    }

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefColumnsFilterEditor columnsAndFilterEditor() {
        return columnsAndFilterEditor;
    }

    @Override
    public DataSetDefBackendCacheAttributesEditor backendCacheEditor() {
        return backendCacheAttributesEditor;
    }

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefClientCacheAttributesEditor clientCacheEditor() {
        return clientCacheAttributesEditor;
    }

    @Override
    public org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor refreshEditor() {
        return refreshEditor;
    }

    /*************************************************************
     ** VIEW CALLBACK METHODS **
     *************************************************************/

    void onOpenColumnsFilterPanel() {
        view.openColumnsFilterPanel(DataSetEditorConstants.INSTANCE.hideColumnsAndFilter());
    }

    void onCloseColumnsFilterPanel() {
        view.closeColumnsFilterPanel(DataSetEditorConstants.INSTANCE.showColumnsAndFilter());
    }

    /*************************************************************
     ** PRIVATE EDITOR METHODS **
     *************************************************************/

    protected void doPreview(final boolean isUpdateFilter) {
        loadingBox.show();
        previewTable.show(dataSetDef,
                          dataSetDef.getColumns(),
                          new DataSetEditorListener(isUpdateFilter));
    }

    protected void afterPreview(final DataSet dataSet,
                                final boolean isUpdateFilter) {
        view.clearErrorNotification();
        if (isUpdateFilter) {
            columnsAndFilterEditor.dataSetFilter().init(dataSet.getMetadata());
        }
        if (afterPreviewCommand != null) {
            afterPreviewCommand.execute();
            afterPreviewCommand = null;
        }
        loadingBox.hide();
    }

    private class DataSetEditorListener extends AbstractDisplayerListener {

        private boolean isUpdateFilter;

        public DataSetEditorListener(final boolean isUpdateFilter) {
            this.isUpdateFilter = isUpdateFilter;
        }

        @Override
        public void onDraw(final Displayer displayer) {
            final DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
            afterPreview(dataSet,
                         isUpdateFilter);
        }

        @Override
        public void onRedraw(final Displayer displayer) {
            final DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
            afterPreview(dataSet,
                         isUpdateFilter);
        }

        @Override
        public void onClose(final Displayer displayer) {
            loadingBox.hide();
        }

        @Override
        public void onError(final Displayer displayer,
                            final ClientRuntimeError error) {
            showError(error);
        }
    }

    protected void showError(final ClientRuntimeError error) {
        loadingBox.hide();
        final String message = error.getCause() != null ? error.getCause() : error.getMessage();
        view.showErrorNotification(new SafeHtmlBuilder().appendEscaped(message).toSafeHtml());
        errorEvent.fire(new ErrorEvent(this,
                                       error));

        if (afterPreviewCommand != null) {
            this.afterPreviewCommand.execute();
        }
    }

    private String getTypeTitle(final DataSetProviderType type) {
        if (BEAN.equals(type)) {
            return DataSetEditorConstants.INSTANCE.bean();
        }
        if (CSV.equals(type)) {
            return DataSetEditorConstants.INSTANCE.csv();
        }
        if (SQL.equals(type)) {
            return DataSetEditorConstants.INSTANCE.sql();
        }
        if (ELASTICSEARCH.equals(type)) {
            return DataSetEditorConstants.INSTANCE.elasticSearch();
        }
        return "";
    }

    protected final Command configurationTabItemClickHandler = new Command() {
        @Override
        public void execute() {
            tabChangedEvent.fire(new TabChangedEvent(DataSetEditor.this,
                                                     TAB_CONFIGURATION));
        }
    };

    protected final Command previewTabItemClickHandler = new Command() {
        @Override
        public void execute() {
            tabChangedEvent.fire(new TabChangedEvent(DataSetEditor.this,
                                                     TAB_PREVIEW));
        }
    };

    protected final Command advancedTabItemClickHandler = new Command() {
        @Override
        public void execute() {
            tabChangedEvent.fire(new TabChangedEvent(DataSetEditor.this,
                                                     TAB_ADVANCED));
        }
    };

    void onColumnsChangedEvent(@Observes ColumnsChangedEvent columnsChangedEvent) {
        checkNotNull("columnsChangedEvent",
                     columnsChangedEvent);
        if (columnsChangedEvent.getContext().equals(columnsAndFilterEditor.columnListEditor().columns())) {
            final List<DataColumnDef> cols = columnsChangedEvent.getColumns();
            dataSetDef.setColumns(cols);
            doPreview(true);
        }
    }

    void onFilterChangedEvent(@Observes FilterChangedEvent filterChangedEvent) {
        checkNotNull("filterChangedEvent",
                     filterChangedEvent);
        if (filterChangedEvent.getContext().equals(columnsAndFilterEditor.dataSetFilter())) {
            final DataSetFilter f = filterChangedEvent.getFilter();
            dataSetDef.setDataSetFilter(f);
            // Do not update filter, as the component status has been already updated by the user interaction.
            doPreview(false);
        }
    }
}
