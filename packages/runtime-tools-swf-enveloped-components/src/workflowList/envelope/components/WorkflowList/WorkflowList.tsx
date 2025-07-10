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

import React, { useEffect, useState } from "react";
import { WorkflowListDriver } from "../../../api";
import {
  WorkflowInstance,
  WorkflowInstanceState,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
  WorkflowListState,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import WorkflowListTable from "../WorkflowListTable/WorkflowListTable";
import WorkflowListToolbar from "../WorkflowListToolbar/WorkflowListToolbar";
import { LoadMore } from "@kie-tools/runtime-tools-components/dist/components/LoadMore";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ISortBy } from "@patternfly/react-table/dist/js/components/Table";
import _ from "lodash";
import { alterOrderByObj, workflowListDefaultStatusFilter } from "../utils/WorkflowListUtils";

import "../styles.css";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface WorkflowListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: WorkflowListDriver;
  initialState: WorkflowListState;
}
const WorkflowList: React.FC<WorkflowListProps & OUIAProps> = ({
  driver,
  isEnvelopeConnectedToChannel,
  initialState,
  ouiaId,
  ouiaSafe,
}) => {
  const defaultStatusFilter = workflowListDefaultStatusFilter;

  const defaultFilters: WorkflowInstanceFilter =
    initialState && initialState.filters
      ? { ...initialState.filters }
      : {
          status: defaultStatusFilter,
          businessKey: [],
        };
  const defaultOrderBy: any =
    initialState && initialState.sortBy
      ? initialState.sortBy
      : {
          lastUpdate: OrderBy.DESC,
        };
  const [defaultPageSize] = useState<number>(10);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [offset, setOffset] = useState<number>(0);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [workflowInstances, setWorkflowInstances] = useState<WorkflowInstance[]>([]);
  const [error, setError] = useState<string | undefined>(undefined);
  const [filters, setFilters] = useState<WorkflowInstanceFilter>(defaultFilters);
  const [workflowStates, setWorkflowStates] = useState<WorkflowInstanceState[]>(defaultStatusFilter);
  const [expanded, setExpanded] = React.useState<{ [key: number]: boolean }>({});
  const [sortBy, setSortBy] = useState<WorkflowListSortBy | ISortBy>(defaultOrderBy);
  const [selectedInstances, setSelectedInstances] = useState<WorkflowInstance[]>([]);
  const [selectableInstances, setSelectableInstances] = useState<number>(0);
  const [isAllChecked, setIsAllChecked] = useState<boolean>(false);

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      initLoad();
    }
  }, [isEnvelopeConnectedToChannel]);

  useEffect(() => {
    setIsLoading(true);
    if (initialState && initialState.filters) {
      setFilters(initialState.filters);
      setWorkflowStates(initialState.filters.status);
      setSortBy(initialState.sortBy);
    }
  }, [initialState]);

  const initLoad = async () => {
    setIsLoading(true);
    setFilters(defaultFilters);
    await driver.initialLoad(defaultFilters, defaultOrderBy);
    doQuery(0, 10, true);
  };

  const countExpandableRows = (instances: WorkflowInstance[]): void => {
    instances.forEach((workflowInstance, index) => {
      expanded[index] = false;
      workflowInstance.isSelected = false;
      workflowInstance.isOpen = false;
      workflowInstance.childWorkflowInstances = [];
      if (workflowInstance.serviceUrl && workflowInstance.addons?.includes("workflow-management")) {
        setSelectableInstances((prev) => prev + 1);
      }
    });
  };

  const doQuery = async (
    _offset: number,
    _limit: number,
    _resetWorkflows: boolean,
    _resetPagination: boolean = false,
    _loadMore: boolean = false
  ): Promise<void> => {
    setIsLoadingMore(_loadMore);
    setSelectableInstances(0);
    setSelectedInstances([]);
    try {
      const response: WorkflowInstance[] = await driver.query(_offset, _limit);
      setLimit(response.length);
      if (_resetWorkflows) {
        countExpandableRows(response);
        setWorkflowInstances(response);
      } else {
        const newData = workflowInstances.concat(response);
        countExpandableRows(newData);
        setWorkflowInstances(newData);
      }
      if (_resetPagination) {
        setOffset(_offset);
      }
    } catch (err) {
      setError(err.errorMessage);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  };

  useEffect(() => {
    if (selectedInstances.length === selectableInstances && selectableInstances !== 0) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  }, [workflowInstances]);

  const applyFilter = async (filter: WorkflowInstanceFilter): Promise<void> => {
    setIsLoading(true);
    setWorkflowInstances([]);
    await driver.applyFilter(filter);
    doQuery(0, defaultPageSize, true, true);
  };

  const applySorting = async (event: any, index: number, direction: "asc" | "desc") => {
    setIsLoading(true);
    setWorkflowInstances([]);
    setSortBy({ index, direction });
    let sortingColumn: string = event.target.innerText;
    sortingColumn = _.camelCase(sortingColumn);
    let sortByObj = _.set({}, sortingColumn, direction.toUpperCase());
    sortByObj = alterOrderByObj(sortByObj);
    await driver.applySorting(sortByObj);
    doQuery(0, defaultPageSize, true, true);
  };

  const doRefresh = async (): Promise<void> => {
    setIsLoading(true);
    setWorkflowInstances([]);
    doQuery(0, defaultPageSize, true, true);
  };

  const doResetFilters = (): void => {
    const resetFilter = {
      status: defaultStatusFilter,
      businessKey: [],
    };
    setIsLoading(true);
    setWorkflowStates(defaultStatusFilter);
    setFilters(resetFilter);
    applyFilter(resetFilter);
  };

  const mustShowLoadMore =
    (!isLoading || isLoadingMore) && workflowInstances && limit === pageSize && filters.status.length > 0;

  if (error) {
    return <ServerErrors error={error} variant={"large"} />;
  }

  return (
    <div {...componentOuiaProps(ouiaId, "workflow-list", ouiaSafe ? ouiaSafe : !isLoading)}>
      <WorkflowListToolbar
        applyFilter={applyFilter}
        refresh={doRefresh}
        filters={filters}
        setFilters={setFilters}
        workflowStates={workflowStates}
        setWorkflowStates={setWorkflowStates}
        selectedInstances={selectedInstances}
        setSelectedInstances={setSelectedInstances}
        workflowInstances={workflowInstances}
        setWorkflowInstances={setWorkflowInstances}
        isAllChecked={isAllChecked}
        setIsAllChecked={setIsAllChecked}
        driver={driver}
        defaultStatusFilter={defaultStatusFilter}
      />
      {filters.status.length > 0 ? (
        <>
          <WorkflowListTable
            workflowInstances={workflowInstances}
            isLoading={isLoading}
            expanded={expanded}
            setExpanded={setExpanded}
            driver={driver}
            onSort={applySorting}
            sortBy={sortBy}
            setWorkflowInstances={setWorkflowInstances}
            selectedInstances={selectedInstances}
            setSelectedInstances={setSelectedInstances}
            selectableInstances={selectableInstances}
            setSelectableInstances={setSelectableInstances}
            setIsAllChecked={setIsAllChecked}
          />
          {mustShowLoadMore && (
            <LoadMore
              offset={offset}
              setOffset={setOffset}
              getMoreItems={(_offset: number, _limit: any) => {
                setPageSize(_limit);
                doQuery(_offset, _limit, false, true, true);
              }}
              pageSize={pageSize}
              isLoadingMore={isLoadingMore}
            />
          )}
        </>
      ) : (
        <div className="kogito-workflow-list__emptyState-card">
          <KogitoEmptyState
            type={KogitoEmptyStateType.Reset}
            title="No filters applied."
            body="Try applying at least one filter to see results"
            onClick={doResetFilters}
          />
        </div>
      )}
    </div>
  );
};

export default WorkflowList;
