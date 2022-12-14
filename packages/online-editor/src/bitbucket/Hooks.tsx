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
  username?: string;
  domain?: string;
  auth?: AuthOptions;
  headers?: Record<string, string>;
}

export interface BitbucketClientApi {
  domain?: string;
  auth?: AuthOptions;
  headers?: Record<string, string>;
  getApiUrl: () => string;
  getAuthedUser: () => Promise<Response>;
  getRepositoryContents: (
    workspace: string,
    name: string,
    ref: string,
    path: string,
    meta: boolean
  ) => Promise<Response>;
  createRepo: (name: string, isPrivate: boolean) => Promise<Response>;
  pushEmptyCommit: (repository: string, branch: string) => Promise<Response>;
  getSnippet: (workspace: string, snippetId: string) => Promise<Response>;
  createSnippet: (
    title: string,
    files: {
      [key: string]: {
        content: string;
      };
    }
  ) => Promise<Response>;
}
export class BitbucketClient implements BitbucketClientApi {
  constructor(options: Options = {}) {
    this.domain = options?.domain ?? "bitbucket.org";
    this.headers = options?.headers ?? {
      "Content-Type": "application/json",
      Accept: "application/json",
    };
    this.auth = options?.auth ?? undefinedAuthOptions;
    this.username = options.username ?? (options?.auth as BasicAuthOptions)?.username;
  }
  auth: AuthOptions;
  domain: string;
  headers: Record<string, string>;
  username?: string;

  request = (props: {
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
  getRepositoryContents = (workspace: string, name: string, ref: string, path: string, meta: boolean) => {
    return this.request({
      urlContext: `/repositories/${workspace}/${name}/src/${ref}/${path}${meta ? "?format=meta" : ""}`,
      method: "get",
    });
  };
  createRepo = (name: string, isPrivate: boolean) => {
    return this.request({
      urlContext: `/repositories/${this.username}/${name}`,
      method: "post",
      body: JSON.stringify({
        is_private: isPrivate,
        name,
      }),
    });
  };
  pushEmptyCommit = (repoName: string, branch: string) => {
    const formData: FormData = new FormData();
    formData.append("branch", branch);
    formData.append("message", "KIE Sandbox Initial Push");
    return this.request({
      urlContext: `/repositories/${this.username}/${repoName}/src`,
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
  getSnippet = (workspace: string, snippetId: string) => {
    return this.request({
      urlContext: `/snippets/${workspace}/${snippetId}`,
      method: "get",
    });
  };
  createSnippet = (
    title: string,
    files: {
      [key: string]: {
        content: string;
      };
    }
  ) => {
    const formData: FormData = new FormData();
    for (const key in files) {
      formData.append("file", new File([files[key].content], key, { type: "text/plain" }));
    }
    formData.append("title", title);
    return this.request({
      urlContext: "/snippets",
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
}

export function useBitbucketClient(authSession: AuthSession | undefined): BitbucketClientApi {
  const authProviders = useAuthProviders();

  return useMemo(() => {
    if (authSession?.type !== "git") {
      return new BitbucketClient();
    }

    const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
    if (authProvider?.type !== "bitbucket") {
      return new BitbucketClient();
    }

    return new BitbucketClient({
      domain: authProvider.domain,
      auth: {
        type: AuthOptionsType.BASIC,
        username: authSession.login,
        password: authSession.token,
      },
    });
  }, [authProviders, authSession]);
}
