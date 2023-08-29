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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportsEditorWidgetView;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

@Templated("WSDLImportsEditorWidget.html#wsdlImport")
public class WSDLImportListItemWidgetView extends Composite implements ImportListItemWidgetView<WSDLImport> {

    @Inject
    @AutoBound
    protected DataBinder<WSDLImport> wsdlImport;

    @Inject
    @Bound
    @DataField
    protected Input location;

    @Inject
    @Bound
    @DataField
    protected Input namespace;

    @Inject
    @DataField
    protected Button deleteButton;

    private WSDLImportsEditorWidget parentWidget;

    @Override
    public void setParentWidget(final ImportsEditorWidgetView.Presenter<WSDLImport> parentWidget) {
        this.parentWidget = (WSDLImportsEditorWidget) parentWidget;
    }

    @Override
    public WSDLImport getModel() {
        return wsdlImport.getModel();
    }

    @Override
    public void setModel(final WSDLImport model) {
        wsdlImport.setModel(model);
        initControls();
    }

    public String getLocation() {
        return getModel().getLocation();
    }

    public void setLocation(String location) {
        getModel().setLocation(location);
    }

    public String getNamespace() {
        return getModel().getNamespace();
    }

    public void setNamespace(String namespace) {
        getModel().setNamespace(namespace);
    }

    private void initControls() {
        deleteButton.setIcon(IconType.TRASH);
        location.setText(getLocation());
        namespace.setText(getNamespace());
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeImport(getModel());
    }
}
