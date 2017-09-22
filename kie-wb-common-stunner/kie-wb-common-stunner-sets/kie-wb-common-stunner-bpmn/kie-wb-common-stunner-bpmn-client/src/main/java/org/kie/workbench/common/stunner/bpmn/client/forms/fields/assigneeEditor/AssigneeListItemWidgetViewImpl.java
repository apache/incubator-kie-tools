/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.text.shared.Renderer;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link AssigneeRow}s.
 * <p/>
 * The Name field of AssigneeRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated("AssigneeEditorWidget.html#assigneeRow")
public class AssigneeListItemWidgetViewImpl implements AssigneeListItemWidgetView,
                                                       ComboBoxView.ModelPresenter {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(AssigneeRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. assigneeRow.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    protected DataBinder<AssigneeRow> assigneeRow;

    private boolean allowDuplicateNames = false;
    private String duplicateNameErrorMessage = "An assignee with this name already exists";

    private String currentValue;

    @DataField
    protected ValueListBox<String> name = new ValueListBox<String>(new Renderer<String>() {
        public String render(final String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected TextBox customName;

    @Inject
    protected ComboBox nameComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    /**
     * Required for implementation of Delete button.
     */
    private AssigneeEditorWidgetView.Presenter parentWidget;

    public void setParentWidget(final AssigneeEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        setCustomName(value);
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        setName(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        String value = getCustomName();
        if (value == null || value.isEmpty()) {
            value = getName();
        }
        return value;
    }

    @PostConstruct
    public void init() {
        // Configure name and customName controls
        nameComboBox.init(this,
                          true,
                          name,
                          customName,
                          false,
                          false,
                          CUSTOM_PROMPT,
                          ENTER_TYPE_PROMPT);
        customName.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                int iChar = event.getNativeKeyCode();
                if (iChar == ' ') {
                    event.preventDefault();
                }
            }
        });
    }

    @Override
    public AssigneeRow getModel() {
        return assigneeRow.getModel();
    }

    @Override
    public void setModel(final AssigneeRow model) {
        assigneeRow.setModel(model);
        initAssigneeControls();
        currentValue = getModel().toString();
    }

    @Override
    public String getName() {
        return getModel().getName();
    }

    @Override
    public void setName(final String name) {
        getModel().setName(name);
    }

    @Override
    public String getCustomName() {
        return getModel().getCustomName();
    }

    @Override
    public void setCustomName(final String customName) {
        getModel().setCustomName(customName);
    }

    @Override
    public void setNames(final ListBoxValues nameListBoxValues) {
        nameComboBox.setCurrentTextValue("");
        nameComboBox.setListBoxValues(nameListBoxValues);
        nameComboBox.setShowCustomValues(true);
        String cn = getCustomName();
        if (cn != null && !cn.isEmpty()) {
            nameComboBox.addCustomValueToListBoxValues(cn,
                                                       "");
        }
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return parentWidget.isDuplicateName(name);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeAssignee(getModel());
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link AssigneeRow}.
     */
    private void initAssigneeControls() {
        deleteButton.setIcon(IconType.TRASH);
        String cn = getCustomName();
        if (cn != null && !cn.isEmpty()) {
            customName.setValue(cn);
            name.setValue(cn);
        } else if (getName() != null) {
            name.setValue(getName());
        }
    }

    @Override
    public void notifyModelChanged() {
        String oldValue = currentValue;
        currentValue = getModel().toString();
        if (oldValue == null) {
            if (currentValue != null && currentValue.length() > 0) {
                parentWidget.notifyModelChanged();
            }
        } else if (!oldValue.equals(currentValue)) {
            parentWidget.notifyModelChanged();
        }
    }
}
