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
import React, { useState, useEffect, useCallback, useMemo } from "react";
import { DropdownItem, Dropdown, KebabToggle } from "@patternfly/react-core/deprecated";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import {
  Toolbar,
  ToolbarContent,
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import {
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuItem,
  OverflowMenuControl,
} from "@patternfly/react-core/dist/js/components/OverflowMenu";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";

import { JobStatus, Job } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { JobsManagementDriver } from "../../../api";
import "../styles.css";
import { IOperations } from "@kie-tools/runtime-tools-components/dist/components/BulkList";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface JobsManagementToolbarProps {
  chips: JobStatus[];
  driver: JobsManagementDriver;
  doQueryJobs: (offset: number, limit: number) => Promise<void>;
  jobOperations: IOperations;
  onResetToDefault: () => void;
  onRefresh: () => void;
  selectedStatus: JobStatus[];
  selectedJobInstances: Job[];
  setSelectedJobInstances: (selectedJobInstances: Job[]) => void;
  setSelectedStatus: (selectedStatus: ((selectedStatus: JobStatus[]) => JobStatus[]) | JobStatus[]) => void;
  setChips: (chips: ((chip: JobStatus[]) => JobStatus[]) | JobStatus[]) => void;
  setDisplayTable: (displayTable: boolean) => void;
  setIsLoading: (isLoading: boolean) => void;
  onApplyFilter: () => void;
}
const JobsManagementToolbar: React.FC<JobsManagementToolbarProps & OUIAProps> = ({
  chips,
  driver,
  doQueryJobs,
  onResetToDefault,
  jobOperations,
  onRefresh,
  selectedStatus,
  selectedJobInstances,
  setChips,
  setDisplayTable,
  setIsLoading,
  setSelectedStatus,
  setSelectedJobInstances,
  onApplyFilter,
  ouiaId,
  ouiaSafe,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [chipRemoved, setChipRemoved] = useState<boolean>(false);
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const statusMenuItems: JSX.Element[] = [
    <SelectOption key="CANCELED" value="CANCELED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="EXECUTED" value="EXECUTED" />,
    <SelectOption key="RETRY" value="RETRY" />,
    <SelectOption key="SCHEDULED" value="SCHEDULED" />,
  ];

  const onStatusToggle = useCallback((): void => {
    setIsExpanded((currentIsExpanded) => !currentIsExpanded);
  }, []);

  const onDelete = useCallback(
    (type: string, id: string): void => {
      setChips((currentChips) => {
        const chipsCopy = [...currentChips];
        const tempChips = chipsCopy.filter((item) => item !== id);
        return tempChips;
      });
      setSelectedJobInstances([]);
      setSelectedStatus((currentSelectedStatus) => {
        let selectedStatusCopy = [...currentSelectedStatus];
        selectedStatusCopy = selectedStatusCopy.filter((item) => item !== id);
        return selectedStatusCopy;
      });
    },
    [setChips, setSelectedJobInstances, setSelectedStatus]
  );

  const onSelect = useCallback(
    (event, selection: JobStatus): void => {
      setSelectedStatus((currentSelectedStatus) => {
        let selectionText = event.target.id;
        selectionText = selectionText.split("pf-random-id-")[1].split("-")[1];
        const selectedStatusCopy = [...currentSelectedStatus];
        if (currentSelectedStatus.includes(selectionText)) {
          return selectedStatusCopy.filter((item) => item !== selectionText);
        } else {
          return [...selectedStatusCopy, selectionText];
        }
      });
    },
    [setSelectedStatus]
  );

  const cancelJobsOptionSelect = useCallback((): void => {
    setIsKebabOpen((currentIsKebabOpen) => !currentIsKebabOpen);
  }, []);

  const cancelJobsKebabToggle = useCallback((isOpen): void => {
    setIsKebabOpen(isOpen);
  }, []);

  const dropdownItemsCancelJobsButtons = useCallback((): JSX.Element[] => {
    return [
      <DropdownItem
        key="cancel"
        onClick={jobOperations[OperationType.CANCEL].functions.perform}
        isDisabled={selectedJobInstances.length === 0}
      >
        Cancel selected
      </DropdownItem>,
    ];
  }, [jobOperations, selectedJobInstances.length]);

  const cancelJobsOption: JSX.Element = useMemo(
    () => (
      <OverflowMenu breakpoint="xl">
        <OverflowMenuContent>
          <OverflowMenuItem>
            <Button
              variant="secondary"
              onClick={jobOperations[OperationType.CANCEL].functions.perform}
              isDisabled={selectedJobInstances.length === 0}
            >
              Cancel selected
            </Button>
          </OverflowMenuItem>
        </OverflowMenuContent>
        <OverflowMenuControl>
          <Dropdown
            onSelect={cancelJobsOptionSelect}
            toggle={<KebabToggle onToggle={cancelJobsKebabToggle} />}
            isOpen={isKebabOpen}
            isPlain
            dropdownItems={dropdownItemsCancelJobsButtons()}
          />
        </OverflowMenuControl>
      </OverflowMenu>
    ),
    [
      cancelJobsKebabToggle,
      cancelJobsOptionSelect,
      dropdownItemsCancelJobsButtons,
      isKebabOpen,
      jobOperations,
      selectedJobInstances.length,
    ]
  );

  useEffect(() => {
    if (chipRemoved) {
      onRefresh();
    }
  }, [chipRemoved, onRefresh]);

  return (
    <Toolbar
      id="data-toolbar-with-chip-groups"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="md"
      clearAllFilters={() => {
        onResetToDefault();
      }}
      clearFiltersButtonText="Reset to default"
    >
      <ToolbarContent>
        <ToolbarGroup variant="filter-group" {...componentOuiaProps(ouiaId, "job-filters", ouiaSafe)}>
          <ToolbarFilter
            chips={chips}
            deleteChip={onDelete}
            categoryName="Status"
            className="kogito-jobs-management__state-dropdown-list"
          >
            <Select
              variant={SelectVariant.checkbox}
              aria-label="Status"
              onToggle={onStatusToggle}
              onSelect={onSelect}
              selections={selectedStatus}
              isOpen={isExpanded}
              placeholderText="Status"
              id="status-select"
            >
              {statusMenuItems}
            </Select>
          </ToolbarFilter>
        </ToolbarGroup>
        <ToolbarGroup {...componentOuiaProps(ouiaId, "job-filters/button", ouiaSafe)}>
          <ToolbarItem>
            <Button
              variant="primary"
              onClick={onApplyFilter}
              id="apply-filter"
              isDisabled={!(selectedStatus.length > 0)}
            >
              Apply Filter
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarGroup>
          <ToolbarItem>
            <Button
              variant="plain"
              onClick={() => {
                onRefresh();
                setSelectedJobInstances([]);
              }}
              id="refresh-button"
              ouiaId="refresh-button"
              aria-label={"Refresh list"}
            >
              <SyncIcon />
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarItem variant="separator" />
        <ToolbarGroup className="pf-v5-u-ml-md" id="jobs-management-buttons">
          {cancelJobsOption}
        </ToolbarGroup>
      </ToolbarContent>
    </Toolbar>
  );
};

export default JobsManagementToolbar;
