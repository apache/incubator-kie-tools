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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportsEditorWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor.VariableListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.uberfire.workbench.events.NotificationEvent;

@Templated("DefaultImportsEditorWidget.html#defaultImport")
public class DefaultImportListItemWidgetView extends Composite implements ImportListItemWidgetView<DefaultImport>,
                                                                          ComboBoxView.ModelPresenter {

    protected static final String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    protected static final String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;

    @Inject
    @AutoBound
    protected DataBinder<DefaultImport> defaultImportDataBinder;

    @DataField
    protected ValueListBox<String> defaultClassNames = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected CustomDataTypeTextBox customClassName;

    @Inject
    protected ComboBox classNamesComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    protected DefaultImportsEditorWidget parentWidget;

    @Override
    public void setParentWidget(final ImportsEditorWidgetView.Presenter<DefaultImport> parentWidget) {
        this.parentWidget = (DefaultImportsEditorWidget) parentWidget;
        initListItem();
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox, final String value) {
        if (value != null && !value.isEmpty()) {
            getModel().setClassName(value);
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox, final String displayName) {
        String value = parentWidget.getDataType(displayName);
        getModel().setClassName(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        return getModel().getClassName();
    }

    @Override
    public void notifyModelChanged() {
        //There is no need to notify a model change here.
    }

    @PostConstruct
    public void init() {
        customClassName.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });

        deleteButton.setIcon(IconType.TRASH);
    }

    @Override
    public DefaultImport getModel() {
        return defaultImportDataBinder.getModel();
    }

    @Override
    public void setModel(final DefaultImport model) {
        defaultImportDataBinder.setModel(model);
    }

    protected void initListItem() {
        Map<String, String> dataTypes = parentWidget.getDataTypes();

        ListBoxValues classNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                 "Edit" + " ",
                                                                 null);

        List<String> displayNames = new ArrayList<>(dataTypes.values());
        classNameListBoxValues.addValues(displayNames);
        classNamesComboBox.setShowCustomValues(true);
        classNamesComboBox.setListBoxValues(classNameListBoxValues);

        String className = getModel().getClassName();
        if (className == null || className.isEmpty()) {
            className = Object.class.getSimpleName();
        }

        String displayName = parentWidget.getDataType(className);

        if ((className.equals(displayName))) {
            displayName = parentWidget.getDataTypes().get(className);
        }

        defaultClassNames.setValue(displayName);
        classNamesComboBox.init(this,
                                true,
                                defaultClassNames,
                                customClassName,
                                false,
                                true,
                                CUSTOM_PROMPT,
                                ENTER_TYPE_PROMPT);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeImport(getModel());
    }
}
