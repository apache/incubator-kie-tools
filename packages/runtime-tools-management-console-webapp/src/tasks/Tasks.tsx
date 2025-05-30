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
import { useNavigate } from "react-router-dom";
import { EmbeddedTaskList } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskList";
import { getActiveTaskStates, getAllTaskStates } from "@kie-tools/runtime-tools-process-webapp-components/dist/utils";
import {
  TaskListQueryFilter,
  TaskListSortBy,
  UserTaskInstance,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { TaskListState } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskList";
import { RuntimePathSearchParamsRoutes, useRuntimeDispatch } from "../runtime/RuntimeContext";
import { useQueryParam, useQueryParams } from "../navigation/queryParams/QueryParamsContext";
import { QueryParams } from "../navigation/Routes";
import { useLocation } from "react-router-dom";
import { useTaskListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskList";

interface Props {
  onNavigateToTaskDetails: (taskId: string) => void;
}

const defaultFilter: TaskListQueryFilter = {
  taskNames: [],
  taskStates: getActiveTaskStates(),
};

const defaultOrderBy: TaskListSortBy = {
  property: "lastUpdate",
  direction: "desc",
};

export const Tasks: React.FC<Props> = ({ onNavigateToTaskDetails }) => {
  const channelApi = useTaskListChannelApi();
  const navigate = useNavigate();
  const location = useLocation();
  const filters = useQueryParam(QueryParams.FILTERS);
  const sortBy = useQueryParam(QueryParams.SORT_BY);
  const queryParams = useQueryParams();
  const { setRuntimePathSearchParams } = useRuntimeDispatch();

  const initialState: TaskListState = useMemo(() => {
    return {
      filters: filters ? (JSON.parse(filters) as TaskListState["filters"]) : defaultFilter,
      sortBy: sortBy ? (JSON.parse(sortBy) as TaskListState["sortBy"]) : defaultOrderBy,
      currentPage: {
        offset: 0,
        limit: 10,
      },
    };
  }, [filters, sortBy]);

  useEffect(() => {
    const newSearchParams = {
      [QueryParams.FILTERS]: JSON.stringify(initialState.filters),
      [QueryParams.SORT_BY]: JSON.stringify(initialState.sortBy),
    };
    setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
      return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASKS, newSearchParams);
    });
  }, [initialState, setRuntimePathSearchParams]);

  useEffect(() => {
    const unsubscriber = channelApi.taskList__onUpdateTaskListState({
      onUpdate(taskListState: TaskListState) {
        const newSearchParams = {
          [QueryParams.FILTERS]: JSON.stringify(taskListState.filters),
          [QueryParams.ORDER_BY]: JSON.stringify(taskListState.sortBy),
        };
        setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
          return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASKS, newSearchParams);
        });
        const newQueryParams = queryParams
          .with(QueryParams.FILTERS, newSearchParams[QueryParams.FILTERS])
          .with(QueryParams.ORDER_BY, newSearchParams[QueryParams.ORDER_BY]);
        navigate({ pathname: location.pathname, search: newQueryParams.toString() }, { replace: true });
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, navigate, location.pathname, queryParams, setRuntimePathSearchParams]);

  useEffect(() => {
    const unsubscriber = channelApi.taskList__onOpenTaskListen({
      onOpen(task: UserTaskInstance) {
        onNavigateToTaskDetails(task.id);
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, onNavigateToTaskDetails]);

  return (
    <EmbeddedTaskList
      initialState={initialState}
      channelApi={channelApi}
      allTaskStates={getAllTaskStates()}
      activeTaskStates={getActiveTaskStates()}
      targetOrigin={window.location.origin}
    />
  );
};
