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
import React, { useEffect, useMemo } from "react";
import {
  ProcessInstance,
  ProcessInstanceState,
  ProcessInstanceFilter,
  ProcessListSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { useProcessListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessList";
import {
  EmbeddedProcessList,
  ProcessListState,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processList";
import { useLocation, useNavigate } from "react-router-dom";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { useQueryParam, useQueryParams } from "../../navigation/queryParams/QueryParamsContext";
import { QueryParams } from "../../navigation/Routes";
import { RuntimePathSearchParamsRoutes, useRuntimeDispatch } from "../../runtime/RuntimeContext";

const defaultFilters = {
  status: [ProcessInstanceState.Active],
  businessKey: [],
};

const defaultSortBy = {
  lastUpdate: OrderBy.DESC,
};

interface Props {
  onNavigateToProcessDetails: (processInstanceId: string) => void;
}

export const ProcessList: React.FC<Props> = ({ onNavigateToProcessDetails }) => {
  const channelApi = useProcessListChannelApi();
  const navigate = useNavigate();
  const location = useLocation();
  const filters = useQueryParam(QueryParams.FILTERS);
  const sortBy = useQueryParam(QueryParams.SORT_BY);
  const queryParams = useQueryParams();
  const { setRuntimePathSearchParams } = useRuntimeDispatch();

  const initialState: ProcessListState = useMemo(() => {
    return {
      filters: filters ? (JSON.parse(filters) as ProcessInstanceFilter) : defaultFilters,
      sortBy: sortBy ? (JSON.parse(sortBy) as ProcessListSortBy) : defaultSortBy,
    };
  }, [filters, sortBy]);

  useEffect(() => {
    const newSearchParams = {
      [QueryParams.FILTERS]: JSON.stringify(initialState.filters),
      [QueryParams.SORT_BY]: JSON.stringify(initialState.sortBy),
    };
    setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
      return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.PROCESSES, newSearchParams);
    });
  }, [initialState, setRuntimePathSearchParams]);

  useEffect(() => {
    const unsubscriber = channelApi.processList__onOpenProcessListen({
      onOpen(process: ProcessInstance) {
        onNavigateToProcessDetails(process.id);
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, onNavigateToProcessDetails]);

  useEffect(() => {
    const unsubscriber = channelApi.processList__onUpdateProcessListState({
      onUpdate(processListState: ProcessListState) {
        const newSearchParams = {
          [QueryParams.FILTERS]: JSON.stringify(processListState.filters),
          [QueryParams.SORT_BY]: JSON.stringify(processListState.sortBy),
        };
        setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
          return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.PROCESSES, newSearchParams);
        });
        const newQueryParams = queryParams
          .with(QueryParams.FILTERS, newSearchParams[QueryParams.FILTERS])
          .with(QueryParams.SORT_BY, newSearchParams[QueryParams.SORT_BY]);
        navigate({ pathname: location.pathname, search: newQueryParams.toString() }, { replace: true });
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, navigate, location.pathname, queryParams, setRuntimePathSearchParams]);

  return (
    <EmbeddedProcessList
      channelApi={channelApi}
      targetOrigin={window.location.origin}
      initialState={initialState}
      singularProcessLabel={"Process"}
      pluralProcessLabel={"Processes"}
    />
  );
};
