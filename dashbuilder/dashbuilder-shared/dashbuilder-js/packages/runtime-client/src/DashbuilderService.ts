/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export type RuntimeMode = "MULTIPLE_IMPORT" | "SINGLE" | "STATIC";

export interface ApiResponse {
  mode: RuntimeMode;
  availableModels: string[];
  acceptingNewImports: boolean;
}

export interface DashboardResponse {
  runtimeModelId: string;
  pages: string[];
}

export interface RequestInfo {
  url: string;
  user: string;
  password: string;
}

export function embeddedRuntimeUrl(url: string, dashboardId: string, page: string) {
  const urlBuild = new URL(url);
  urlBuild.searchParams.set("standalone", "true");
  urlBuild.searchParams.set("perspective", page);
  if (dashboardId && dashboardId !== "") {
    urlBuild.searchParams.set("import", dashboardId);
  }
  return urlBuild.href;
}

export class DashbuilderService {
  private requestInfo: RequestInfo;
  private authToken: string;

  constructor(requestInfo: RequestInfo) {
    this.requestInfo = requestInfo;
    this.authToken = btoa(requestInfo.user + ":" + requestInfo.password);
  }

  public listDashboards(): Promise<ApiResponse> {
    const url = `${this.requestInfo.url}/rest/api`;
    return this.request(url).then(obj => (obj as unknown) as ApiResponse);
  }

  public listPages(id: string): Promise<DashboardResponse> {
    if (!id || id === "") {
      return Promise.resolve({
        runtimeModelId: id,
        pages: []
      });
    }
    const url = `${this.requestInfo.url}/rest/api/dashboard/${id}`;
    return this.request(url).then(obj => (obj as unknown) as DashboardResponse);
  }

  private request(url: string): Promise<string> {
    return fetch(url, {
      credentials: "include",
      mode: "cors",
      headers: new Headers({
        Authorization: `Basic ${this.authToken}`
      })
    }).then(r => r.json());
  }
}
