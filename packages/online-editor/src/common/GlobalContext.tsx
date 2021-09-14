/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { File, newFile } from "@kie-tooling-core/editor/dist/channel";
import * as React from "react";
import { useContext, useEffect, useMemo, useState } from "react";
import { Octokit } from "@octokit/rest";
import { Routes } from "./Routes";
import { EditorEnvelopeLocator } from "@kie-tooling-core/editor/dist/api";
import { GithubService } from "./GithubService";

const GITHUB_AUTH_TOKEN_COOKIE_NAME = "github-oauth-token-kie-editors";

export enum AuthStatus {
  SIGNED_OUT,
  TOKEN_EXPIRED,
  LOADING,
  SIGNED_IN,
}

export interface GlobalContextType {
  file: File;
  externalFile?: File;
  setFile: React.Dispatch<React.SetStateAction<File>>;
  routes: Routes;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  readonly: boolean;
  senderTabId?: string;
  isChrome: boolean;

  githubAuthStatus: AuthStatus;
  githubOctokit: Octokit;
  githubToken?: string;
  githubUser?: string;
  githubScopes?: string[];
  githubService: GithubService;
  githubAuthService: { reset: () => void; authenticate: (token: string) => Promise<void> };
}

export const GlobalContext = React.createContext<GlobalContextType>({} as any);

export function GlobalContextProvider(props: { externalFile?: File; senderTabId?: string; children: React.ReactNode }) {
  const [githubAuthStatus, setGitHubAuthStatus] = useState(AuthStatus.LOADING);
  const [octokit, setOctokit] = useState<Octokit>(new Octokit());
  const [githubToken, setGitHubToken] = useState<string | undefined>(undefined);
  const [githubUser, setGitHubUser] = useState<string | undefined>(undefined);
  const [githubScopes, setGitHubScopes] = useState<string[] | undefined>(undefined);

  const githubAuthService = useMemo(() => {
    return {
      reset: () => {
        setOctokit(new Octokit());
        setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
        setGitHubToken(undefined);
        setGitHubUser(undefined);
        setGitHubScopes(undefined);
        setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, "");
      },
      authenticate: async (token: string) => {
        try {
          setGitHubAuthStatus(AuthStatus.LOADING);
          const octokit = new Octokit({ auth: token });
          const response = await octokit.users.getAuthenticated();
          await delay(1000);
          setOctokit(octokit);
          setGitHubAuthStatus(AuthStatus.SIGNED_IN);
          setGitHubToken(token);
          setGitHubUser(response.data.login);
          setGitHubScopes(response.headers["x-oauth-scopes"]?.split(", ") ?? []);
          setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, token);
        } catch (e) {
          await delay(1000);
          setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
          throw e;
        }
      },
    };
  }, []);

  useEffect(() => {
    const tokenCookie = getCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME);
    if (!tokenCookie) {
      setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
      return;
    }

    githubAuthService.authenticate(tokenCookie).catch(() => {
      setGitHubAuthStatus(AuthStatus.TOKEN_EXPIRED);
    });
  }, [githubAuthService]);

  //

  const [file, setFile] = useState(newFile("dmn"));
  const routes = useMemo(() => new Routes(), []);
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () => ({
      targetOrigin: window.location.origin,
      mapping: new Map([
        ["bpmn", { resourcesPathPrefix: "gwt-editors/bpmn", envelopePath: "/bpmn-envelope.html" }],
        ["bpmn2", { resourcesPathPrefix: "gwt-editors/bpmn", envelopePath: "/bpmn-envelope.html" }],
        ["dmn", { resourcesPathPrefix: "gwt-editors/dmn", envelopePath: "/dmn-envelope.html" }],
        ["pmml", { resourcesPathPrefix: "", envelopePath: "/pmml-envelope.html" }],
      ]),
    }),
    []
  );

  return (
    <GlobalContext.Provider
      value={{
        ...props,
        readonly: file.isReadOnly,
        editorEnvelopeLocator,
        file,
        setFile,
        routes,
        isChrome: !!window.chrome,

        githubOctokit: octokit,
        githubAuthStatus,
        githubToken,
        githubUser,
        githubScopes,
        githubAuthService,
        githubService: new GithubService(),
      }}
    >
      {props.children}
    </GlobalContext.Provider>
  );
}

export function useGlobals() {
  return useContext(GlobalContext);
}

function getCookie(name: string) {
  const value = "; " + document.cookie;
  const parts = value.split("; " + name + "=");

  if (parts.length === 2) {
    return parts.pop()!.split(";").shift();
  }
}

function setCookie(name: string, value: string) {
  const date = new Date();

  date.setTime(date.getTime() + 10 * 365 * 24 * 60 * 60); // expires in 10 years

  document.cookie = name + "=" + value + "; expires=" + date.toUTCString() + "; path=/";
}

function delay(ms: number) {
  return new Promise<void>((res) => {
    setTimeout(() => {
      res();
    }, ms);
  });
}
