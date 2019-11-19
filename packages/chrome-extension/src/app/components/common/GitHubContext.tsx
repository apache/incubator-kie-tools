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

import * as Octokit from "@octokit/rest";
import * as React from "react";
import { useContext, useEffect, useState } from "react";

export const GitHubContext = React.createContext<{
  octokit: Octokit;
  setToken: (token: string) => void;
  token?: string;
}>({} as any);

export function useGitHubApi() {
  return useContext(GitHubContext);
}

export function setCookie(name: string, value: string) {
  const date = new Date();

  // Set it expire in 10 years
  date.setTime(date.getTime() + 10 * 365 * 24 * 60 * 60);

  // Set it
  document.cookie = name + "=" + value + "; expires=" + date.toUTCString() + "; path=/";
}

export function getCookie(name: string) {
  const value = "; " + document.cookie;
  const parts = value.split("; " + name + "=");

  if (parts.length === 2) {
    return parts
      .pop()!
      .split(";")
      .shift();
  }
}

const cookieName = "github-auth-token";

export const GitHubContextProvider: React.FC<{}> = props => {
  const [ready, setReady] = useState(false);
  const [octokit, setOctokit] = useState(new Octokit());
  const [token, setToken] = useState("");

  useEffect(() => {
    const value = getCookie(cookieName);
    if (value) {
      setOctokit(new Octokit({ auth: value }));
      setToken(value);
      console.info("Token found");
    } else {
      console.info("No token.");
    }
    setReady(true);
  }, []);

  useEffect(() => {
    if (!token) {
      setCookie(cookieName, "");
      setOctokit(new Octokit());
    } else {
      setCookie(cookieName, token);
      setOctokit(new Octokit({ auth: token }));
    }
  }, [token]);

  return (
    <GitHubContext.Provider value={{ token, setToken, octokit }}>{ready && props.children}</GitHubContext.Provider>
  );
};
