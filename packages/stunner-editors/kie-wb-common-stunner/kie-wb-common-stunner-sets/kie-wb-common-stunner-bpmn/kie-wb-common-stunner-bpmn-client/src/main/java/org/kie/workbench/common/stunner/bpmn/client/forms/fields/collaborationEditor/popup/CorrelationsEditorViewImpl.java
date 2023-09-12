/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorValidationItem;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorValidator;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class CorrelationsEditorViewImpl extends BaseModal implements CorrelationsEditorView {

    @Inject
    protected CorrelationsEditorWidget correlationsEditorWidget;

    private Presenter presenter;

    private Button btnOk;

    private Button btnCancel;

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column(ColumnSize.LG_12);

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        container.setFluid(true);
        container.add(row);
        row.add(column);
        setTitle(StunnerFormsClientFieldsConstants.CONSTANTS.Correlations_Title());

        column.add(correlationsEditorWidget.getWidget());

        final Row btnRow = new Row();
        btnRow.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        final Column btnColumn = new Column(ColumnSize.MD_12);
        btnRow.add(btnColumn);

        btnOk = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Ok());
        btnOk.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        btnOk.addClickHandler(clickEvent -> presenter.ok());
        btnColumn.add(btnOk);

        btnCancel = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Cancel());
        btnCancel.setPull(Pull.RIGHT);
        btnCancel.addClickHandler(clickEvent -> presenter.cancel());
        btnColumn.add(btnCancel);

        container.add(btnRow);
        setWidth("1200px");
        setBody(container);

        correlationsEditorWidget.addValueChangeHandler(valueChangeEvent -> presenter.update());
    }

    @Override
    public void hideView() {
        super.hide();
    }

    @Override
    public void showView() {
        super.show();
    }

    @Override
    public void updateView(final List<CorrelationsEditorValidationItem> validationItems) {
        this.btnOk.setEnabled(!CorrelationsEditorValidator.hasInvalidCorrelation(validationItems));
        correlationsEditorWidget.update(validationItems);
    }

    @Override
    public List<Correlation> getCorrelations() {
        return correlationsEditorWidget.getCorrelations();
    }

    @Override
    public void setCorrelations(List<Correlation> correlations) {
        correlationsEditorWidget.setCorrelations(correlations);
    }
}