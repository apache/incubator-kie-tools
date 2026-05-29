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

import * as React from "react";
import { DEFAULT_DATA_TYPES } from "@kie-tools/bpmn-editor/dist/mutations/addOrGetItemDefinitions";

export enum RestProperties {
  Method = "Method",
  Url = "Url",
  Protocol = "Protocol",
  Host = "Host",
  Port = "Port",
  ContentData = "ContentData",
  RequestTimeout = "RequestTimeout",
  AccessTokenAcquisitionStrategy = "AccessTokenAcquisitionStrategy",
  RestServiceCallTaskId = "RestServiceCallTaskId",
}

export enum HttpMethod {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  PATCH = "PATCH",
  DELETE = "DELETE",
}

export enum AuthStrategy {
  PROPAGATED = "propagated",
  CONFIGURED = "configured",
  NONE = "none",
}

export const HTTP_METHODS_OPTIONS = [
  { value: HttpMethod.GET, labelKey: "httpMethodGet" },
  { value: HttpMethod.POST, labelKey: "httpMethodPost" },
  { value: HttpMethod.PUT, labelKey: "httpMethodPut" },
  { value: HttpMethod.PATCH, labelKey: "httpMethodPatch" },
  { value: HttpMethod.DELETE, labelKey: "httpMethodDelete" },
] as const;

export const AUTH_STRATEGIES_OPTIONS = [
  { value: AuthStrategy.PROPAGATED, labelKey: "authStrategyPropagated" },
  { value: AuthStrategy.CONFIGURED, labelKey: "authStrategyConfigured" },
  { value: AuthStrategy.NONE, labelKey: "authStrategyNone" },
] as const;

export const REST_PROPERTIES_KEYS = [
  RestProperties.Method,
  RestProperties.Url,
  RestProperties.Protocol,
  RestProperties.Host,
  RestProperties.Port,
  RestProperties.ContentData,
  RestProperties.RequestTimeout,
  RestProperties.AccessTokenAcquisitionStrategy,
  RestProperties.RestServiceCallTaskId,
] as const;

export const REST_TASK_ICON = (
  <svg
    width="30"
    height="30"
    viewBox="0 0 30 30"
    xmlns="http://www.w3.org/2000/svg"
    fill="none"
    stroke="black"
    strokeWidth="1"
  >
    <text x="15" y="25" textAnchor="middle" fontSize="24" fontFamily="Arial" fontWeight={"light"}>
      🌐
    </text>
  </svg>
);

export const REST_PROPERTIES_DATA_TYPES = {
  [RestProperties.Method]: DEFAULT_DATA_TYPES.STRING,
  [RestProperties.Url]: DEFAULT_DATA_TYPES.STRING,
  [RestProperties.Protocol]: DEFAULT_DATA_TYPES.STRING,
  [RestProperties.Host]: DEFAULT_DATA_TYPES.STRING,
  [RestProperties.Port]: DEFAULT_DATA_TYPES.INTEGER,
  [RestProperties.ContentData]: DEFAULT_DATA_TYPES.OBJECT,
  [RestProperties.RequestTimeout]: DEFAULT_DATA_TYPES.INTEGER,
  [RestProperties.AccessTokenAcquisitionStrategy]: DEFAULT_DATA_TYPES.STRING,
  [RestProperties.RestServiceCallTaskId]: DEFAULT_DATA_TYPES.STRING,
};

export const HEADER_PREFIX = "HEADER_";
export const QUERY_PREFIX = "QUERY_";
