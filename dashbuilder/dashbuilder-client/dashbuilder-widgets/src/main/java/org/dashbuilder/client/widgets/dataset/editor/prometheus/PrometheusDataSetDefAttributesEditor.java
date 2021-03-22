/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.editor.prometheus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.PrometheusDataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

/**
 * <p>Prometheus Data Set specific attributes editor presenter.</p>
 * 
 */
@Dependent
public class PrometheusDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.PrometheusDataSetDefAttributesEditor {

    public interface View extends UberView<PrometheusDataSetDefAttributesEditor> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View serverUrlView, ValueBoxEditor.View queryView);

    }

    DataSetClientServices dataSetClientServices;
    ValueBoxEditor<String> serverUrl;
    ValueBoxEditor<String> query;

    public View view;
    PrometheusDataSetDef value;

    @Inject
    public PrometheusDataSetDefAttributesEditor(final DataSetClientServices dataSetClientServices,
                                                final ValueBoxEditor<String> serverUrl,
                                                final ValueBoxEditor<String> query,
                                                final View view) {
        this.dataSetClientServices = dataSetClientServices;
        this.serverUrl = serverUrl;
        this.query = query;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        // Initialize the Bean specific attributes editor view.
        view.init(this);
        view.initWidgets(serverUrl.view, query.view);
        serverUrl.addHelpContent(DataSetEditorConstants.INSTANCE.prometheus_server_url(),
                                 DataSetEditorConstants.INSTANCE.prometheus_server_url_description(),
                                 Placement.BOTTOM);
        query.addHelpContent(DataSetEditorConstants.INSTANCE.prometheus_query(),
                             DataSetEditorConstants.INSTANCE.prometheus_query_description(),
                             Placement.BOTTOM);

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public ValueBoxEditor<String> serverUrl() {
        return this.serverUrl;
    }

    @Override
    public ValueBoxEditor<String> query() {
        return this.query;
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final PrometheusDataSetDef value) {
        this.value = value;
    }

    @Override
    public void setDelegate(final EditorDelegate<PrometheusDataSetDef> delegate) {

    }
}