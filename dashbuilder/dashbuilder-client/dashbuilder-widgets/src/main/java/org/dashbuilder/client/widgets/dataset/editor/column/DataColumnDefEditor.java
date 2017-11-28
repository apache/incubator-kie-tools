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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Data Column Definition editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class DataColumnDefEditor implements IsWidget, org.dashbuilder.dataset.client.editor.DataColumnDefEditor {

    public interface View extends UberView<DataColumnDefEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View idView, IsWidget columnTypeView);
                
    }

    ValueBoxEditor<String> id;
    ColumnTypeEditor columnType;
    public View view;

    boolean isEditMode = true;
    DataSetProviderType providerType;

    @Inject
    public DataColumnDefEditor(final ValueBoxEditor<String> id,
                               final ColumnTypeEditor columnType,
                               final View view) {
        this.id = id;
        this.columnType = columnType;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(id.view, columnType.asWidget());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void removeFromParent() {
        id.asWidget().removeFromParent();
    }

    @Override
    public void setProviderType(final DataSetProviderType type) {
        this.providerType = type;
        updateColumnTypeEditMode();
    }

    @Override
    public void setOriginalColumnType(final ColumnType columnType) {
        this.columnType.setOriginalColumnType(columnType);
    }

    @Override
    public void isEditMode(final boolean isEdit) {
        this.isEditMode = isEdit;
        updateColumnTypeEditMode();
    }


    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public ValueBoxEditor<String> id() {
        return id;
    }

    @Override
    public org.dashbuilder.dataset.client.editor.ColumnTypeEditor columnType() {
        return columnType;
    }
    
    private void updateColumnTypeEditMode() {
        final boolean isTypeEditable = !DataSetProviderType.BEAN.equals(providerType);
        columnType.isEditMode(isTypeEditable && isEditMode);

    }
}
