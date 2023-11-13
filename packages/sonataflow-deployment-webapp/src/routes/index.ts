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

import { APPDATA_JSON_FILENAME } from "../AppConstants";

const IS_HASH_ROUTER = true;
const WORKFLOWS_ROUTE = "/workflows";
const RUNTIME_TOOLS_ROUTE = "/runtime-tools";

export enum QueryParams {
  FILTERS = "filters",
  SORT_BY = "sortBy",
}

export enum PathParams {
  WORKFLOW_ID = "workflowId",
}

export class Route<
  T extends {
    pathParams?: any;
    queryParams?: any;
  }
> {
  constructor(private readonly pathDelegate: (pathParams: { [k in T["pathParams"]]: string }) => string) {}

  public url(args: {
    base?: string;
    pathParams: { [k in T["pathParams"]]: string };
    queryParams?: Partial<{ [k in T["queryParams"]]: string }>;
  }) {
    const SEP = args.base?.endsWith("/") ? "" : "/";
    const HASH = IS_HASH_ROUTER ? "#" : "";
    const path = this.pathDelegate(args.pathParams);
    const queryParams = args.queryParams ?? {};

    if (!args.base && Object.keys(queryParams).length <= 0) {
      return `${HASH}${path}`;
    }

    if (!args.base) {
      return `${HASH}${path}?${this.queryString(queryParams)}`;
    }

    if (Object.keys(queryParams).length <= 0) {
      return `${args.base}${SEP}${HASH}${path}`;
    }

    return `${args.base}${SEP}${HASH}${path}?${this.queryString(queryParams)}`;
  }

  public queryString(queryParams: Partial<{ [k in T["queryParams"]]: string }>) {
    return decodeURIComponent(new URLSearchParams(queryParams as Record<string, string>).toString());
  }

  public queryArgs(queryString: QueryParamsImpl<string>): QueryParamsImpl<T["queryParams"]> {
    return queryString;
  }

  public path(pathParams: { [k in T["pathParams"]]: string }) {
    return this.pathDelegate(pathParams);
  }
}

export interface QueryParamsImpl<Q extends string> {
  has(name: Q): boolean;
  get(name: Q): string | undefined;
  with(name: Q, value: string): QueryParamsImpl<Q>;
  without(name: Q): QueryParamsImpl<Q>;
  toString(): string;
}

export const routes = {
  home: new Route<{}>(() => "/"),
  dataJsonError: new Route<{}>(() => "/data-json-error"),

  workflows: {
    home: new Route<{}>(() => WORKFLOWS_ROUTE),
    form: new Route<{
      pathParams: PathParams.WORKFLOW_ID;
    }>(({ workflowId }) => `${WORKFLOWS_ROUTE}/${workflowId}`),
    cloudEvent: new Route<{}>(() => `/triggerCloudEvent`),
  },

  runtimeTools: {
    home: new Route<{}>(() => RUNTIME_TOOLS_ROUTE),
    workflowInstances: new Route<{}>(() => RUNTIME_TOOLS_ROUTE + `/workflow-instances`),
    workflowDetails: new Route<{
      queryParams: QueryParams.FILTERS | QueryParams.SORT_BY;
      pathParams: PathParams.WORKFLOW_ID;
    }>(({ workflowId }) => RUNTIME_TOOLS_ROUTE + `/workflow-details/${workflowId}`),
  },

  dataJson: new Route<{}>(() => "/" + APPDATA_JSON_FILENAME),
  openApiJson: new Route<{}>(() => "/q/openapi.json"),
};
