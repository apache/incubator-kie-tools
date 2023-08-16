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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.AssigneeLiveSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Assignee;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class AssigneeListItem {

    private Assignee assignee;

    private AssigneeLiveSearchService liveSearchService;

    private LiveSearchDropDown<String> liveSearchDropDown;

    private SingleLiveSearchSelectionHandler<String> searchSelectionHandler = new SingleLiveSearchSelectionHandler<>();

    private Command notifyChangeCommand;

    private ParameterizedCommand<AssigneeListItem> removeCommand;

    @Inject
    public AssigneeListItem(final LiveSearchDropDown liveSearchDropDown,
                            final AssigneeLiveSearchService liveSearchService) {
        this.liveSearchDropDown = liveSearchDropDown;
        this.liveSearchService = liveSearchService;
    }

    public void init(final AssigneeType type,
                     final Assignee assignee,
                     final Command notifyChangeCommand,
                     final ParameterizedCommand<AssigneeListItem> removeCommand,
                     final ParameterizedCommand<Throwable> errorCommand) {
        this.assignee = assignee;
        this.notifyChangeCommand = notifyChangeCommand;
        this.removeCommand = removeCommand;

        liveSearchService.init(type);

        liveSearchService.setSearchErrorHandler(errorCommand::execute);

        liveSearchDropDown.init(liveSearchService, searchSelectionHandler);

        liveSearchDropDown.setSelectedItem(assignee.getName());

        liveSearchDropDown.setOnChange(this::notifyChange);
    }

    public void notifyChange() {
        String value = searchSelectionHandler.getSelectedValue();

        if (value == null) {
            value = "";
        }

        assignee.setName(value);

        notifyChangeCommand.execute();
    }

    public void notifyRemoval() {
        removeCommand.execute(this);
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public LiveSearchDropDown<String> getLiveSearchDropDown() {
        return liveSearchDropDown;
    }
}
