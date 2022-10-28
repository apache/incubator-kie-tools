/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const IS_HASH_ROUTER = true;

export enum QueryParams {
  SETTINGS = "settings",
  URL = "url",
  BRANCH = "branch",
  DMN_RUNNER_FORM_INPUTS = "formInputs",
  EXPAND = "expand",
  AUTH_SESSION_ID = "authSessionId",
  CONFIRM = "confirm",
}

export enum PathParams {
  EXTENSION = "extension",
  WORKSPACE_ID = "workspaceId",
  FILE_RELATIVE_PATH = "fileRelativePath",
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
  download: new Route<{}>(() => `/download`),

  home: new Route<{
    queryParams: QueryParams.EXPAND;
  }>(() => `/`),

  /** @deprecated
   * Use import instead */
  editor: new Route<{
    pathParams: PathParams.EXTENSION;
    queryParams: QueryParams.URL | QueryParams.SETTINGS | QueryParams.DMN_RUNNER_FORM_INPUTS;
  }>(({ extension }) => `/editor/${extension}`),

  newModel: new Route<{
    pathParams: PathParams.EXTENSION;
  }>(({ extension }) => `/new/${extension}`),

  import: new Route<{
    queryParams:
      | QueryParams.URL
      | QueryParams.DMN_RUNNER_FORM_INPUTS
      | QueryParams.BRANCH
      | QueryParams.AUTH_SESSION_ID
      | QueryParams.CONFIRM;
  }>(() => `/import`),

  workspaceWithFilePath: new Route<{
    pathParams: PathParams.WORKSPACE_ID | PathParams.FILE_RELATIVE_PATH | PathParams.EXTENSION;
  }>(({ workspaceId, fileRelativePath, extension }) => `/${workspaceId}/file/${fileRelativePath}.${extension}`),

  static: {
    sample: new Route<{ pathParams: "type" }>(({ type }) => `samples/Sample.${type}`),
    images: {
      vscodeLogoBlue: new Route<{}>(() => `images/vscode.svg`),
      vscodeLogoWhite: new Route<{}>(() => `images/vscode-alt.svg`),
      kogitoLogoWhite: new Route<{}>(() => `images/kogito_logo_white.png`),
      kieHorizontalLogoReverse: new Route<{}>(() => `images/kie_horizontal_rgb_fullcolor_reverse.svg`),
      dmnRunnerGif: new Route<{}>(() => `images/dmn-runner2.gif`),
      dmnDevSandboxGif: new Route<{}>(() => `images/dmn-dev-sandbox.gif`),
    },
  },
};
