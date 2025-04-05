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
import React, { useState, useEffect, useCallback } from "react";
import { sortable, IRow, ISortBy } from "@patternfly/react-table/dist/js/components/Table";
import { Table, TableHeader, TableBody } from "@patternfly/react-table/dist/js/deprecated";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import { Job, JobsSortBy } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import Moment from "react-moment";
import _ from "lodash";
import { JobsIconCreator } from "../../../utils/utils";
import { JobsManagementDriver } from "../../../api";
import { HistoryIcon } from "@patternfly/react-icons/dist/js/icons/history-icon";
import "../styles.css";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { constructObject, setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";

interface ActionsMeta {
  title: string;
  onClick: (event, rowId, rowData, extra) => void;
}
interface RowTitle {
  title: JSX.Element;
}

interface RetrievedValueType {
  tempRows: RowTitle[];
  jobType: string;
}

interface JobsManagementTableProps {
  jobs: Job[];
  driver: JobsManagementDriver;
  doQueryJobs: (offset: number, limit: number) => Promise<void>;
  handleCancelModalToggle: () => void;
  handleDetailsToggle: () => void;
  handleRescheduleToggle: () => void;
  isActionPerformed: boolean;
  isLoading: boolean;
  setIsActionPerformed: (isActionPerformed: boolean) => void;
  selectedJobInstances: Job[];
  setModalTitle: (title: JSX.Element) => void;
  setModalContent: (content: string) => void;
  setSelectedJobInstances: React.Dispatch<React.SetStateAction<Job[]>>;
  setSelectedJob: (job?: Job) => void;
  setSortBy: (sortObj: ISortBy) => void;
  setOrderBy: (orderBy: JobsSortBy) => void;
  sortBy: ISortBy;
}

const editableJobStatus: string[] = ["SCHEDULED", "ERROR"];

const JobsManagementTable: React.FC<JobsManagementTableProps & OUIAProps> = ({
  jobs,
  driver,
  doQueryJobs,
  handleCancelModalToggle,
  handleDetailsToggle,
  handleRescheduleToggle,
  isActionPerformed,
  isLoading,
  setIsActionPerformed,
  selectedJobInstances,
  setModalTitle,
  setModalContent,
  setSelectedJobInstances,
  setSelectedJob,
  setSortBy,
  sortBy,
  setOrderBy,
  ouiaId,
  ouiaSafe,
}) => {
  const [rows, setRows] = useState<IRow[]>([]);

  const checkNotEmpty = useCallback(() => {
    return jobs && jobs.length > 0 && !isLoading;
  }, [jobs, isLoading]);

  const [columns, setColumns] = useState<Array<{ title: string }>>([
    { title: "Id" },
    { title: "Status" },
    { title: "Expiration time" },
    { title: "Retries" },
    { title: "Execution counter" },
    { title: "Last update" },
  ]);

  useEffect(() => {
    setColumns((currentColumns) => {
      return [...currentColumns].map((column) => {
        column["props"] = { className: "pf-v5-u-text-align-center" };
        checkNotEmpty() && column.title !== "Id" ? (column["transforms"] = [sortable]) : "";
        return column;
      });
    });
  }, [checkNotEmpty]);

  const getValues = useCallback((job): RetrievedValueType => {
    const tempRows: RowTitle[] = [];
    let jobType: string = "";
    for (const item in job) {
      if (item === "id") {
        const ele = {
          title: (
            <Tooltip content={job.id}>
              <span>{job.id.substring(0, 7)}</span>
            </Tooltip>
          ),
        };
        tempRows.push(ele);
      } else if (item === "status") {
        const ele = {
          title: JobsIconCreator(job.status),
        };
        if (editableJobStatus.includes(job[item])) {
          jobType = "Editable";
        } else {
          jobType = "Non-editable";
        }
        tempRows.push(ele);
      } else if (item === "expirationTime") {
        const ele = {
          title: (
            <React.Fragment>
              {job.expirationTime ? (
                <>
                  {" "}
                  expires in{" "}
                  <Moment fromNow ago>
                    {job.expirationTime}
                  </Moment>
                </>
              ) : (
                "N/A"
              )}
            </React.Fragment>
          ),
        };
        tempRows.push(ele);
      } else if (item === "lastUpdate") {
        const ele = {
          title: (
            <>
              <HistoryIcon className="pf-v5-u-mr-sm" /> Updated <Moment fromNow>{job.lastUpdate}</Moment>
            </>
          ),
        };
        tempRows.push(ele);
      } else {
        const ele = {
          title: <span>{job[item]}</span>,
        };
        tempRows.push(ele);
      }
    }
    return { tempRows, jobType };
  }, []);

  const onSelect = useCallback(
    (_event, isSelected, rowId, _rowData): void => {
      if (!checkNotEmpty()) {
        return;
      }
      setIsActionPerformed(false);
      setRows((currentRows) => {
        const copyOfRows = [...currentRows];
        if (rowId === -1) {
          copyOfRows.forEach((row) => {
            row.selected = isSelected;
            return row;
          });
          setSelectedJobInstances((currentSelectedJobInstances) => {
            if (currentSelectedJobInstances.length === jobs.length) {
              return [];
            } else if (currentSelectedJobInstances.length < jobs.length) {
              return _.cloneDeep(jobs);
            }
            return currentSelectedJobInstances;
          });
        } else {
          if (copyOfRows[rowId]) {
            copyOfRows[rowId].selected = isSelected;
            const row = [...jobs].filter((job) => job.id === copyOfRows[rowId].rowKey);
            setSelectedJobInstances((currentSelectedJobInstances) => {
              const rowData = _.find(currentSelectedJobInstances, ["id", copyOfRows[rowId].rowKey]);
              if (rowData === undefined) {
                return [...currentSelectedJobInstances, row[0]];
              } else {
                const copyOfSelectedJobInstances = [...currentSelectedJobInstances];
                _.remove(copyOfSelectedJobInstances, (job) => job.id === copyOfRows[rowId].rowKey);
                return copyOfSelectedJobInstances;
              }
            });
          }
        }
        return copyOfRows;
      });
    },
    [checkNotEmpty, jobs, setIsActionPerformed, setSelectedJobInstances]
  );

  const tableContent = useCallback(
    (jobs): void => {
      const jobRow: IRow[] = [];
      if (!isLoading && !_.isEmpty(jobs)) {
        jobs.map((job) => {
          const retrievedValue = getValues(
            _.pick(job, ["id", "status", "expirationTime", "retries", "executionCounter", "lastUpdate"])
          );
          jobRow.push({
            cells: retrievedValue.tempRows,
            type: retrievedValue.jobType,
            rowKey: job.id,
            selected: false,
          });
        });
      }
      if (isLoading) {
        const tempRows = [
          {
            rowKey: "1",
            cells: [
              {
                props: { colSpan: 8 },
                title: <KogitoSpinner spinnerText={"Loading jobs list..."} />,
              },
            ],
          },
        ];
        setRows(tempRows);
      } else {
        if (jobRow.length === 0) {
          const tempRows = [
            {
              rowKey: "1",
              cells: [
                {
                  props: { colSpan: 8 },
                  title: (
                    <KogitoEmptyState
                      type={KogitoEmptyStateType.Search}
                      title="No results found"
                      body="Try using different filters"
                    />
                  ),
                },
              ],
            },
          ];
          setRows(tempRows);
        } else {
          setRows((prev) => [...prev, ...jobRow]);
        }
      }
    },
    [getValues, isLoading]
  );

  const handleJobDetails = useCallback(
    (id): void => {
      const job = jobs.find((job) => job.id === id);
      setSelectedJob(job);
      handleDetailsToggle();
    },
    [handleDetailsToggle, jobs, setSelectedJob]
  );

  const handleJobReschedule = useCallback(
    (id): void => {
      const job = jobs.find((job) => job.id === id);
      setSelectedJob(job);
      handleRescheduleToggle();
    },
    [handleRescheduleToggle, jobs, setSelectedJob]
  );

  const handleCancelAction = useCallback(
    async (id): Promise<void> => {
      const job: any = jobs.find((job) => job.id === id);
      const cancelResponse = await driver.cancelJob(job);
      const title: JSX.Element = setTitle(cancelResponse.modalTitle, "Job cancel");
      setModalTitle(title);
      setModalContent(cancelResponse.modalContent);
      handleCancelModalToggle();
    },
    [driver, handleCancelModalToggle, jobs, setModalContent, setModalTitle]
  );

  const dynamicActions = useCallback(
    (rowData) => {
      if (rowData.type === "Editable") {
        return [
          {
            title: "Reschedule",
            onClick: (_event, _rowId, rowData, _extra) => handleJobReschedule(rowData.rowKey),
          },
          {
            title: "Cancel",
            onClick: (_event, _rowId, rowData, _extra) => handleCancelAction(rowData.rowKey),
          },
        ];
      } else {
        return [];
      }
    },
    [handleCancelAction, handleJobReschedule]
  );

  const actionResolver = useCallback(
    (rowData): ActionsMeta[] => {
      if (!checkNotEmpty()) {
        return [];
      }
      const editActions = dynamicActions(rowData);
      return [
        {
          title: "Details",
          onClick: (event, rowId, rowData, extra) => handleJobDetails(rowData.rowKey),
        },
        ...editActions,
      ];
    },
    [checkNotEmpty, dynamicActions, handleJobDetails]
  );

  const onSort = useCallback(
    async (event, index: number, direction: "asc" | "desc"): Promise<void> => {
      setSortBy({ index, direction });
      let sortingColumn: string = event.target.innerText;
      sortingColumn = _.camelCase(sortingColumn);
      const obj: JobsSortBy = {};
      constructObject(obj, sortingColumn, direction.toUpperCase());
      setOrderBy(obj);
      await driver.sortBy(obj);
      await doQueryJobs(0, 10);
    },
    [doQueryJobs, driver, setOrderBy, setSortBy]
  );

  useEffect(() => {
    if (isActionPerformed) {
      setSelectedJobInstances([]);
      setRows((curentRows) => {
        const updatedRows = curentRows.filter((row) => {
          row.selected = false;
          return row;
        });
        return updatedRows;
      });
    }
  }, [isActionPerformed, setSelectedJobInstances]);

  useEffect(() => {
    setRows([]);
    tableContent(jobs);
  }, [isLoading, jobs, tableContent]);

  return (
    <Table
      cells={columns}
      rows={rows}
      onSelect={checkNotEmpty() ? onSelect : undefined}
      actionResolver={checkNotEmpty() ? actionResolver : undefined}
      sortBy={sortBy}
      onSort={onSort}
      aria-label="Jobs management Table"
      className="kogito-jobs-management__table"
      {...componentOuiaProps(ouiaId, "jobs-management-table", ouiaSafe)}
    >
      <TableHeader />
      <TableBody />
    </Table>
  );
};

export default JobsManagementTable;
