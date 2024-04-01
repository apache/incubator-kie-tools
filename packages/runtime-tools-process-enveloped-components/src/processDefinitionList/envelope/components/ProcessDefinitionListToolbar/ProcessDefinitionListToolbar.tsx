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
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface ProcessDefinitionListToolbarProps {
  filterProcessNames: string[];
  setFilterProcessNames: React.Dispatch<React.SetStateAction<string[]>>;
  applyFilter: () => void;
  singularProcessLabel: string;
}

enum Category {
  PROCESS_NAME = "Process name",
}

const ProcessDefinitionListToolbar: React.FC<ProcessDefinitionListToolbarProps & OUIAProps> = ({
  applyFilter,
  filterProcessNames,
  setFilterProcessNames,
  singularProcessLabel,
  ouiaSafe,
  ouiaId,
}) => {
  const [processNameInput, setProcessNameInput] = useState<string>("");

  const doResetFilter = (): void => {
    applyFilter();
    setFilterProcessNames([]);
  };

  const doRefresh = (): void => {
    setFilterProcessNames([...filterProcessNames]);
    applyFilter();
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === "Enter") {
      processNameInput.length > 0 && doApplyFilter();
    }
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newfilterProcessNames = [...filterProcessNames];
    if (categoryName === Category.PROCESS_NAME) {
      remove(newfilterProcessNames, (status: string) => {
        return status === value;
      });
      setFilterProcessNames(newfilterProcessNames);
      applyFilter();
    }
  };

  const doApplyFilter = (): void => {
    const newProcessNames = [...filterProcessNames];
    if (processNameInput && !newProcessNames.includes(processNameInput)) {
      newProcessNames.push(processNameInput);
      setFilterProcessNames(newProcessNames);
    }
    setProcessNameInput("");
    applyFilter();
  };

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          key="input-process-name"
          chips={filterProcessNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.PROCESS_NAME}
        >
          <InputGroup>
            <TextInput
              name="processName"
              id="processName"
              type="search"
              aria-label="process name"
              onChange={setProcessNameInput}
              onKeyPress={onEnterClicked}
              placeholder={`Filter by ${singularProcessLabel.toLowerCase()} name`}
              value={processNameInput}
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
    </React.Fragment>
  );

  return (
    <Toolbar
      id="process-definition-list-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, "process-definition-list-toolbar", ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default ProcessDefinitionListToolbar;
