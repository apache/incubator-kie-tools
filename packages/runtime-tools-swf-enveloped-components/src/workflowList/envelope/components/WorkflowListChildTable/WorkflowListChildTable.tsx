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

import { WorkflowListDriver } from "../../../api";
import { ICell, IRow, IRowCell, Table, TableBody, TableHeader } from "@patternfly/react-table/dist/js/components/Table";
import React, { useEffect, useState } from "react";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import _ from "lodash";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";
import { EndpointLink } from "@kie-tools/runtime-tools-components/dist/components/EndpointLink";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { getWorkflowInstanceDescription, WorkflowInstanceIconCreator } from "../utils/WorkflowListUtils";
import { HistoryIcon } from "@patternfly/react-icons/dist/js/icons/history-icon";
import Moment from "react-moment";
import WorkflowListActionsKebab from "../WorkflowListActionsKebab/WorkflowListActionsKebab";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import DisablePopup from "../DisablePopup/DisablePopup";
import ErrorPopover from "../ErrorPopover/ErrorPopover";
import "../styles.css";
export interface WorkflowListChildTableProps {
  parentWorkflowId: string;
  workflowInstances: WorkflowInstance[];
  setWorkflowInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  selectedInstances: WorkflowInstance[];
  setSelectedInstances: React.Dispatch<React.SetStateAction<WorkflowInstance[]>>;
  driver: WorkflowListDriver;
  onSkipClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onRetryClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onAbortClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  setSelectableInstances: React.Dispatch<React.SetStateAction<number>>;
}
const WorkflowListChildTable: React.FC<WorkflowListChildTableProps & OUIAProps> = ({
  parentWorkflowId,
  selectedInstances,
  setSelectedInstances,
  workflowInstances,
  setWorkflowInstances,
  driver,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  setSelectableInstances,
  ouiaId,
  ouiaSafe,
}) => {
  const [rows, setRows] = useState<(IRow | string[])[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [showNoDataEmptyState, setShowNoDataEmptyState] = useState<boolean>(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const columnNames: string[] = ["__Select", "Id", "Status", "Created", "Last update", "__Actions"];
  const columns: ICell[] = columnNames.map((it) => ({
    title: it.startsWith("__") ? "" : it,
  }));

  const handleClick = (childWorkflowInstance: WorkflowInstance): void => {
    driver.openWorkflow(childWorkflowInstance);
  };

  const checkBoxSelect = (workflowInstance: WorkflowInstance): void => {
    const clonedWorkflowInstances = [...workflowInstances];
    clonedWorkflowInstances.forEach((instance: WorkflowInstance) => {
      if (instance.id === parentWorkflowId) {
        instance.childWorkflowInstances?.forEach((childInstance: WorkflowInstance) => {
          if (childInstance.id === workflowInstance.id) {
            if (childInstance.isSelected) {
              childInstance.isSelected = false;
              setSelectedInstances(
                selectedInstances.filter((selectedInstance) => selectedInstance.id !== childInstance.id)
              );
            } else {
              childInstance.isSelected = true;
              setSelectedInstances([...selectedInstances, childInstance]);
            }
          }
        });
      }
    });
    setWorkflowInstances(clonedWorkflowInstances);
  };

  const createRows = (childWorkflowInstances: WorkflowInstance[]): void => {
    if (!_.isEmpty(childWorkflowInstances)) {
      const tempRows: IRow[] = [];
      childWorkflowInstances.forEach((child: WorkflowInstance) => {
        const cells: IRowCell[] = [
          {
            title: (
              <>
                {child.addons?.includes("workflow-management") && child.serviceUrl !== null ? (
                  <Checkbox
                    isChecked={child.isSelected}
                    onChange={() => {
                      checkBoxSelect(child);
                    }}
                    aria-label="workflow-list-checkbox"
                    id={`checkbox-${child.id}`}
                    name={`checkbox-${child.id}`}
                  />
                ) : (
                  <DisablePopup
                    workflowInstanceData={child}
                    component={
                      <Checkbox
                        aria-label="workflow-list-checkbox-disabled"
                        id={`checkbox-${child.id}`}
                        isDisabled={true}
                      />
                    }
                  />
                )}
              </>
            ),
          },
          {
            title: (
              <>
                <a
                  className="kogito-workflow-list__link"
                  onClick={() => handleClick(child)}
                  {...componentOuiaProps(ouiaId, "workflow-description", ouiaSafe)}
                >
                  <strong>
                    <ItemDescriptor itemDescription={getWorkflowInstanceDescription(child)} />
                  </strong>
                </a>
                <EndpointLink serviceUrl={child.serviceUrl} isLinkShown={false} />
              </>
            ),
          },
          {
            title:
              child.state === WorkflowInstanceState.Error ? (
                <ErrorPopover workflowInstanceData={child} onSkipClick={onSkipClick} onRetryClick={onRetryClick} />
              ) : (
                WorkflowInstanceIconCreator(child.state)
              ),
          },
          {
            title: child.start ? <Moment fromNow>{new Date(`${child.start}`)}</Moment> : "",
          },
          {
            title: child.lastUpdate ? (
              <span>
                {" "}
                <HistoryIcon className="pf-u-mr-sm" /> Updated{" "}
                <Moment fromNow>{new Date(`${child.lastUpdate}`)}</Moment>
              </span>
            ) : (
              ""
            ),
          },
          {
            title: (
              <WorkflowListActionsKebab
                workflowInstance={child}
                onSkipClick={onSkipClick}
                onRetryClick={onRetryClick}
                onAbortClick={onAbortClick}
                key={child.id}
              />
            ),
          },
        ];
        cells.forEach((cellInRow, index) => {
          cellInRow.props = componentOuiaProps(columnNames[index].toLowerCase(), "workflow-list-cell", true);
        });
        tempRows.push({
          // props are not passed to the actual <tr> element (to set OUIA attributes).
          // Seems that only solution is to use TableComposable instead.
          cells: cells,
        });
      });
      setRows(tempRows);
      setShowNoDataEmptyState(false);
    } else {
      setShowNoDataEmptyState(true);
    }
  };

  const getChildWorkflowInstances = async (): Promise<void> => {
    try {
      setIsLoading(true);
      const response: WorkflowInstance[] = await driver.getChildWorkflowsQuery(parentWorkflowId);
      workflowInstances.forEach((workflowInstance: WorkflowInstance) => {
        if (workflowInstance.id === parentWorkflowId) {
          response.forEach((child: WorkflowInstance) => {
            child.isSelected = false;
            if (child.serviceUrl && child.addons?.includes("workflow-management")) {
              setSelectableInstances((prev) => prev + 1);
            }
          });
          workflowInstance.childWorkflowInstances = response;
        }
      });
      createRows(response);
    } catch (error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (workflowInstances.length > 0) {
      const workflowInstance: WorkflowInstance | undefined = workflowInstances.find(
        (instance: WorkflowInstance) => instance.id === parentWorkflowId
      );
      createRows(workflowInstance?.childWorkflowInstances ?? []);
    }
  }, [workflowInstances]);

  useEffect(() => {
    getChildWorkflowInstances();
  }, []);

  if (isLoading) {
    return <KogitoSpinner spinnerText={"Loading child instances..."} />;
  }

  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }

  if (!isLoading && showNoDataEmptyState) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Info}
        title={`No child workflow instances`}
        body={`This workflow has no related sub workflows`}
      />
    );
  }

  return (
    <Table
      aria-label="Workflow List Child Table"
      cells={columns}
      rows={rows}
      variant={"compact"}
      className="kogito-management-console__compact-table"
      {...componentOuiaProps(ouiaId, "workflow-list-child-table", ouiaSafe ? ouiaSafe : !isLoading)}
    >
      <TableHeader />
      <TableBody />
    </Table>
  );
};

export default WorkflowListChildTable;
