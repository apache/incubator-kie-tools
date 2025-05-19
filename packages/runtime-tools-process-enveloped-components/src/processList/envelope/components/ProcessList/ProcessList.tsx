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
import React, { useCallback, useEffect, useState, useMemo } from "react";
import { ProcessListChannelApi, ProcessListState } from "../../../api";
import ProcessListTable from "../ProcessListTable/ProcessListTable";
import ProcessListToolbar from "../ProcessListToolbar/ProcessListToolbar";
import { ISortBy } from "@patternfly/react-table/dist/js/components/Table";
import _ from "lodash";
import { alterOrderByObj, processListDefaultStatusFilter } from "../utils/ProcessListUtils";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import "../styles.css";
import {
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessInstanceState,
  ProcessListSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { LoadMore } from "@kie-tools/runtime-tools-components/dist/components/LoadMore";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

interface ProcessListProps {
  isEnvelopeConnectedToChannel: boolean;
  channelApi: MessageBusClientApi<ProcessListChannelApi>;
  initialState: ProcessListState;
  singularProcessLabel: string;
  pluralProcessLabel: string;
}
const ProcessList: React.FC<ProcessListProps & OUIAProps> = ({
  channelApi,
  isEnvelopeConnectedToChannel,
  initialState,
  singularProcessLabel,
  pluralProcessLabel,
  ouiaId,
  ouiaSafe,
}) => {
  const defaultFilters: ProcessInstanceFilter = useMemo(
    () =>
      initialState && initialState.filters
        ? { ...initialState.filters }
        : {
            status: processListDefaultStatusFilter,
            businessKey: [],
          },
    [initialState]
  );

  const defaultOrderBy: any = useMemo(
    () =>
      initialState && initialState.sortBy
        ? initialState.sortBy
        : {
            lastUpdate: OrderBy.DESC,
          },
    [initialState]
  );

  const [defaultPageSize] = useState<number>(10);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [offset, setOffset] = useState<number>(0);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [processInstances, setProcessInstances] = useState<ProcessInstance[]>([]);
  const [error, setError] = useState<string>();
  const [filters, setFilters] = useState<ProcessInstanceFilter>(defaultFilters);
  const [processStates, setProcessStates] = useState<ProcessInstanceState[]>(processListDefaultStatusFilter);
  const [expanded, setExpanded] = React.useState<{ [key: number]: boolean }>({});
  const [sortBy, setSortBy] = useState<ProcessListSortBy | ISortBy>(defaultOrderBy);
  const [selectedInstances, setSelectedInstances] = useState<ProcessInstance[]>([]);
  const [selectableInstances, setSelectableInstances] = useState<number>(0);
  const [isAllChecked, setIsAllChecked] = useState<boolean>(false);
  const [isInitialLoadDone, setIsInitialLoadDone] = useState(false);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsInitialLoadDone(false);
    }
  }, [isEnvelopeConnectedToChannel]);

  const countExpandableRows = useCallback(
    (instances: ProcessInstance[]): void => {
      instances.forEach((processInstance, index) => {
        expanded[index] = false;
        processInstance.isSelected = false;
        processInstance.isOpen = false;
        processInstance.childProcessInstances = [];
        if (processInstance.serviceUrl && processInstance.addons?.includes("process-management")) {
          setSelectableInstances((prev) => prev + 1);
        }
      });
    },
    [expanded]
  );

  const doQuery = useCallback(
    async (
      _offset: number,
      _limit: number,
      _resetProcesses: boolean,
      _resetPagination: boolean = false,
      _loadMore: boolean = false
    ): Promise<void> => {
      setIsLoading(true);
      setIsLoadingMore(_loadMore);
      setSelectableInstances(0);
      setSelectedInstances([]);
      setError(undefined);
      try {
        const response: ProcessInstance[] = await channelApi.requests.processList__query(_offset, _limit);
        setLimit(response.length);
        if (_resetProcesses) {
          countExpandableRows(response);
          setProcessInstances(response);
        } else {
          setProcessInstances((currentProcessInstances) => {
            const newData = currentProcessInstances.concat(response);
            countExpandableRows(newData);
            return newData;
          });
        }
        if (_resetPagination) {
          setOffset(_offset);
        }
      } catch (err) {
        setError(JSON.parse(JSON.parse(err.message).errorMessage));
      } finally {
        setIsLoading(false);
        setIsLoadingMore(false);
      }
    },
    [countExpandableRows, channelApi]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isEnvelopeConnectedToChannel && !isInitialLoadDone) {
          setFilters(defaultFilters);
          channelApi.requests
            .processList__initialLoad(defaultFilters, defaultOrderBy)
            .then(() => {
              if (canceled.get()) {
                return;
              }
              return doQuery(0, 10, true);
            })
            .then(() => {
              if (canceled.get()) {
                return;
              }
              setIsInitialLoadDone(true);
            });
        }
      },
      [defaultFilters, defaultOrderBy, doQuery, channelApi.requests, isEnvelopeConnectedToChannel, isInitialLoadDone]
    )
  );

  useEffect(() => {
    if (initialState && initialState.filters) {
      setFilters(initialState.filters);
      setProcessStates(initialState.filters.status);
      setSortBy(initialState.sortBy);
    }
  }, [initialState]);

  useEffect(() => {
    if (selectedInstances.length === selectableInstances && selectableInstances !== 0) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  }, [processInstances, selectableInstances, selectedInstances.length]);

  const applyFilter = useCallback(
    async (filter: ProcessInstanceFilter): Promise<void> => {
      setProcessInstances([]);
      await channelApi.requests.processList__applyFilter(filter);
      await doQuery(0, defaultPageSize, true, true);
    },
    [defaultPageSize, doQuery, channelApi.requests]
  );

  const applySorting = useCallback(
    async (event, index: number, direction: "asc" | "desc") => {
      setProcessInstances([]);
      setSortBy({ index, direction });
      let sortingColumn: string = event.target.innerText;
      sortingColumn = _.camelCase(sortingColumn);
      let sortByObj = _.set({}, sortingColumn, direction.toUpperCase());
      sortByObj = alterOrderByObj(sortByObj);
      await channelApi.requests.processList__applySorting(sortByObj);
      await doQuery(0, defaultPageSize, true, true);
    },
    [defaultPageSize, doQuery, channelApi.requests]
  );

  const doRefresh = useCallback(async (): Promise<void> => {
    setProcessInstances([]);
    await doQuery(0, defaultPageSize, true, true);
  }, [defaultPageSize, doQuery]);

  const doResetFilters = useCallback(async () => {
    const resetFilter = {
      status: processListDefaultStatusFilter,
      businessKey: [],
    };
    setProcessStates(processListDefaultStatusFilter);
    setFilters(resetFilter);
    await applyFilter(resetFilter);
  }, [applyFilter]);

  const mustShowLoadMore =
    (!isLoading || isLoadingMore) && processInstances && limit === pageSize && filters.status.length > 0;

  if (error) {
    return <ServerErrors error={error} variant={"large"} />;
  }

  return (
    <div {...componentOuiaProps(ouiaId, "process-list", ouiaSafe ? ouiaSafe : !isLoading)}>
      <ProcessListToolbar
        applyFilter={applyFilter}
        refresh={doRefresh}
        filters={filters}
        setFilters={setFilters}
        processStates={processStates}
        setProcessStates={setProcessStates}
        selectedInstances={selectedInstances}
        setSelectedInstances={setSelectedInstances}
        processInstances={processInstances}
        setProcessInstances={setProcessInstances}
        isAllChecked={isAllChecked}
        setIsAllChecked={setIsAllChecked}
        channelApi={channelApi}
        defaultStatusFilter={processListDefaultStatusFilter}
        singularProcessLabel={singularProcessLabel}
        pluralProcessLabel={pluralProcessLabel}
      />
      {filters.status.length > 0 ? (
        <>
          <ProcessListTable
            processInstances={processInstances}
            isLoading={isLoading}
            expanded={expanded}
            setExpanded={setExpanded}
            channelApi={channelApi}
            onSort={applySorting}
            sortBy={sortBy}
            setProcessInstances={setProcessInstances}
            selectedInstances={selectedInstances}
            setSelectedInstances={setSelectedInstances}
            selectableInstances={selectableInstances}
            setSelectableInstances={setSelectableInstances}
            setIsAllChecked={setIsAllChecked}
            singularProcessLabel={singularProcessLabel}
            pluralProcessLabel={pluralProcessLabel}
          />
          {mustShowLoadMore && (
            <LoadMore
              offset={offset}
              setOffset={setOffset}
              getMoreItems={(_offset, _limit) => {
                setPageSize(_limit);
                doQuery(_offset, _limit, false, true, true);
              }}
              pageSize={pageSize}
              isLoadingMore={isLoadingMore}
            />
          )}
        </>
      ) : (
        <div>
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

export default ProcessList;
