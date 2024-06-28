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

const IS_HASH_ROUTER = true;
const SETTINGS_ROUTE = "/settings";

export enum QueryParams {
  SETTINGS = "settings",
  URL = "url",
  BRANCH = "branch",
  REMOVE_REMOTE = "removeRemote",
  RENAME_WORKSPACE = "renameWorkspace",
  SAMPLE_ID = "sampleId",
  SAMPLES_CATEGORY = "category",
  FILTERS = "filters",
  SORT_BY = "sortBy",
}

export enum PathParams {
  EXTENSION = "extension",
  WORKSPACE_ID = "workspaceId",
  FILE_RELATIVE_PATH = "fileRelativePath",
  WORKFLOW_ID = "workflowId",
  WORKFLOW_NAME = "workflowName",
}

export class Route<
  T extends {
    pathParams?: any;
    queryParams?: any;
  },
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

export function newQueryParamsImpl<Q extends string>(queryString: string): QueryParamsImpl<Q> {
  return {
    has: (name) => new URLSearchParams(queryString).has(name),
    get: (name) => {
      const val = new URLSearchParams(queryString).get(name);
      return !val ? undefined : decodeURIComponent(val);
    },
    with: (name, value) => {
      const urlSearchParams = new URLSearchParams(queryString);
      urlSearchParams.set(name, value);
      return newQueryParamsImpl(decodeURIComponent(urlSearchParams.toString()));
    },
    without: (name) => {
      const urlSearchParams = new URLSearchParams(queryString);
      urlSearchParams.delete(name);
      return newQueryParamsImpl(decodeURIComponent(urlSearchParams.toString()));
    },
    toString: () => {
      return decodeURIComponent(new URLSearchParams(queryString).toString());
    },
  };
}

export const routes = {
  home: new Route<{}>(() => `/`),
  runtimeToolsWorkflowInstances: new Route<{}>(() => `/WorkflowInstances`),
  runtimeToolsWorkflowDefinitions: new Route<{}>(() => `/WorkflowDefinitions`),
  runtimeToolsWorkflowDetails: new Route<{
    queryParams: QueryParams.FILTERS | QueryParams.SORT_BY;
    pathParams: PathParams.WORKFLOW_ID;
  }>(({ workflowId }) => `/WorkflowDetails/${workflowId}`),
  runtimeToolsWorkflowForm: new Route<{
    pathParams: PathParams.WORKFLOW_NAME;
  }>(({ workflowName }) => `/WorkflowDefinition/${workflowName}`),
  runtimeToolsTriggerCloudEventForWorkflow: new Route<{
    pathParams: PathParams.WORKFLOW_ID;
  }>(({ workflowId }) => `/WorkflowInstances/${workflowId}`),
  runtimeToolsTriggerCloudEventForWorkflowDefinition: new Route<{
    pathParams: PathParams.WORKFLOW_NAME;
  }>(({ workflowName }) => `/WorkflowDefinition/${workflowName}/trigger-cloud-event`),
  monitoring: new Route<{}>(() => `/Monitoring`),
};
