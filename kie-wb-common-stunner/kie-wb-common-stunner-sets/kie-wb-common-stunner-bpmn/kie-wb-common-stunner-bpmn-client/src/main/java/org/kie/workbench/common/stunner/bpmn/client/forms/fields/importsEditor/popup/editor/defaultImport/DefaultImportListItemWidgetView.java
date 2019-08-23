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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

@Templated("DefaultImportsEditorWidget.html#defaultImport")
public class DefaultImportListItemWidgetView extends Composite implements ImportListItemWidgetView<DefaultImport> {

    @Inject
    @AutoBound
    protected DataBinder<DefaultImport> defaultImport;

    @Inject
    @Bound
    @DataField
    protected Input className;

    @Inject
    @DataField
    protected Button deleteButton;

    private DefaultImportsEditorWidget parentWidget;

    @Override
    public void setParentWidget(final ImportsEditorWidgetView.Presenter<DefaultImport> parentWidget) {
        this.parentWidget = (DefaultImportsEditorWidget) parentWidget;
    }

    @Override
    public DefaultImport getModel() {
        return defaultImport.getModel();
    }

    @Override
    public void setModel(final DefaultImport model) {
        defaultImport.setModel(model);
        initControls();
    }

    public String getClassName() {
        return getModel().getClassName();
    }

    public void setClassName(String className) {
        getModel().setClassName(className);
    }

    private void initControls() {
        deleteButton.setIcon(IconType.TRASH);
        className.setText(getClassName());
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeImport(getModel());
    }
}
