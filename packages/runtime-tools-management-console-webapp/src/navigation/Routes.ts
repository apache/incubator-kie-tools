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

declare global {
  interface Window {
    BASE_PATH: string;
  }
}

const IS_HASH_ROUTER = false;

export enum QueryParams {
  USER = "user",
  FILTERS = "filters",
  SORT_BY = "sort",
  ORDER_BY = "order",
  IMPERSONATION_USER = "impersonationUsername",
  IMPERSONATION_GROUPS = "impersonationGroups",
}

export enum PathParams {
  RUNTIME_URL = "runtimeUrl",
  PROCESS_INSTANCE_ID = "processInstanceId",
  TASK_ID = "taskId",
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
    static?: boolean;
    pathParams: { [k in T["pathParams"]]: string };
    queryParams?: Partial<{ [k in T["queryParams"]]: string }>;
  }) {
    const path = this.pathDelegate(args.pathParams);
    const SEP = args.base?.endsWith("/") || path.startsWith("/") ? "" : "/";
    const HASH = !args.static && IS_HASH_ROUTER ? "#" : "";
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
  with(name: Q, value: string | undefined): QueryParamsImpl<Q>;
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
      if (value === undefined) {
        urlSearchParams.delete(name);
      } else {
        urlSearchParams.set(name, value);
      }
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
  home: new Route<{}>(() => "/"),

  runtime: {
    context: new Route<{
      pathParams: PathParams.RUNTIME_URL;
      queryParams: QueryParams.USER;
    }>(({ runtimeUrl }) => `/${runtimeUrl}`),

    processes: new Route<{
      pathParams: PathParams.RUNTIME_URL;
      queryParams: QueryParams.USER | QueryParams.FILTERS | QueryParams.SORT_BY;
    }>(({ runtimeUrl }) => `/${runtimeUrl}/processes`),

    processDetails: new Route<{
      pathParams: PathParams.RUNTIME_URL | PathParams.PROCESS_INSTANCE_ID;
      queryParams: QueryParams.USER;
    }>(({ runtimeUrl, processInstanceId }) => `/${runtimeUrl}/process/${processInstanceId}`),

    jobs: new Route<{
      pathParams: PathParams.RUNTIME_URL;
      queryParams: QueryParams.USER | QueryParams.FILTERS | QueryParams.ORDER_BY;
    }>(({ runtimeUrl }) => `/${runtimeUrl}/jobs`),

    tasks: new Route<{
      pathParams: PathParams.RUNTIME_URL;
      queryParams:
        | QueryParams.USER
        | QueryParams.FILTERS
        | QueryParams.SORT_BY
        | QueryParams.IMPERSONATION_USER
        | QueryParams.IMPERSONATION_GROUPS;
    }>(({ runtimeUrl }) => `/${runtimeUrl}/tasks`),

    taskDetails: new Route<{
      pathParams: PathParams.RUNTIME_URL | PathParams.TASK_ID;
      queryParams: QueryParams.USER | QueryParams.IMPERSONATION_USER | QueryParams.IMPERSONATION_GROUPS;
    }>(({ runtimeUrl, taskId }) => `/${runtimeUrl}/task/${taskId}`),
  },

  login: new Route<{}>(() => `/login`),

  static: {
    images: {
      appLogoDefault: new Route<{}>(() => `/images/app_logo_rgb_fullcolor_default.svg`),
      appLogoReverse: new Route<{}>(() => `/images/app_logo_rgb_fullcolor_reverse.svg`),
    },
  },
} as const;

export function buildRouteUrl<
  T extends {
    pathParams: any;
    queryParams: any;
  },
>(
  route: Route<T>,
  pathParams?: { [k in T["pathParams"]]: string },
  queryParams?: Partial<{ [k in T["queryParams"]]: string }>,
  extraQueryParams?: Record<string, string>
) {
  const encodedPathParams = {} as T["pathParams"];
  if (pathParams) {
    for (const [key, value] of Object.entries(pathParams)) {
      encodedPathParams[key] = encodeURIComponent(value as string);
    }
  }
  const pathname = route.path(encodedPathParams);

  const searchParams: string[] = [];
  if (queryParams || extraQueryParams) {
    for (const [key, value] of Object.entries({ ...(queryParams ?? {}), ...(extraQueryParams ?? {}) })) {
      if (value) {
        searchParams.push(`${key}=${encodeURIComponent(value as string)}`);
      }
    }
  }
  const search = searchParams.length > 0 ? `?${searchParams.join("&")}` : "";

  return {
    pathname,
    search,
  };
}
