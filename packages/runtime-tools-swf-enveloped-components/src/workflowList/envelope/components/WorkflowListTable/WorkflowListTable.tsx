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

import React, { useCallback, useEffect, useState } from "react";
import { ExpandableRowContent } from "@patternfly/react-table/dist/js/components/Table";
import { Thead, Tbody, Tr, Th, Td } from "@patternfly/react-table/dist/js/components/Table";
import { Table } from "@patternfly/react-table/deprecated";
import _ from "lodash";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { WorkflowInfoModal } from "@kie-tools/runtime-tools-components/dist/components/WorkflowInfoModal";
import WorkflowListChildTable from "../WorkflowListChildTable/WorkflowListChildTable";
import { EndpointLink } from "@kie-tools/runtime-tools-components/dist/components/EndpointLink";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { HistoryIcon } from "@patternfly/react-icons/dist/js/icons/history-icon";
import Moment from "react-moment";
import { getWorkflowInstanceDescription, WorkflowInstanceIconCreator } from "../utils/WorkflowListUtils";
import { WorkflowListDriver } from "../../../api";
import WorkflowListActionsKebab from "../WorkflowListActionsKebab/WorkflowListActionsKebab";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import DisablePopup from "../DisablePopup/DisablePopup";
import "../styles.css";
import ErrorPopover from "../ErrorPopover/ErrorPopover";
import { TitleType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface WorkflowListTableProps {
  workflowInstances: WorkflowInstance[];
  isLoading: boolean;
  expanded: {
    [key: number]: boolean;
  };
  setExpanded: React.Dispatch<
    React.SetStateAction<{
      [key: number]: boolean;
    }>
  >;
  driver: WorkflowListDriver;
  onSort: (event: React.SyntheticEvent<EventTarget>, index: number, direction: "desc" | "asc") => void;
  sortBy: any;
  setWorkflowInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  selectedInstances: WorkflowInstance[];
  setSelectedInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  selectableInstances: number;
  setSelectableInstances: React.Dispatch<React.SetStateAction<number>>;
  setIsAllChecked: React.Dispatch<React.SetStateAction<boolean>>;
}

const WorkflowListTable: React.FC<WorkflowListTableProps & OUIAProps> = ({
  isLoading,
  expanded,
  setExpanded,
  sortBy,
  onSort,
  workflowInstances,
  setWorkflowInstances,
  selectedInstances,
  setSelectedInstances,
  selectableInstances,
  setSelectableInstances,
  setIsAllChecked,
  driver,
  ouiaId,
  ouiaSafe,
}) => {
  const [rowPairs, setRowPairs] = useState<any>([]);
  const columns: string[] = ["__Toggle", "__Select", "Process name", "Status", "Created", "Last update", "__Actions"];
  const [modalTitle, setModalTitle] = useState<string>("");
  const [modalContent, setModalContent] = useState<string>("");
  const [titleType, setTitleType] = useState<string>("");
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [selectedWorkflowInstance, setSelectedWorkflowInstance] = useState<WorkflowInstance | null>(null);

  const handleModalToggle = (): void => {
    setIsModalOpen(!isModalOpen);
  };
  const onShowMessage = (title: string, content: string, type: TitleType, workflowInstance: WorkflowInstance): void => {
    setSelectedWorkflowInstance(workflowInstance);
    setTitleType(type);
    setModalTitle(title);
    setModalContent(content);
    handleModalToggle();
  };

  const onSkipClick = async (workflowInstance: WorkflowInstance): Promise<void> => {
    try {
      await driver.handleWorkflowSkip(workflowInstance);
      onShowMessage(
        "Skip operation",
        `The workflows ${workflowInstance.processName} was successfully skipped.`,
        TitleType.SUCCESS,
        workflowInstance
      );
    } catch (error) {
      onShowMessage(
        "Skip operation",
        `The workflow ${workflowInstance.processName} failed to skip. Message: ${error.message}`,
        TitleType.FAILURE,
        workflowInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const onRetryClick = async (workflowInstance: WorkflowInstance): Promise<void> => {
    try {
      await driver.handleWorkflowRetry(workflowInstance);
      onShowMessage(
        "Retry operation",
        `The workflow ${workflowInstance.processName} was successfully re-executed.`,
        TitleType.SUCCESS,
        workflowInstance
      );
    } catch (error) {
      onShowMessage(
        "Retry operation",
        `The workflow ${workflowInstance.processName} failed to re-execute. Message: ${error.message}`,
        TitleType.FAILURE,
        workflowInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const onAbortClick = async (workflowInstance: WorkflowInstance): Promise<void> => {
    try {
      await driver.handleWorkflowAbort(workflowInstance);
      onShowMessage(
        "Abort operation",
        `The workflow ${workflowInstance.processName} was successfully aborted.`,
        TitleType.SUCCESS,
        workflowInstance
      );
      workflowInstances.forEach((instance) => {
        if (instance.id === workflowInstance.id) {
          instance.state = WorkflowInstanceState.Aborted;
        }
      });
      setWorkflowInstances([...workflowInstances]);
    } catch (error) {
      onShowMessage(
        "Abort operation",
        `Failed to abort workflow ${workflowInstance.processName}. Message: ${error.message}`,
        TitleType.FAILURE,
        workflowInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const onOpenTriggerCloudEvent = useCallback(
    (workflowInstance: WorkflowInstance) => {
      return (instance: WorkflowInstance) => driver.openTriggerCloudEvent(instance);
    },
    [driver]
  );

  const handleClick = (workflowInstance: WorkflowInstance): void => {
    driver.openWorkflow(workflowInstance);
  };

  useEffect(() => {
    if (!_.isEmpty(workflowInstances)) {
      const tempRows: any[] = [];
      workflowInstances.forEach((workflowInstance: WorkflowInstance) => {
        tempRows.push({
          id: workflowInstance.id,
          parent: [
            <>
              {workflowInstance.addons?.includes("workflow-management") && workflowInstance.serviceUrl !== null ? (
                <Checkbox
                  isChecked={workflowInstance.isSelected}
                  onChange={() => checkBoxSelect(workflowInstance)}
                  aria-label="workflow-list-checkbox"
                  id={`checkbox-${workflowInstance.id}`}
                  name={`checkbox-${workflowInstance.id}`}
                />
              ) : (
                <DisablePopup
                  workflowInstanceData={workflowInstance}
                  component={
                    <Checkbox
                      aria-label="workflow-list-checkbox-disabled"
                      id={`checkbox-${workflowInstance.id}`}
                      isDisabled={true}
                    />
                  }
                />
              )}
            </>,
            <>
              <a
                className="kogito-workflow-list__link"
                onClick={() => handleClick(workflowInstance)}
                {...componentOuiaProps(ouiaId, "workflow-description", ouiaSafe)}
              >
                <strong>
                  <ItemDescriptor itemDescription={getWorkflowInstanceDescription(workflowInstance)} />
                </strong>
              </a>
              <EndpointLink serviceUrl={workflowInstance.serviceUrl} isLinkShown={false} />
            </>,
            workflowInstance.state === WorkflowInstanceState.Error ? (
              <ErrorPopover
                workflowInstanceData={workflowInstance}
                onSkipClick={onSkipClick}
                onRetryClick={onRetryClick}
              />
            ) : (
              WorkflowInstanceIconCreator(workflowInstance.state)
            ),
            workflowInstance.start ? <Moment fromNow>{new Date(`${workflowInstance.start}`)}</Moment> : "",
            workflowInstance.lastUpdate ? (
              <span>
                <HistoryIcon className="pf-v5-u-mr-sm" /> {"Updated "}
                <Moment fromNow>{new Date(`${workflowInstance.lastUpdate}`)}</Moment>
              </span>
            ) : (
              ""
            ),
            <WorkflowListActionsKebab
              workflowInstance={workflowInstance}
              onSkipClick={onSkipClick}
              onRetryClick={onRetryClick}
              onAbortClick={onAbortClick}
              onOpenTriggerCloudEvent={onOpenTriggerCloudEvent}
              key={workflowInstance.id}
            />,
          ],
          child: [workflowInstance.id],
        });
      });
      setRowPairs(tempRows);
    } else {
      setRowPairs([]);
    }
  }, [workflowInstances]);

  const loadChild = (parentId: string, parentIndex: number): JSX.Element | null => {
    if (!expanded[parentIndex]) {
      return null;
    } else {
      return (
        <WorkflowListChildTable
          parentWorkflowId={parentId}
          workflowInstances={workflowInstances}
          setWorkflowInstances={setWorkflowInstances}
          selectedInstances={selectedInstances}
          setSelectedInstances={setSelectedInstances}
          setSelectableInstances={setSelectableInstances}
          driver={driver}
          onSkipClick={onSkipClick}
          onRetryClick={onRetryClick}
          onAbortClick={onAbortClick}
          ouiaId={parentId}
        />
      );
    }
  };

  const checkBoxSelect = (workflowInstance: WorkflowInstance): void => {
    const clonedWorkflowInstances = [...workflowInstances];
    clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
      if (workflowInstance.id === instance.id) {
        if (instance.isSelected) {
          instance.isSelected = false;
          setSelectedInstances(selectedInstances.filter((selectedInstance) => selectedInstance.id !== instance.id));
        } else {
          instance.isSelected = true;
          setSelectedInstances([...selectedInstances, instance]);
        }
      }
    });
    setWorkflowInstances(clonedWorkflowInstances);
  };

  const onToggle = (pairIndex: number, pair: any): void => {
    setExpanded({
      ...expanded,
      [pairIndex]: !expanded[pairIndex],
    });

    if (expanded[pairIndex]) {
      const workflowInstance: WorkflowInstance | undefined = workflowInstances.find(
        (instance) => instance.id === pair.id
      );
      workflowInstance?.childWorkflowInstances?.forEach((childInstance: WorkflowInstance) => {
        if (childInstance.isSelected) {
          const index = selectedInstances.findIndex((selectedInstance) => selectedInstance.id === childInstance.id);
          if (index !== -1) {
            selectedInstances.splice(index, 1);
          }
        }
      });
      workflowInstances.forEach((instance: WorkflowInstance) => {
        if (workflowInstance?.id === instance.id) {
          instance.isOpen = false;
          instance.childWorkflowInstances?.forEach((child: WorkflowInstance) => {
            if (child.serviceUrl && child.addons?.includes("workflow-management")) {
              setSelectableInstances((prev) => prev - 1);
            }
          });
        }
      });
    } else {
      const workflowInstance = !_.isEmpty(workflowInstances)
        ? workflowInstances.find((instance) => instance.id === pair.id)
        : undefined;
      !_.isEmpty(workflowInstances) &&
        workflowInstances.forEach((instance: WorkflowInstance) => {
          if (workflowInstance?.id === instance.id) {
            instance.isOpen = true;
          }
        });
    }
    if (selectedInstances.length === selectableInstances && selectableInstances !== 0) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  };

  return (
    <React.Fragment>
      <WorkflowInfoModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalTitle={setTitle(titleType, modalTitle)}
        modalContent={modalContent}
        workflowName={selectedWorkflowInstance?.processName}
        ouiaId={selectedWorkflowInstance ? "workflow-" + selectedWorkflowInstance.id : undefined}
      />
      <Table
        aria-label="Workflow List Table"
        {...componentOuiaProps(ouiaId, "workflow-list-table", ouiaSafe ? ouiaSafe : !isLoading)}
      >
        <Thead>
          <Tr ouiaId="workflow-list-table-header">
            {columns.map((column, columnIndex) => {
              let sortParams = {};
              if (!isLoading && rowPairs.length > 0) {
                sortParams = {
                  sort: {
                    sortBy,
                    onSort,
                    columnIndex,
                  },
                };
              }
              let styleParams;
              switch (columnIndex) {
                case 0:
                  styleParams = { width: "72px" };
                  sortParams = {};
                  break;
                case 1:
                  styleParams = { width: "86px" };
                  sortParams = {};
                  break;
                case columns.length - 1:
                  styleParams = { width: "188px" };
                  sortParams = {};
                  break;
              }
              return (
                <Th style={styleParams} key={`${column}_header`} {...sortParams}>
                  {column.startsWith("__") ? "" : column}
                </Th>
              );
            })}
          </Tr>
        </Thead>
        {!isLoading && !_.isEmpty(rowPairs) ? (
          rowPairs.map((pair: any, pairIndex: any) => {
            const parentRow = (
              <Tr key={`${pair.id}-parent`} {...componentOuiaProps(pair.id, "workflow-list-row", true)}>
                <Td
                  key={`${pair.id}-parent-0`}
                  expand={{
                    rowIndex: pairIndex,
                    isExpanded: expanded[pairIndex],
                    onToggle: () => onToggle(pairIndex, pair),
                  }}
                  {...componentOuiaProps(columns[0].toLowerCase(), "workflow-list-cell", true)}
                />
                {pair.parent.map((cell: any, cellIndex: any) => (
                  <Td
                    key={`${pair.id}-parent-${columns[cellIndex + 1]}`}
                    dataLabel={columns[cellIndex + 1]}
                    {...componentOuiaProps(columns[cellIndex + 1].toLowerCase(), "workflow-list-cell", true)}
                  >
                    {cell}
                  </Td>
                ))}
              </Tr>
            );
            const childRow = (
              <Tr
                key={`${pair.id}-child`}
                isExpanded={expanded[pairIndex] === true}
                {...componentOuiaProps(pair.id, "workflow-list-row-expanded", true)}
              >
                <Td key={`${pair.id}-child-0`} />
                {rowPairs[pairIndex].child.map((cell: any, cellIndex: any) => (
                  <Td
                    key={`${pair.id}-child-${columns[++cellIndex]}`}
                    dataLabel={columns[cellIndex]}
                    noPadding={rowPairs[pairIndex].noPadding}
                    colSpan={6}
                  >
                    <ExpandableRowContent>{loadChild(cell, pairIndex)}</ExpandableRowContent>
                  </Td>
                ))}
              </Tr>
            );
            return (
              <Tbody key={`${pair.id}_tBody`}>
                {parentRow}
                {childRow}
              </Tbody>
            );
          })
        ) : (
          <tbody>
            <Tr>
              <Td colSpan={7}>
                <>
                  {isLoading && rowPairs.length === 0 && (
                    <KogitoSpinner spinnerText={"Loading workflow instances..."} />
                  )}
                  {!isLoading && rowPairs.length === 0 && (
                    <KogitoEmptyState
                      type={KogitoEmptyStateType.Search}
                      title="No results found"
                      body="Try using different filters"
                    />
                  )}
                </>
              </Td>
            </Tr>
          </tbody>
        )}
      </Table>
    </React.Fragment>
  );
};

export default WorkflowListTable;
