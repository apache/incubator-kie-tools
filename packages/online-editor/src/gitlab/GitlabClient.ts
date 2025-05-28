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
import { isEmpty } from "lodash";

export interface Options {
  appName: string;
  token?: string;
  domain?: string;
  headers?: Record<string, string>;
  proxyUrl?: string;
}

type GitlabVisibility = "private" | "internal" | "public";
type GitlabBasePath = "/projects" | "/groups" | "/snippets";

type GetSnippetArgsType = {
  snippetId: string;
  group?: string;
  project?: string;
};

type GetSnippetFileArgsType = {
  snippetId: string;
  group?: string;
  project?: string;
  branch?: string;
  filePath: string;
};

type GetRepositoryContentsArgsType = {
  project: string;
  ref: string;
  path: string;
  group?: string;
};

type CreateRepositoryArgsType = {
  name: string;
  groupId?: string;
  visibility: GitlabVisibility;
};

type SnippetsFile = {
  file_path: string;
  content: string;
};

type CreateSnippetArgsType = {
  title: string;
  files: SnippetsFile[];
  visibility: GitlabVisibility;
  id?: string;
};

type ProjectPath = {
  group?: string;
  project?: string;
};

export interface GitlabClientApi {
  appName: string;
  token?: string;
  domain?: string;
  headers?: Record<string, string>;
  getApiUrl(): string;
  getAuthedUser(): Promise<Response>;
  createRepository(args: CreateRepositoryArgsType): Promise<Response>;
  getRepositoryContents(args: GetRepositoryContentsArgsType): Promise<Response>;
  createSnippet(args: CreateSnippetArgsType): Promise<Response>;
  getSnippet(args: GetSnippetArgsType): Promise<Response>;
  getSnippetFile(args: GetSnippetFileArgsType): Promise<Response>;
  getSnippetFileRaw(args: GetSnippetFileArgsType): Promise<Response>;
  listGroups(): Promise<Response>;
  listProjects(): Promise<Response>;
}
export class GitlabClient implements GitlabClientApi {
  constructor(options: Options) {
    this.appName = options.appName;
    this.token = options?.token ?? "";
    this.domain = options?.domain ?? "gitlab.com";
    this.headers = {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...options?.headers,
    };
    this.proxyUrl = options.proxyUrl;
  }
  appName: string;
  token: string;
  domain: string;
  headers: Record<string, string>;
  proxyUrl?: string;

  request = async (props: {
    urlContext: string;
    method?: string;
    body?: any;
    extraHeaders?: { [key: string]: string | undefined };
  }) => {
    const resolvedUrl = new URL(this.getApiUrl() + props.urlContext).toString();
    const authHeader: Record<string, string> = { Authorization: "Bearer " + this.token };
    const requestHeaders = {
      ...this.headers,
      ...props.extraHeaders,
      ...authHeader,
    };

    return fetch(resolvedUrl, {
      method: props.method,
      headers: JSON.parse(JSON.stringify(requestHeaders)), // allows for erasing header in extraHeaders
      body: props.body,
    });
  };

  getApiUrl = () => {
    if (this.proxyUrl) {
      return `${this.proxyUrl}/${this.domain}/api/v4`;
    }
    return `https://${this.domain}/api/v4`;
  };

  getAuthedUser = () => this.request({ urlContext: "/user" });

  createProjectPath = ({ group, project }: ProjectPath): string =>
    encodeURIComponent([group, project].filter(Boolean).join("/"));

  createProjectBasePathWithId = ({ group, project }: ProjectPath, basePath: GitlabBasePath = "/projects") => {
    const id = this.createProjectPath({ group, project });
    return id ? `${basePath}/${id}` : basePath;
  };

  checkSnippetLevel = ({ group, project }: ProjectPath): string => {
    const isProjectSnippet = !isEmpty(group) || !isEmpty(project);
    return isProjectSnippet ? this.createProjectBasePathWithId({ group, project }) : "";
  };

  getRepositoryContents = ({ project, path, ref, group }: GetRepositoryContentsArgsType) => {
    const baseUrl = this.createProjectBasePathWithId({ group, project });
    return this.request({
      urlContext: `${baseUrl}/repository/files/${path}/raw?ref=${ref}`,
      method: "get",
    });
  };

  createRepository = ({ groupId, visibility, name }: CreateRepositoryArgsType) =>
    this.request({
      urlContext: `/projects`,
      method: "post",
      body: JSON.stringify({
        name,
        visibility,
        description: `This repository was created using ${this.appName}`,
        ...(groupId && { namespace_id: groupId }),
      }),
    });

  createSnippet = ({ title, visibility, id, files }: CreateSnippetArgsType) => {
    const url = id ? "/projects/" + id + "/snippets" : "/snippets";
    return this.request({
      urlContext: url,
      method: "post",
      body: JSON.stringify({
        title,
        description: `This snippet was created using ${this.appName}`,
        visibility,
        files,
      }),
    });
  };

  getSnippet = ({ snippetId, group, project }: GetSnippetArgsType) => {
    const baseUrl = this.checkSnippetLevel({ group, project });
    return this.request({
      urlContext: `${baseUrl}/snippets/${snippetId}`,
      method: "get",
    });
  };

  getSnippetFile = ({ snippetId, branch, filePath, group, project }: GetSnippetFileArgsType) => {
    const baseUrl = this.checkSnippetLevel({ group, project });
    return this.request({
      urlContext: `${baseUrl}/snippets/${snippetId}/files/${branch}/${filePath}`,
      method: "get",
    });
  };

  getSnippetFileRaw = ({ snippetId, branch, filePath, group, project }: GetSnippetFileArgsType) => {
    const baseUrl = this.checkSnippetLevel({ group, project });
    return this.request({
      urlContext: `${baseUrl}/snippets/${snippetId}/files/${branch}/${filePath}/raw`,
      method: "get",
    });
  };

  listGroups = () =>
    this.request({
      urlContext: "/groups?active=true",
      method: "get",
    });

  listProjects = () =>
    this.request({
      urlContext: "/projects?membership=true&&active=true",
      method: "get",
    });
}
