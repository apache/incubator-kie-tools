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

import React, { useCallback, useState } from "react";
import {
  Dropdown,
  DropdownItem,
  KebabToggle,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleCheckbox,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import {
  Toolbar,
  ToolbarItem,
  ToolbarContent,
  ToolbarFilter,
  ToolbarToggleGroup,
  ToolbarGroup,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import {
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuControl,
  OverflowMenuItem,
} from "@patternfly/react-core/dist/js/components/OverflowMenu";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { FilterIcon } from "@patternfly/react-icons/dist/js/icons/filter-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import _ from "lodash";
import {
  WorkflowInstance,
  WorkflowInstanceState,
  WorkflowInstanceFilter,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import {
  BulkListType,
  IOperationResults,
  IOperations,
} from "@kie-tools/runtime-tools-components/dist/components/BulkList";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { WorkflowInfoModal } from "@kie-tools/runtime-tools-components/dist/components/WorkflowInfoModal";
import { WorkflowListDriver } from "../../../api";
import "../styles.css";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { formatForBulkListWorkflowInstance } from "../utils/WorkflowListUtils";
import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

enum Category {
  STATUS = "Status",
  BUSINESS_KEY = "Business key",
}

enum BulkSelectionType {
  NONE = "NONE",
  PARENT = "PARENT",
  PARENT_CHILD = "PARENT_CHILD",
}

interface WorkflowListToolbarProps {
  filters: WorkflowInstanceFilter;
  setFilters: React.Dispatch<React.SetStateAction<WorkflowInstanceFilter>>;
  applyFilter: (filter: WorkflowInstanceFilter) => void;
  refresh: () => void;
  workflowStates: WorkflowInstanceState[];
  setWorkflowStates: React.Dispatch<React.SetStateAction<WorkflowInstanceState[]>>;
  selectedInstances: WorkflowInstance[];
  setSelectedInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  workflowInstances: WorkflowInstance[];
  setWorkflowInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  isAllChecked: boolean;
  setIsAllChecked: React.Dispatch<React.SetStateAction<boolean>>;
  driver: WorkflowListDriver;
  defaultStatusFilter: WorkflowInstanceState[];
}

const WorkflowListToolbar: React.FC<WorkflowListToolbarProps & OUIAProps> = ({
  filters,
  setFilters,
  applyFilter,
  refresh,
  workflowStates,
  setWorkflowStates,
  selectedInstances,
  setSelectedInstances,
  workflowInstances,
  setWorkflowInstances,
  isAllChecked,
  setIsAllChecked,
  driver,
  defaultStatusFilter,
  ouiaId,
  ouiaSafe,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [businessKeyInput, setBusinessKeyInput] = useState<string>("");
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>("");
  const [titleType, setTitleType] = useState<string>("");
  const [operationType, setOperationType] = useState<OperationType | undefined>(undefined);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] = useState<boolean>(false);
  const [operationResults, setOperationResults] = useState<IOperationResults>({
    ABORT: {
      successItems: [],
      failedItems: [],
      ignoredItems: [],
    },
    SKIP: {
      successItems: [],
      failedItems: [],
      ignoredItems: [],
    },
    RETRY: {
      successItems: [],
      failedItems: [],
      ignoredItems: [],
    },
  });

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const operations: IOperations = {
    ABORT: {
      type: BulkListType.WORKFLOW,
      results: operationResults[OperationType.ABORT],
      messages: {
        successMessage: `Aborted workflows: `,
        noItemsMessage: `No workflows were aborted`,
        warningMessage: !workflowStates.includes(WorkflowInstanceState.Aborted)
          ? `Note: The workflow status has been updated. The list may appear inconsistent until you refresh any applied filters.`
          : "",
        ignoredMessage: `These workflows were ignored because they were already completed or aborted.`,
      },
      functions: {
        perform: async () => {
          const ignoredItems: any[] = [];
          const remainingInstances = selectedInstances.filter((instance: WorkflowInstance) => {
            if (
              instance.state === WorkflowInstanceState.Aborted ||
              instance.state === WorkflowInstanceState.Completed
            ) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await driver.handleWorkflowMultipleAction(remainingInstances, OperationType.ABORT).then((result) => {
            onShowMessage(
              "Abort operation",
              result.successWorkflowInstances,
              result.failedWorkflowInstances,
              ignoredItems,
              OperationType.ABORT
            );
            workflowInstances.forEach((instance) => {
              result.successWorkflowInstances.forEach((successInstances) => {
                if (successInstances.id === instance.id) {
                  instance.state = WorkflowInstanceState.Aborted;
                }
              });
            });
            setWorkflowInstances([...workflowInstances]);
          });
        },
      },
    },
    SKIP: {
      type: BulkListType.WORKFLOW,
      results: operationResults[OperationType.SKIP],
      messages: {
        successMessage: `Skipped workflows: `,
        noItemsMessage: `No workflows were skipped`,
        ignoredMessage: `These workflows were ignored because they were not in error state.`,
      },
      functions: {
        perform: async () => {
          const ignoredItems: any[] = [];
          const remainingInstances = selectedInstances.filter((instance: WorkflowInstance) => {
            if (instance.state !== WorkflowInstanceState.Error) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await driver.handleWorkflowMultipleAction(remainingInstances, OperationType.SKIP).then((result) => {
            onShowMessage(
              "Skip operation",
              result.successWorkflowInstances,
              result.failedWorkflowInstances,
              ignoredItems,
              OperationType.SKIP
            );
          });
        },
      },
    },
    RETRY: {
      type: BulkListType.WORKFLOW,
      results: operationResults[OperationType.RETRY],
      messages: {
        successMessage: `Retriggered workflows: `,
        noItemsMessage: `No workflows were retriggered`,
        ignoredMessage: `These workflows were ignored because they were not in error state.`,
      },
      functions: {
        perform: async () => {
          const ignoredItems: any[] = [];
          const remainingInstances = selectedInstances.filter((instance) => {
            if (instance["state"] !== WorkflowInstanceState.Error) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await driver.handleWorkflowMultipleAction(remainingInstances, OperationType.RETRY).then((result) => {
            onShowMessage(
              "Retry operation",
              result.successWorkflowInstances,
              result.failedWorkflowInstances,
              ignoredItems,
              OperationType.RETRY
            );
          });
        },
      },
    },
  };

  const onShowMessage = (
    title: string,
    successItems: WorkflowInstance[],
    failedItems: WorkflowInstance[],
    ignoredItems: WorkflowInstance[],
    operation: OperationType
  ) => {
    setModalTitle(title);
    setTitleType("success");
    setOperationType(operation);
    setOperationResults({
      ...operationResults,
      [operation]: {
        ...operationResults[operation],
        successItems: formatForBulkListWorkflowInstance(successItems),
        failedItems: formatForBulkListWorkflowInstance(failedItems),
        ignoredItems: formatForBulkListWorkflowInstance(ignoredItems),
      },
    });
    handleModalToggle();
  };

  const checkboxDropdownToggle = (): void => {
    setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
  };

  const onStatusToggle = (isExpandedItem: boolean): void => {
    setIsExpanded(isExpandedItem);
  };

  const onWorkflowManagementButtonSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onWorkflowManagementKebabToggle = (isOpen: boolean) => {
    setIsKebabOpen(isOpen);
  };

  const onSelect = (event: any, selection: any): void => {
    if (workflowStates.includes(selection)) {
      const newWorkflowStates = [...workflowStates].filter((state) => state !== selection);
      setWorkflowStates(newWorkflowStates);
    } else {
      setWorkflowStates([...workflowStates, selection]);
    }
  };

  const onDeleteChip = (categoryName: Category, value: string): void => {
    const clonedWorkflowStates = [...workflowStates];
    const clonedBusinessKeyArray = [...(filters.businessKey ?? [])];
    switch (categoryName) {
      case Category.STATUS:
        _.remove(clonedWorkflowStates, (status: string) => {
          return status === value;
        });
        setWorkflowStates(clonedWorkflowStates);
        setFilters({ ...filters, status: clonedWorkflowStates });
        break;
      case Category.BUSINESS_KEY:
        _.remove(clonedBusinessKeyArray, (businessKey: string) => {
          return businessKey === value;
        });
        setFilters({ ...filters, businessKey: clonedBusinessKeyArray });
        break;
    }
    applyFilter({
      status: clonedWorkflowStates,
      businessKey: clonedBusinessKeyArray,
    });
  };

  const onApplyFilter = (): void => {
    setBusinessKeyInput("");
    const clonedBusinessKeyArray = [...(filters.businessKey ?? [])];
    if (businessKeyInput && !clonedBusinessKeyArray.includes(businessKeyInput)) {
      clonedBusinessKeyArray.push(businessKeyInput);
    }
    setFilters({
      ...filters,
      status: workflowStates,
      businessKey: clonedBusinessKeyArray,
    });
    applyFilter({
      status: workflowStates,
      businessKey: clonedBusinessKeyArray,
    });
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === "Enter") {
      businessKeyInput.length > 0 && onApplyFilter();
    }
  };

  const resetAllFilters = (): void => {
    const defaultFilters = {
      status: defaultStatusFilter,
      businessKey: [],
    };
    setWorkflowStates(defaultFilters.status);
    setFilters(defaultFilters);
    applyFilter(defaultFilters);
  };

  const resetSelected = (): void => {
    const clonedWorkflowInstances = _.cloneDeep(workflowInstances);
    clonedWorkflowInstances.forEach((workflowInstance: WorkflowInstance) => {
      workflowInstance.isSelected = false;
      /* istanbul ignore else */
      if (!_.isEmpty(workflowInstance.childWorkflowInstances)) {
        workflowInstance.childWorkflowInstances?.forEach((childInstance: WorkflowInstance) => {
          childInstance.isSelected = false;
        });
      }
    });
    setWorkflowInstances(clonedWorkflowInstances);
    setSelectedInstances([]);
    setIsAllChecked(false);
  };

  const handleCheckboxSelectClick = (selection: string, isCheckBoxClicked: boolean): void => {
    const clonedWorkflowInstances = [...workflowInstances];
    if (selection === BulkSelectionType.NONE) {
      clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
        instance.isSelected = false;
        instance.childWorkflowInstances &&
          instance.childWorkflowInstances.length > 0 &&
          instance.childWorkflowInstances.forEach((childInstance: WorkflowInstance) => {
            childInstance.isSelected = false;
          });
      });
      setSelectedInstances([]);
    }
    if (selection === BulkSelectionType.PARENT) {
      const tempSelectedInstances: any[] = [];
      clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
        /* istanbul ignore else */
        if (instance.serviceUrl && instance.addons?.includes("workflow-management")) {
          instance.isSelected = true;
          tempSelectedInstances.push(instance);
        }
        instance.childWorkflowInstances &&
          instance.childWorkflowInstances.length > 0 &&
          instance.childWorkflowInstances.forEach((childInstance: WorkflowInstance) => {
            childInstance.isSelected = false;
          });
      });
      setSelectedInstances(tempSelectedInstances);
    }
    if (selection === BulkSelectionType.PARENT_CHILD) {
      const tempSelectedInstances: any[] = [];
      if (isAllChecked && isCheckBoxClicked) {
        tempSelectedInstances.length = 0;
        clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
          if (instance.serviceUrl && instance.addons?.includes("workflow-management")) {
            instance.isSelected = false;
          }
          instance.childWorkflowInstances &&
            instance.childWorkflowInstances.length > 0 &&
            instance.childWorkflowInstances.forEach((childInstance: WorkflowInstance) => {
              if (childInstance.serviceUrl && childInstance.addons?.includes("workflow-management")) {
                if (instance.isOpen) {
                  childInstance.isSelected = false;
                }
              }
            });
        });
      } else {
        clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
          /* istanbul ignore else */
          if (instance.serviceUrl && instance.addons?.includes("workflow-management")) {
            instance.isSelected = true;
            tempSelectedInstances.push(instance);
          }

          instance.childWorkflowInstances &&
            instance.childWorkflowInstances.length > 0 &&
            instance.childWorkflowInstances.forEach((childInstance: WorkflowInstance) => {
              if (childInstance.serviceUrl && childInstance.addons?.includes("workflow-management")) {
                if (instance.isOpen) {
                  childInstance.isSelected = true;
                  tempSelectedInstances.push(childInstance);
                }
              }
            });
        });
      }
      setSelectedInstances(tempSelectedInstances);
    }
    setWorkflowInstances(clonedWorkflowInstances);
  };

  const statusMenuItems: JSX.Element[] = [
    <SelectOption key="ACTIVE" value="ACTIVE" />,
    <SelectOption key="COMPLETED" value="COMPLETED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="ABORTED" value="ABORTED" />,
    <SelectOption key="SUSPENDED" value="SUSPENDED" />,
  ];

  const checkboxItems = [
    <DropdownItem key="none" onClick={() => handleCheckboxSelectClick(BulkSelectionType.NONE, false)} id="none">
      Select none
    </DropdownItem>,
    <DropdownItem
      key="all-parent"
      onClick={() => handleCheckboxSelectClick(BulkSelectionType.PARENT, false)}
      id="all-parent"
    >
      Select all parent workflows
    </DropdownItem>,
    <DropdownItem
      key="all-parent-child"
      onClick={() => handleCheckboxSelectClick(BulkSelectionType.PARENT_CHILD, false)}
      id="all-parent-child"
    >
      Select all workflows
    </DropdownItem>,
  ];

  const dropdownItemsProcesManagementButtons = () => {
    return [
      <DropdownItem
        key="abort"
        onClick={operations[OperationType.ABORT].functions.perform}
        isDisabled={selectedInstances.length === 0}
      >
        Abort selected
      </DropdownItem>,
      <DropdownItem
        key="skip"
        onClick={operations[OperationType.SKIP].functions.perform}
        isDisabled={selectedInstances.length === 0}
      >
        Skip selected
      </DropdownItem>,
      <DropdownItem
        key="retry"
        onClick={operations[OperationType.RETRY].functions.perform}
        isDisabled={selectedInstances.length === 0}
      >
        Retry selected
      </DropdownItem>,
    ];
  };

  const buttonItems = (
    <OverflowMenu breakpoint="xl">
      <OverflowMenuContent>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.ABORT].functions.perform}
            isDisabled={selectedInstances.length === 0}
          >
            Abort selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.SKIP].functions.perform}
            isDisabled={selectedInstances.length === 0}
          >
            Skip selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.RETRY].functions.perform}
            isDisabled={selectedInstances.length === 0}
          >
            Retry selected
          </Button>
        </OverflowMenuItem>
      </OverflowMenuContent>
      <OverflowMenuControl>
        <Dropdown
          onSelect={onWorkflowManagementButtonSelect}
          toggle={<KebabToggle onToggle={onWorkflowManagementKebabToggle} />}
          isOpen={isKebabOpen}
          isPlain
          dropdownItems={dropdownItemsProcesManagementButtons()}
        />
      </OverflowMenuControl>
    </OverflowMenu>
  );

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarItem variant="bulk-select" id="bulk-select">
          <Dropdown
            position={DropdownPosition.left}
            toggle={
              <DropdownToggle
                isDisabled={filters.status.length === 0}
                onToggle={checkboxDropdownToggle}
                splitButtonItems={[
                  <DropdownToggleCheckbox
                    id="select-all-checkbox"
                    key="split-checkbox"
                    aria-label="Select all"
                    isChecked={isAllChecked}
                    onChange={() => handleCheckboxSelectClick(BulkSelectionType.PARENT_CHILD, true)}
                    isDisabled={filters.status.length === 0}
                  />,
                ]}
              >
                {selectedInstances.length === 0 ? "" : selectedInstances.length + " selected"}
              </DropdownToggle>
            }
            dropdownItems={checkboxItems}
            isOpen={isCheckboxDropdownOpen}
          />
        </ToolbarItem>
        <ToolbarFilter
          chips={filters.status}
          deleteChip={onDeleteChip}
          className="kogito-management-console__state-dropdown-list pf-u-mr-sm"
          categoryName="Status"
          id="datatoolbar-filter-status"
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={workflowStates}
            isOpen={isExpanded}
            placeholderText="Status"
            id="status-select"
          >
            {statusMenuItems}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter chips={filters.businessKey} deleteChip={onDeleteChip} categoryName={Category.BUSINESS_KEY}>
          <InputGroup>
            <TextInput
              name="businessKey"
              id="businessKey"
              type="search"
              aria-label="business key"
              onChange={setBusinessKeyInput}
              onKeyPress={onEnterClicked}
              placeholder="Filter by business key"
              value={businessKeyInput}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button variant="primary" onClick={onApplyFilter} id="apply-filter-button">
            Apply filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
      <ToolbarGroup>
        <ToolbarItem variant="separator" />
        <ToolbarGroup className="pf-u-ml-md" id="workflow-management-buttons">
          {buttonItems}
        </ToolbarGroup>
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
            <Button variant="plain" onClick={refresh} id="refresh">
              <SyncIcon />
            </Button>
          </Tooltip>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  return (
    <>
      <WorkflowInfoModal
        modalTitle={setTitle(titleType, modalTitle)}
        isModalOpen={isModalOpen}
        operationResult={operations[operationType!]}
        handleModalToggle={handleModalToggle}
        resetSelected={resetSelected}
        ouiaId="operation-result"
      />
      <Toolbar
        id="data-toolbar-with-filter"
        className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
        collapseListedFiltersBreakpoint="xl"
        clearAllFilters={resetAllFilters}
        clearFiltersButtonText="Reset to default"
        {...componentOuiaProps(ouiaId, "workflow-list-toolbar", ouiaSafe)}
      >
        <ToolbarContent>{toolbarItems}</ToolbarContent>
      </Toolbar>
    </>
  );
};

export default WorkflowListToolbar;
