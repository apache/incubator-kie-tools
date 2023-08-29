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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

@Dependent
@Templated("DefaultImportsEditorWidget.html#widget")
public class DefaultImportsEditorWidgetView extends Composite implements ImportsEditorWidgetView<DefaultImport> {

    @DataField
    private final TableElement table = Document.get().createTableElement();
    @Inject
    @DataField
    protected Button addImportButton;
    @DataField
    protected TableCellElement classNameTableHeader = Document.get().createTHElement();
    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<DefaultImport, DefaultImportListItemWidgetView> defaultImports;
    @DataField
    private HeadingElement tableTitle = Document.get().createHElement(3);
    private Presenter presenter;

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        tableTitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type_Imports_Title());

        addImportButton.setText(StunnerFormsClientFieldsConstants.CONSTANTS.Add());
        addImportButton.setIcon(IconType.PLUS);

        classNameTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.ClassName());
    }

    @Override
    public int getImportsCount() {
        return defaultImports.getValue().size();
    }

    @Override
    public void setDisplayStyle(Style.Display displayStyle) {
        table.getStyle().setDisplay(displayStyle);
    }

    @Override
    public List<DefaultImport> getImports() {
        return defaultImports.getValue();
    }

    @Override
    public void setImports(List<DefaultImport> imports) {
        defaultImports.setValue(imports);
    }

    @Override
    public DefaultImportListItemWidgetView getImportWidget(final int index) {
        return defaultImports.getComponent(index);
    }

    @EventHandler("addImportButton")
    public void handleAddImportButton(final ClickEvent e) {
        presenter.addImport();
    }
}
