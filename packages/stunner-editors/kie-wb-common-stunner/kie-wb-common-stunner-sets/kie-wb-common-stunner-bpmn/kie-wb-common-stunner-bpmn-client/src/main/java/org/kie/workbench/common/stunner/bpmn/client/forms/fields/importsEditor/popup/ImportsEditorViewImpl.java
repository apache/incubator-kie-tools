/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup;

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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport.DefaultImportsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.wsdlImport.WSDLImportsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class ImportsEditorViewImpl extends BaseModal implements ImportsEditorView {

    @Inject
    protected DefaultImportsEditorWidget defaultImportsEditorWidget;

    @Inject
    protected WSDLImportsEditorWidget wsdlImportsEditorWidget;

    private Presenter presenter;

    private Button btnOk;

    private Button btnCancel;

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column(ColumnSize.MD_12);

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        container.setFluid(true);
        container.add(row);
        row.add(column);
        setTitle(StunnerFormsClientFieldsConstants.CONSTANTS.Imports());

        column.add(defaultImportsEditorWidget.getWidget());
        column.add(wsdlImportsEditorWidget.getWidget());

        final Row btnRow = new Row();
        btnRow.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        final Column btnColumn = new Column(ColumnSize.MD_12);
        btnRow.add(btnColumn);

        btnOk = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Ok());
        btnOk.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        btnOk.addClickHandler(clickEvent -> {
            presenter.ok();
            defaultImportsEditorWidget.addDataTypes(presenter.getImports());
        }
        );

        btnColumn.add(btnOk);

        btnCancel = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Cancel());
        btnCancel.setPull(Pull.RIGHT);
        btnCancel.addClickHandler(clickEvent -> presenter.cancel());
        btnColumn.add(btnCancel);

        container.add(btnRow);
        setBody(container);
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
    public List<DefaultImport> getDefaultImports() {
        return defaultImportsEditorWidget.getData();
    }

    @Override
    public void setDefaultImports(List<DefaultImport> defaultImports) {
        defaultImportsEditorWidget.setData(defaultImports);
    }

    @Override
    public List<WSDLImport> getWSDLImports() {
        return wsdlImportsEditorWidget.getData();
    }

    @Override
    public void setWSDLImports(List<WSDLImport> wsdlImports) {
        wsdlImportsEditorWidget.setData(wsdlImports);
    }
}
