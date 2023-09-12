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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.kie.workbench.common.stunner.client.widgets.canvas.actions.IntegerTextBox;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.ALPHA_NUM_REGEXP;

@Templated("RolesEditorWidget.html#tableRow")
public class RolesListItemWidgetViewImpl implements RolesListItemWidgetView,
                                                    IsElement {

    public static final String INVALID_CHARACTERS_MESSAGE = "Invalid characters";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "A role with this name already exists";
    private static final String EMPTY_ERROR_MESSAGE = "Role name already cannot be empty";

    @Inject
    @AutoBound
    protected DataBinder<KeyValueRow> row;

    @Inject
    @Bound(property = "key")
    @DataField("roleInput")
    protected VariableNameTextBox role;

    @Inject
    @Bound(property = "value")
    @DataField("cardinalityInput")
    protected IntegerTextBox cardinality;

    private boolean allowDuplicateNames = false;

    private String previousRole;

    private String previousCardinality;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    @Inject
    @DataField("tableRow")
    protected HTMLTableRowElement tableRow;

    /**
     * Required for implementation of Delete button.
     */
    private Optional<RolesEditorWidgetView> parentWidget;

    protected RolesListItemWidgetViewImpl() {
    }

    public void setParentWidget(final RolesEditorWidgetView parentWidget) {
        this.parentWidget = Optional.ofNullable(parentWidget);
    }

    @PostConstruct
    public void init() {
        role.setRegExp(ALPHA_NUM_REGEXP, INVALID_CHARACTERS_MESSAGE, INVALID_CHARACTERS_MESSAGE);
        role.addChangeHandler((e) -> handleValueChanged());
        cardinality.addChangeHandler((e) -> handleValueChanged());
        cardinality.addFocusHandler((e) -> handleFocus());
        deleteButton.setIcon(IconType.TRASH);
        deleteButton.addClickHandler((e) -> handleDeleteButton());
        //show the widget that is hidden on the template
        tableRow.hidden = false;
    }

    private void handleFocus() {
        if (Objects.equals("0", cardinality.getText())) {
            cardinality.clear();
        }
    }

    private void handleValueChanged() {
        final String currentRole = row.getModel().getKey();
        final String currentCardinality = row.getModel().getValue();
        if (StringUtils.isEmpty(currentRole)) {
            notification.fire(new NotificationEvent(EMPTY_ERROR_MESSAGE,
                                                    NotificationEvent.NotificationType.ERROR));
            row.getModel().setKey(previousRole);
            return;
        }
        if (!allowDuplicateNames && isDuplicateName(currentRole)) {
            notification.fire(new NotificationEvent(DUPLICATE_NAME_ERROR_MESSAGE,
                                                    NotificationEvent.NotificationType.ERROR));
            row.getModel().setKey(previousRole);
            return;
        }

        //skip in case not modified values
        if ((Objects.equals(previousRole, currentRole) && Objects.equals(previousCardinality, currentCardinality))) {
            return;
        }
        previousRole = currentRole;
        previousCardinality = currentCardinality;
        notifyModelChanged();
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.PROCESS;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        role.setEnabled(!readOnly);
        cardinality.setEnabled(!readOnly);
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return parentWidget.map(p -> p.isDuplicateName(name)).orElse(false);
    }

    public void handleDeleteButton() {
        parentWidget.ifPresent(p -> p.remove(getValue()));
    }

    @Override
    public void notifyModelChanged() {
        parentWidget.ifPresent(RolesEditorWidgetView::notifyModelChanged);
    }

    @Override
    public void setValue(KeyValueRow value) {
        //when first setting the value then set as previous as well
        if (Objects.isNull(previousRole)) {
            previousRole = value.getKey();
            previousCardinality = value.getValue();
        }
        row.setModel(value);
    }

    @Override
    public KeyValueRow getValue() {
        return row.getModel();
    }

    @Override
    public KeyValueRow getModel() {
        return getValue();
    }

    @Override
    public void setModel(KeyValueRow model) {
        setValue(model);
    }
}
