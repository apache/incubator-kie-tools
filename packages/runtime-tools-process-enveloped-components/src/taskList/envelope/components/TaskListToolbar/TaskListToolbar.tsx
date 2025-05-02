/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem,
  ToolbarToggleGroup,
  Toolbar,
  ToolbarContent,
  ToolbarChipGroup,
  ToolbarChip,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { Select, SelectOption, SelectOptionObject, SelectVariant } from "@patternfly/react-core/deprecated";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { InputGroup, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { FilterIcon } from "@patternfly/react-icons/dist/js/icons/filter-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import _ from "lodash";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools/OuiaUtils";
import { TaskListQueryFilter } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

interface TaskListToolbarProps {
  activeFilter: TaskListQueryFilter;
  allTaskStates: string[];
  activeTaskStates: string[];
  applyFilter: (filter: TaskListQueryFilter) => Promise<void>;
  refresh: () => void;
}

enum Category {
  STATUS = "Status",
  TASK_NAME = "Task name",
}

const TaskListToolbar: React.FC<TaskListToolbarProps & OUIAProps> = ({
  activeFilter,
  allTaskStates,
  activeTaskStates,
  applyFilter,
  refresh,
  ouiaSafe,
  ouiaId,
}) => {
  const [isStatusExpanded, setStatusExpanded] = useState(false);

  const [allStates, setAllStates] = useState<string[]>([]);
  const [activeStates, setActiveStates] = useState<string[]>([]);

  // filters currently applied
  const [filterTaskStates, setFilterTaskStates] = useState<string[]>([]);
  const [filterTaskNames, setFilterTaskNames] = useState<string[]>([]);

  // filters not applied yet
  const [selectedTaskStates, setSelectedTaskStates] = useState<string[]>([]);
  const [taskNameInput, setTaskNameInput] = useState<string>("");

  useEffect(() => {
    setAllStates(allTaskStates);
    setActiveStates(activeTaskStates);
    setSelectedTaskStates(activeFilter.taskStates);
    setFilterTaskStates(activeFilter.taskStates);
    setFilterTaskNames(activeFilter.taskNames);
  }, [activeFilter, activeTaskStates, allTaskStates]);

  const createStatusMenuItems = useCallback(() => {
    return allStates.map((state) => <SelectOption key={state} value={state} />);
  }, [allStates]);

  const doResetFilter = useCallback(async () => {
    await applyFilter({
      taskStates: activeStates,
      taskNames: [],
    });
  }, [activeStates, applyFilter]);

  const onDeleteFilterGroup = useCallback(
    async (categoryName: string | ToolbarChipGroup, value: string | ToolbarChip) => {
      const newFilterTaskStates = [...filterTaskStates];
      const newFilterTaskNames = [...filterTaskNames];

      switch (categoryName) {
        case Category.STATUS:
          _.remove(newFilterTaskStates, (status: string) => {
            return status === value;
          });
          setFilterTaskStates(newFilterTaskStates);
          setSelectedTaskStates(newFilterTaskStates);
          break;
        case Category.TASK_NAME:
          _.remove(newFilterTaskNames, (status: string) => {
            return status === value;
          });
          setFilterTaskNames(newFilterTaskNames);
          break;
      }
      await applyFilter({
        taskNames: newFilterTaskNames,
        taskStates: newFilterTaskStates,
      });
    },
    [applyFilter, filterTaskNames, filterTaskStates]
  );

  const onSelectTaskState = useCallback(
    (event: React.MouseEvent | React.ChangeEvent, selection: string | SelectOptionObject): void => {
      const filter: string[] = [...selectedTaskStates];

      if (!filter.includes(selection.toString())) {
        filter.push(selection.toString());
      } else {
        _.remove(filter, (status: string) => {
          return status === selection;
        });
      }
      setSelectedTaskStates(filter);
    },
    [selectedTaskStates]
  );

  const doApplyFilter = useCallback(async () => {
    const newTaskNames = [...filterTaskNames];
    if (taskNameInput && !newTaskNames.includes(taskNameInput)) {
      newTaskNames.push(taskNameInput);
      setFilterTaskNames(newTaskNames);
    }
    setFilterTaskStates([...selectedTaskStates]);
    setTaskNameInput("");
    applyFilter({
      taskStates: [...selectedTaskStates],
      taskNames: newTaskNames,
    });
  }, [applyFilter, filterTaskNames, selectedTaskStates, taskNameInput]);

  const toggleGroupItems = useMemo(
    () => (
      <React.Fragment>
        <ToolbarGroup variant="filter-group">
          <ToolbarFilter chips={filterTaskStates} deleteChip={onDeleteFilterGroup} categoryName={Category.STATUS}>
            <Select
              variant={SelectVariant.checkbox}
              aria-label="Status"
              onToggle={(_event, val) => setStatusExpanded(val)}
              onSelect={onSelectTaskState}
              selections={selectedTaskStates}
              isOpen={isStatusExpanded}
              placeholderText="Status"
            >
              {createStatusMenuItems()}
            </Select>
          </ToolbarFilter>
          <ToolbarFilter chips={filterTaskNames} deleteChip={onDeleteFilterGroup} categoryName={Category.TASK_NAME}>
            <InputGroup>
              <InputGroupItem isFill>
                <TextInput
                  name="taskName"
                  id="taskName"
                  type="search"
                  aria-label="task name"
                  onChange={(_event, val) => setTaskNameInput(val)}
                  placeholder="Filter by Task name"
                  value={taskNameInput}
                />
              </InputGroupItem>
            </InputGroup>
          </ToolbarFilter>
          <ToolbarItem>
            <Button
              id="apply-filter"
              variant="primary"
              onClick={doApplyFilter}
              isDisabled={_.isEmpty(selectedTaskStates) && _.isEmpty(taskNameInput)}
            >
              Apply Filter
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
      </React.Fragment>
    ),
    [
      createStatusMenuItems,
      doApplyFilter,
      filterTaskNames,
      filterTaskStates,
      isStatusExpanded,
      onDeleteFilterGroup,
      onSelectTaskState,
      selectedTaskStates,
      taskNameInput,
    ]
  );

  const toolbarItems = useMemo(
    () => (
      <React.Fragment>
        <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
          {toggleGroupItems}
        </ToolbarToggleGroup>
        <ToolbarGroup variant="icon-button-group">
          <ToolbarItem>
            <Tooltip content={"Refresh"}>
              <Button variant="plain" onClick={refresh} id="refresh">
                <SyncIcon />
              </Button>
            </Tooltip>
          </ToolbarItem>
        </ToolbarGroup>
      </React.Fragment>
    ),
    [refresh, toggleGroupItems]
  );

  return (
    <Toolbar
      id="tasks-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, "tasks-toolbar", ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default TaskListToolbar;
