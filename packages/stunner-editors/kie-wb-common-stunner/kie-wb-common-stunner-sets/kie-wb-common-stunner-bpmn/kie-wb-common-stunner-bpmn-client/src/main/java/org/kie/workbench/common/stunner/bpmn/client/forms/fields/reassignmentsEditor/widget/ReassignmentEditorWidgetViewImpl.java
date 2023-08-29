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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLButtonElement;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.AssigneeLiveSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.event.ReassignmentEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.PeriodBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;

@Dependent
@Templated("ReassignmentEditorWidgetViewImpl.html#container")
public class ReassignmentEditorWidgetViewImpl extends Composite implements ReassignmentEditorWidgetView {

    private ReassignmentEditorWidgetView.Presenter presenter;

    private BaseModal modal = new BaseModal();

    private ReassignmentRow current;

    @Inject
    @AutoBound
    private DataBinder<ReassignmentRow> customerBinder;

    @Inject
    private Event<ReassignmentEvent> reassignmentEvent;

    private AssigneeLiveSearchService assigneeLiveSearchService;
    private AssigneeLiveSearchService groupsLiveSearchService;

    @DataField
    @Bound(property = "users")
    private MultipleSelectorInput<String> multipleSelectorInputUsers;

    @DataField
    @Bound(property = "groups")
    private MultipleSelectorInput<String> multipleSelectorInputGroups;

    @DataField
    @Bound(property = "type")
    private Select typeSelect = new Select();

    @Inject
    @DataField
    @Bound(property = "duration")
    private PeriodBox periodBox;

    private Option notStarted = new Option();

    private Option notCompleted = new Option();

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerUsers = new MultipleLiveSearchSelectionHandler();

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerGroups = new MultipleLiveSearchSelectionHandler();

    @DataField
    @Inject
    private HTMLButtonElement closeButton, okButton;

    @Inject
    private Validator validator;

    @Inject
    public ReassignmentEditorWidgetViewImpl(final MultipleSelectorInput multipleSelectorInputUsers,
                                            final MultipleSelectorInput multipleSelectorInputGroups,
                                            final AssigneeLiveSearchService assigneeLiveSearchService,
                                            final AssigneeLiveSearchService groupLiveSearchService) {
        initUsersAndGroupsDropdowns(multipleSelectorInputUsers,
                                    multipleSelectorInputGroups,
                                    assigneeLiveSearchService,
                                    groupLiveSearchService);
        initTypeSelector();
    }

    private void initUsersAndGroupsDropdowns(MultipleSelectorInput multipleUsers,
                                             MultipleSelectorInput multipleGroups,
                                             AssigneeLiveSearchService liveSearchServiceUsers,
                                             AssigneeLiveSearchService liveSearchServiceGroups) {

        this.assigneeLiveSearchService = liveSearchServiceUsers;
        this.groupsLiveSearchService = liveSearchServiceGroups;

        this.multipleSelectorInputUsers = multipleUsers;
        this.multipleSelectorInputGroups = multipleGroups;

        this.assigneeLiveSearchService.init(AssigneeType.USER);
        this.groupsLiveSearchService.init(AssigneeType.GROUP);

        this.multipleSelectorInputUsers.init(assigneeLiveSearchService, multipleLiveSearchSelectionHandlerUsers);
        this.multipleSelectorInputGroups.init(groupsLiveSearchService, multipleLiveSearchSelectionHandlerGroups);
    }

    protected void initTypeSelector() {
        notStarted.setValue(ReassignmentType.NotStartedReassign.getAlias());
        notStarted.setText(ReassignmentType.NotStartedReassign.getType());
        notCompleted.setText(ReassignmentType.NotCompletedReassign.getType());
        notCompleted.setValue(ReassignmentType.NotCompletedReassign.getAlias());

        typeSelect.add(notStarted);
        typeSelect.add(notCompleted);
    }

    @PostConstruct
    public void init() {
        closeButton.addEventListener("click", event -> close(), false);
        okButton.addEventListener("click", event -> ok(), false);
    }

    @Override
    public void init(final ReassignmentEditorWidgetView.Presenter presenter) {
        this.presenter = presenter;
        initModel();
    }

    private void initModel() {
        modal.setTitle(presenter.getNameHeader());
        modal.setSize(ModalSize.MEDIUM);
        modal.setBody(this);
        modal.setClosable(false);
        modal.addDomHandler(getEscDomHandler(), KeyDownEvent.getType());
    }

    protected KeyDownHandler getEscDomHandler() {
        return event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                close();
            }
        };
    }

    @Override
    public void createOrEdit(ReassignmentWidgetView parent, ReassignmentRow row) {
        current = row;
        customerBinder.setModel(row.clone());
        if (row.getUsers() != null && row.getUsers().size() > 0) {
            row.getUsers().forEach(u -> assigneeLiveSearchService.addCustomEntry(u));
            multipleSelectorInputUsers.setValue(row.getUsers());
        }

        if (row.getGroups() != null && row.getGroups().size() > 0) {
            row.getGroups().forEach(u -> groupsLiveSearchService.addCustomEntry(u));
            multipleSelectorInputGroups.setValue(row.getGroups());
        }

        if (row.getType() != null) {
            if (row.getType().equals(ReassignmentType.NotCompletedReassign)) {
                notStarted.setSelected(false);
                notCompleted.setSelected(true);
            } else {
                notStarted.setSelected(true);
                notCompleted.setSelected(false);
            }
        }
        modal.show();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        okButton.disabled = readOnly;
    }

    protected void ok() {
        // TODO looks like errai data binder doenst support liststore widgets.
        current.setUsers(multipleLiveSearchSelectionHandlerUsers.getSelectedValues());
        current.setGroups(multipleLiveSearchSelectionHandlerGroups.getSelectedValues());
        current.setType(customerBinder.getModel().getType());
        current.setDuration(customerBinder.getModel().getDuration());

        Set<ConstraintViolation<ReassignmentValue>> violations = validator.validate(current.toReassignmentValue());
        if (violations.isEmpty()) {
            reassignmentEvent.fire(new ReassignmentEvent(current));
            hide();
        } else {
            onViolationError(violations);
        }
    }

    private void onViolationError(Set<ConstraintViolation<ReassignmentValue>> violations) {
        violations.stream().forEach(error -> periodBox.setErrorText(error.getMessage()));
    }

    void close() {
        reassignmentEvent.fire(new ReassignmentEvent(null));
        hide();
    }

    protected void hide() {
        //clear widgets and set default values
        multipleSelectorInputUsers.setValue(Collections.EMPTY_LIST);
        multipleSelectorInputGroups.setValue(Collections.EMPTY_LIST);
        periodBox.clear();
        notStarted.setSelected(true);
        modal.hide();
    }
}
