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

import React, { useState } from "react";
import {
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem,
  ToolbarToggleGroup,
  Toolbar,
  ToolbarContent,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { FilterIcon } from "@patternfly/react-icons/dist/js/icons/filter-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import remove from "lodash/remove";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
interface WorkflowDefinitionListToolbarProps {
  filterWorkflowNames: string[];
  setFilterWorkflowNames: React.Dispatch<React.SetStateAction<string[]>>;
  applyFilter: () => void;
  onOpenTriggerCloudEvent?: () => void;
  doRefresh: () => void;
}

enum Category {
  PROCESS_NAME = "Workflow name",
}

const WorkflowDefinitionListToolbar: React.FC<WorkflowDefinitionListToolbarProps & OUIAProps> = ({
  applyFilter,
  filterWorkflowNames,
  setFilterWorkflowNames,
  onOpenTriggerCloudEvent,
  ouiaSafe,
  ouiaId,
  doRefresh,
}) => {
  const [workflowNameInput, setWorkflowNameInput] = useState<string>("");

  const doResetFilter = (): void => {
    applyFilter();
    setFilterWorkflowNames([]);
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === "Enter") {
      workflowNameInput.length > 0 && doApplyFilter();
    }
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newfilterWorkflowNames = [...filterWorkflowNames];
    if (categoryName === Category.PROCESS_NAME) {
      remove(newfilterWorkflowNames, (status: string) => {
        return status === value;
      });
      setFilterWorkflowNames(newfilterWorkflowNames);
      applyFilter();
    }
  };

  const doApplyFilter = (): void => {
    const newWorkflowNames = [...filterWorkflowNames];
    if (workflowNameInput && !newWorkflowNames.includes(workflowNameInput)) {
      newWorkflowNames.push(workflowNameInput);
      setFilterWorkflowNames(newWorkflowNames);
    }
    setWorkflowNameInput("");
    applyFilter();
  };

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          key="input-workflow-name"
          chips={filterWorkflowNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.PROCESS_NAME}
        >
          <InputGroup>
            <TextInput
              name="workflowName"
              id="workflowName"
              type="search"
              aria-label="workflow name"
              onChange={setWorkflowNameInput}
              onKeyPress={onEnterClicked}
              placeholder={`Filter by workflow name`}
              value={workflowNameInput}
              data-testid="workflow-filter-input"
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button id="apply-filter" variant="primary" onClick={doApplyFilter} data-testid="apply-filter">
            Apply Filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  const toolbarItems: JSX.Element = (
    <React.Fragment>
      <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </ToolbarToggleGroup>
      <ToolbarGroup variant="icon-button-group">
        <ToolbarItem>
          <Tooltip content={"Refresh"}>
            <Button variant="plain" onClick={doRefresh} id="refresh" data-testid="refresh">
              <SyncIcon />
            </Button>
          </Tooltip>
        </ToolbarItem>
      </ToolbarGroup>
      {onOpenTriggerCloudEvent && (
        <ToolbarGroup>
          <ToolbarItem variant="separator" />
          <ToolbarItem>
            <Button variant="primary" key={"triggerCloudEventButton"} onClick={() => onOpenTriggerCloudEvent()}>
              Trigger Cloud Event
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
      )}
    </React.Fragment>
  );

  return (
    <Toolbar
      id="workflow-definition-list-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, "workflow-definition-list-toolbar", ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default WorkflowDefinitionListToolbar;
