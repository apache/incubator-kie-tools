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
import React, { useMemo, useEffect } from "react";
import {
  EmbeddedJobsManagement,
  JobsManagementState,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/jobsManagement";
import { useJobsManagementChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/JobsManagement";
import { useLocation, useNavigate } from "react-router-dom";
import { useQueryParam, useQueryParams } from "../navigation/queryParams/QueryParamsContext";
import { RuntimePathSearchParamsRoutes, useRuntimeDispatch } from "../runtime/RuntimeContext";
import { QueryParams } from "../navigation/Routes";
import { JobStatus } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

const defaultStatus = [JobStatus.Scheduled];

const defaultOrderBy = {
  lastUpdate: OrderBy.DESC,
};

export const Jobs: React.FC = () => {
  const channelApi = useJobsManagementChannelApi();
  const navigate = useNavigate();
  const location = useLocation();
  const filters = useQueryParam(QueryParams.FILTERS);
  const orderBy = useQueryParam(QueryParams.ORDER_BY);
  const queryParams = useQueryParams();
  const { setRuntimePathSearchParams } = useRuntimeDispatch();

  const initialState: JobsManagementState = useMemo(() => {
    return {
      filters: filters ? (JSON.parse(filters) as JobsManagementState["filters"]) : defaultStatus,
      orderBy: orderBy ? (JSON.parse(orderBy) as JobsManagementState["orderBy"]) : defaultOrderBy,
    };
  }, [filters, orderBy]);

  useEffect(() => {
    const newSearchParams = {
      [QueryParams.FILTERS]: JSON.stringify(initialState.filters),
      [QueryParams.ORDER_BY]: JSON.stringify(initialState.orderBy),
    };
    setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
      return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.JOBS, newSearchParams);
    });
  }, [initialState, setRuntimePathSearchParams]);

  useEffect(() => {
    const unsubscriber = channelApi.jobList__onUpdateJobsManagementState({
      onUpdate(jobsManagementState: JobsManagementState) {
        const newSearchParams = {
          [QueryParams.FILTERS]: JSON.stringify(jobsManagementState.filters),
          [QueryParams.ORDER_BY]: JSON.stringify(jobsManagementState.orderBy),
        };
        setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
          return currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.JOBS, newSearchParams);
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
  }, [channelApi, location.pathname, navigate, queryParams, setRuntimePathSearchParams]);

  return channelApi && initialState ? (
    <EmbeddedJobsManagement channelApi={channelApi} targetOrigin={window.location.origin} initialState={initialState} />
  ) : (
    <></>
  );
};
