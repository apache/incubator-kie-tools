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
package org.dashbuilder.client.widgets.dataset.editor.external;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

/**
 * <p>External Data Set specific attributes editor presenter.</p>
 * 
 */
@Dependent
public class ExternalDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.ExternalDataSetDefAttributesEditor {

    public interface View extends UberView<ExternalDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View urlView);
        
        
    }

    DataSetClientServices dataSetClientServices;
    ValueBoxEditor<String> url;
    public View view;
    ExternalDataSetDef value;
    boolean isUsingFilePath = true;

    @Inject
    public ExternalDataSetDefAttributesEditor(final DataSetClientServices dataSetClientServices,
                                         final ValueBoxEditor<String> url,
                                         final View view) {
        this.dataSetClientServices = dataSetClientServices;
        this.url = url;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(url.view);
        url.addHelpContent(DataSetEditorConstants.INSTANCE.external_URL(),
                DataSetEditorConstants.INSTANCE.external_URL_description(),
                Placement.BOTTOM);

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public ValueBoxEditor<String> url() {
        return url;
    }

   
    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final ExternalDataSetDef value) {
        this.value = value;
    }

    @Override
    public void setDelegate(final EditorDelegate<ExternalDataSetDef> delegate) {

    }
}
