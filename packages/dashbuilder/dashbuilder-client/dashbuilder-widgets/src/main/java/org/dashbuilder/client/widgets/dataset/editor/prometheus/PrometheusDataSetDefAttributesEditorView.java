/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;

/**
 * <p>The Prometheus Data Set attributes editor view.</p>
 *
 */
@Dependent
public class PrometheusDataSetDefAttributesEditorView extends Composite implements PrometheusDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, PrometheusDataSetDefAttributesEditorView> {

        Binder BINDER = GWT.create(Binder.class);
    }

    PrometheusDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View serverUrlView;

    @UiField(provided = true)
    ValueBoxEditor.View queryView;

    @Override
    public void init(final PrometheusDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final ValueBoxEditor.View serverUrlView,
                            final ValueBoxEditor.View queryView) {
        this.serverUrlView = serverUrlView;
        this.queryView = queryView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
}
