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
package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Data Set basic attributes (uuid, name) editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class DataSetDefBasicAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.DataSetDefBasicAttributesEditor {

    public interface View extends UberView<DataSetDefBasicAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View uuidEditor, ValueBoxEditor.View nameEditor);
    }
    
    ValueBoxEditor<String> uuidEditor;
    ValueBoxEditor<String> nameEditor;
    public View view;

    @Inject
    public DataSetDefBasicAttributesEditor(final ValueBoxEditor<String> uuidEditor,
                                           final ValueBoxEditor<String> nameEditor,
                                           final View view) {
        this.uuidEditor = uuidEditor;
        this.nameEditor = nameEditor;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(uuidEditor.view, nameEditor.view);
        uuidEditor.addHelpContent(DataSetEditorConstants.INSTANCE.attributeUUID(),
                DataSetEditorConstants.INSTANCE.attributeUUID_description(),
                Placement.BOTTOM);
        nameEditor.addHelpContent(DataSetEditorConstants.INSTANCE.attributeName(),
                DataSetEditorConstants.INSTANCE.attributeName_description(),
                Placement.BOTTOM);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    
    /*************************************************************
            ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public ValueBoxEditor<String> UUID() {
        return uuidEditor;
    }

    @Override
    public ValueBoxEditor<String> name() {
        return nameEditor;
    }
    
}
