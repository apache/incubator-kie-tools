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
import { useCallback, useEffect, useRef, useState } from "react";
import { useGitHubApi } from "./GitHubContext";

export function KogitoMenu() {
  const gitHubApi = useGitHubApi();
  const isAuthenticated = !!gitHubApi.token;

  const inputRef = useRef<HTMLInputElement>(null);

  const [potentialToken, setPotentialToken] = useState("");

  const [isOpen, setOpen] = useState(!isAuthenticated);

  async function updateToken(token?: string) {
    const validToken = await tokenIsValid(token);

    if (validToken) {
      gitHubApi.setToken(token!);
      setPotentialToken("");
    } else {
      gitHubApi.setToken("");
      setOpen(true);
    }

    return validToken;
  }

  useEffect(() => {
    updateToken(gitHubApi.token).then(() => {
      console.debug("Checked GitHub token.");
    });
  }, []);

  const onPaste = useCallback(e => {
    const token = e.clipboardData.getData("text/plain").slice(0, 40);
    setPotentialToken(token);
    setTimeout(async () => {
      const wasValid = await updateToken(token);
      if (wasValid) {
        setTimeout(() => setOpen(false), 2000);
      }
      inputRef.current!.setSelectionRange(0, 0);
    }, 0);
  }, []);

  const onReset = useCallback(() => {
    gitHubApi.setToken("");
    setPotentialToken("");
    setTimeout(() => {
      inputRef.current!.focus();
    }, 0);
  }, []);

  const toggleMenu = useCallback(() => {
    setOpen(!isOpen);
  }, [isOpen]);

  return (
    <>
      {isOpen && (
        <>
          {!isAuthenticated && (
            <a
              target={"blank"}
              className="Header-link mr-0 mr-lg-3 py-2 py-lg-0 border-top border-lg-top-0 border-white-fade-15"
              href="https://github.com/settings/tokens"
            >
              Create token
            </a>
          )}
          <label style={{ position: "relative" }}>
            <input
              maxLength={40}
              autoFocus={true}
              ref={inputRef}
              disabled={isAuthenticated}
              className={"kogito-github-token-input form-control input-sm " + (isAuthenticated ? "authenticated" : "")}
              placeholder={"Paste your token here..."}
              value={gitHubApi.token || potentialToken}
              onPaste={onPaste}
              onChange={() => {
                /**/
              }}
            />
            {isAuthenticated && <b className={"icon tick"} />}
            {!!potentialToken && <b className={"icon cross"} />}
          </label>
          <button className={"btn btn-sm"} onClick={onReset}>
            Reset
          </button>
        </>
      )}
      <img
        onClick={toggleMenu}
        className={`kogito-menu-icon ${isAuthenticated ? "authenticated" : ""}`}
        src={"https://karinavarelame.files.wordpress.com/2019/08/kogito_icon_rgb_color_default_256px-1.png?w=30"}
      />
    </>
  );
}

async function tokenIsValid(token?: string) {
  if (!token) {
    return false;
  }

  const testOctokit = new Octokit({ auth: token });
  return await testOctokit.emojis
    .get({})
    .then(() => true)
    .catch(() => false);
}
