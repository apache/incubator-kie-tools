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
import React, { useCallback, useMemo, useState } from "react";
import {
  Toolbar,
  ToolbarItem,
  ToolbarContent,
  ToolbarFilter,
  ToolbarToggleGroup,
  ToolbarGroup,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { FilterIcon } from "@patternfly/react-icons/dist/js/icons/filter-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { ProcessDefinitionsFilter } from "../../api";

enum Category {
  PROCESS_NAME = "Process Definition name",
}

interface ProcessDefinitionsListToolbarProps {
  filters: ProcessDefinitionsFilter;
  setFilters: React.Dispatch<React.SetStateAction<ProcessDefinitionsFilter>>;
  applyFilter: (filter: ProcessDefinitionsFilter) => Promise<void>;
  refresh: () => Promise<void>;
}

const ProcessDefinitionsListToolbar: React.FC<ProcessDefinitionsListToolbarProps> = ({
  filters,
  setFilters,
  applyFilter,
  refresh,
}) => {
  const [processNameInput, setProcessNameInput] = useState<string>("");

  const onDeleteChip = useCallback(
    async (_: any, value: string) => {
      let updatedProcessNamesList: string[] = [];
      setFilters((currentFilters) => {
        updatedProcessNamesList = [...(currentFilters.processNames ?? [])].filter(
          (processName) => processName !== value
        );
        return { ...currentFilters, processNames: updatedProcessNamesList };
      });
      await applyFilter({
        processNames: updatedProcessNamesList,
      });
    },
    [applyFilter, setFilters]
  );

  const onApplyFilter = useCallback(async () => {
    let updatedProcessNamesList: string[] = [];
    setFilters((currentFilters) => {
      updatedProcessNamesList = [...(currentFilters.processNames ?? [])];
      if (processNameInput && !updatedProcessNamesList.includes(processNameInput)) {
        updatedProcessNamesList.push(processNameInput);
      }
      return {
        ...currentFilters,
        processNames: updatedProcessNamesList,
      };
    });
    setProcessNameInput("");

    await applyFilter({
      processNames: updatedProcessNamesList,
    });
  }, [applyFilter, setFilters, processNameInput]);

  const onEnterClicked = useCallback(
    async (event: React.KeyboardEvent<EventTarget>) => {
      if (event.key === "Enter") {
        processNameInput.length > 0 && (await onApplyFilter());
      }
    },
    [onApplyFilter, processNameInput.length]
  );

  const resetAllFilters = useCallback(async () => {
    const defaultFilters = {
      processNames: [],
    };
    setFilters(defaultFilters);
    await applyFilter(defaultFilters);
  }, [applyFilter, setFilters]);

  const toggleGroupItems: JSX.Element = useMemo(
    () => (
      <React.Fragment>
        <ToolbarGroup variant="filter-group">
          <ToolbarFilter chips={filters.processNames} deleteChip={onDeleteChip} categoryName={Category.PROCESS_NAME}>
            <InputGroup>
              <TextInput
                name="processName"
                id="processName"
                data-testid="processName"
                type="search"
                aria-label="process definition name"
                onChange={(_event, value) => setProcessNameInput(value)}
                onKeyPress={onEnterClicked}
                placeholder="Filter by Process Definition name"
                value={processNameInput}
              />
            </InputGroup>
          </ToolbarFilter>
          <ToolbarItem>
            <Button variant="primary" onClick={onApplyFilter} data-testid="apply-filter-button">
              Apply filter
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
      </React.Fragment>
    ),
    [filters.processNames, onApplyFilter, onDeleteChip, onEnterClicked, processNameInput]
  );

  const toolbarItems: JSX.Element = useMemo(
    () => (
      <React.Fragment>
        <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
          {toggleGroupItems}
        </ToolbarToggleGroup>
        <ToolbarGroup variant="icon-button-group">
          <ToolbarItem>
            <Tooltip content={"Refresh"}>
              <Button variant="plain" onClick={refresh} data-testid="refresh">
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
      data-testid="data-toolbar-with-filter"
      className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={resetAllFilters}
      clearFiltersButtonText="Reset to default"
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default ProcessDefinitionsListToolbar;
