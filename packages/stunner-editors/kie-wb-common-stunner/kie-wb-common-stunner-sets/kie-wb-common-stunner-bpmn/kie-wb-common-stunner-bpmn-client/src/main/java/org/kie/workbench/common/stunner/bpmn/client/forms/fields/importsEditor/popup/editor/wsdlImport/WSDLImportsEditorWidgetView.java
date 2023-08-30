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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.wsdlImport;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportsEditorWidgetView;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

@Dependent
@Templated("WSDLImportsEditorWidget.html#widget")
public class WSDLImportsEditorWidgetView extends Composite implements ImportsEditorWidgetView<WSDLImport> {

    @DataField
    private final TableElement table = Document.get().createTableElement();
    @Inject
    @DataField
    protected Button addImportButton;
    @DataField
    protected TableCellElement locationTableHeader = Document.get().createTHElement();
    @DataField
    protected TableCellElement namespaceTableHeader = Document.get().createTHElement();
    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<WSDLImport, WSDLImportListItemWidgetView> wsdlImports;
    @DataField
    private HeadingElement tableTitle = Document.get().createHElement(3);
    private Presenter presenter;

    @Override
    public void init(final ImportsEditorWidgetView.Presenter presenter) {
        this.presenter = presenter;

        tableTitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.WSDL_Imports_Title());

        addImportButton.setText(StunnerFormsClientFieldsConstants.CONSTANTS.Add());
        addImportButton.setIcon(IconType.PLUS);

        locationTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Location());
        namespaceTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Namespace());
    }

    @Override
    public int getImportsCount() {
        return wsdlImports.getValue().size();
    }

    @Override
    public void setDisplayStyle(Style.Display displayStyle) {
        table.getStyle().setDisplay(displayStyle);
    }

    @Override
    public List<WSDLImport> getImports() {
        return wsdlImports.getValue();
    }

    @Override
    public void setImports(List<WSDLImport> imports) {
        wsdlImports.setValue(imports);
    }

    @Override
    public WSDLImportListItemWidgetView getImportWidget(final int index) {
        return wsdlImports.getComponent(index);
    }

    @EventHandler("addImportButton")
    public void handleAddImportButton(final ClickEvent e) {
        presenter.addImport();
    }
}
