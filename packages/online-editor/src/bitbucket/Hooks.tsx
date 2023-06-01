/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { useMemo } from "react";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { AuthSession } from "../authSessions/AuthSessionApi";
import { useEnv } from "../env/hooks/EnvContext";

export enum AuthOptionsType {
  UNDEFINED = "UNDEFINED",
  BASIC = "BASIC",
}
export type BasicAuthOptions = {
  type: AuthOptionsType.BASIC;
  username: string;
  password: string;
};
const undefinedAuthOptions = {
  type: AuthOptionsType.UNDEFINED,
} as const;

export type AuthOptions = BasicAuthOptions | typeof undefinedAuthOptions;

export interface Options {
  appName: string;
  username?: string;
  domain?: string;
  auth?: AuthOptions;
  headers?: Record<string, string>;
}

type GetRepositoryContentsArgsType = {
  workspace: string;
  repository: string;
  ref: string;
  path: string;
  meta: boolean;
};

type CreateRepoArgsType = {
  name: string;
  workspace: string;
  isPrivate: boolean;
};

type PushEmptyCommitArgsType = {
  repository: string;
  workspace: string;
  branch: string;
};

type GetSnippetArgsType = {
  workspace: string;
  snippetId: string;
};

type CreateSnippetArgsType = {
  workspace: string;
  title: string;
  files: {
    [key: string]: {
      content: string;
    };
  };
  isPrivate: boolean;
};

export interface BitbucketClientApi {
  appName: string;
  domain?: string;
  auth?: AuthOptions;
  headers?: Record<string, string>;
  getApiUrl(): string;
  getAuthedUser(): Promise<Response>;
  getRepositoryContents(args: GetRepositoryContentsArgsType): Promise<Response>;
  createRepo(args: CreateRepoArgsType): Promise<Response>;
  pushEmptyCommit(args: PushEmptyCommitArgsType): Promise<Response>;
  getSnippet(args: GetSnippetArgsType): Promise<Response>;
  createSnippet(args: CreateSnippetArgsType): Promise<Response>;
  listWorkspaces(): Promise<Response>;
}
export class BitbucketClient implements BitbucketClientApi {
  constructor(options: Options) {
    this.appName = options.appName;
    this.domain = options?.domain ?? "bitbucket.org";
    this.headers = options?.headers ?? {
      "Content-Type": "application/json",
      Accept: "application/json",
    };
    this.auth = options?.auth ?? undefinedAuthOptions;
    this.username = options.username ?? (options?.auth as BasicAuthOptions)?.username;
  }
  appName: string;
  auth: AuthOptions;
  domain: string;
  headers: Record<string, string>;
  username?: string;

  request = async (props: {
    urlContext: string;
    method?: string;
    body?: any;
    extraHeaders?: { [key: string]: string | undefined };
  }) => {
    const resolvedUrl = new URL(this.getApiUrl() + props.urlContext).toString();
    let authHeader: Record<string, string> = {};
    if (this.auth.type === AuthOptionsType.BASIC) {
      const basicAuth = this.auth as BasicAuthOptions;
      authHeader = {
        Authorization: "Basic " + Buffer.from(basicAuth.username + ":" + basicAuth.password).toString("base64"),
      };
    }
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
    return `https://api.${this.domain}/2.0`;
  };
  getAuthedUser = () => {
    return this.request({ urlContext: "/user" });
  };
  getRepositoryContents = (args: GetRepositoryContentsArgsType) => {
    return this.request({
      urlContext: `/repositories/${args.workspace}/${args.repository}/src/${args.ref}/${args.path}${
        args.meta ? "?format=meta" : ""
      }`,
      method: "get",
    });
  };
  createRepo = (args: CreateRepoArgsType) => {
    return this.request({
      urlContext: `/repositories/${args.workspace}/${args.name}`,
      method: "post",
      body: JSON.stringify({
        is_private: args.isPrivate,
        name,
      }),
    });
  };
  pushEmptyCommit = (args: PushEmptyCommitArgsType) => {
    const formData: FormData = new FormData();
    formData.append("branch", args.branch);
    formData.append("message", `${this.appName} Initial Push`);
    return this.request({
      urlContext: `/repositories/${args.workspace}/${args.repository}/src`,
      method: "post",
      body: formData,
      extraHeaders: {
        /* Override default settings to force browser to decide content-type for itself.
         Important to overcome boundary-related issue
         https://stackoverflow.com/questions/39280438/fetch-missing-boundary-in-multipart-form-data-post
        */
        "Content-Type": undefined,
      },
    });
  };
  getSnippet = (args: GetSnippetArgsType) => {
    return this.request({
      urlContext: `/snippets/${args.workspace}/${args.snippetId}`,
      method: "get",
    });
  };
  createSnippet = (args: CreateSnippetArgsType) => {
    const formData: FormData = new FormData();
    for (const key in args.files) {
      formData.append("file", new File([args.files[key].content], key, { type: "text/plain" }));
    }
    formData.append("title", args.title);
    formData.append("is_private", args.isPrivate.toString());
    return this.request({
      urlContext: `/snippets/${args.workspace}`,
      method: "post",
      body: formData,
      extraHeaders: {
        /* Override default settings to force browser to decide content-type for itself.
         Important to overcome boundary-related issue
         https://stackoverflow.com/questions/39280438/fetch-missing-boundary-in-multipart-form-data-post
        */
        "Content-Type": undefined,
      },
    });
  };
  listWorkspaces = () => {
    return this.request({
      urlContext: "/user/permissions/workspaces",
      method: "get",
    });
  };
}

export function useBitbucketClient(authSession: AuthSession | undefined): BitbucketClientApi {
  const authProviders = useAuthProviders();
  const { env } = useEnv();

  return useMemo(() => {
    if (authSession?.type !== "git") {
      return new BitbucketClient({ appName: env.KIE_SANDBOX_APP_NAME });
    }

    const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
    if (authProvider?.type !== "bitbucket") {
      return new BitbucketClient({ appName: env.KIE_SANDBOX_APP_NAME });
    }

    return new BitbucketClient({
      appName: env.KIE_SANDBOX_APP_NAME,
      domain: authProvider.domain,
      auth: {
        type: AuthOptionsType.BASIC,
        username: authSession.login,
        password: authSession.token,
      },
    });
  }, [authProviders, authSession, env.KIE_SANDBOX_APP_NAME]);
}
