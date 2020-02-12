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

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useGitHubApi } from "./GitHubContext";
import * as Octokit from "@octokit/rest";
import { useGlobals } from "./GlobalContext";

const GITHUB_OAUTH_TOKEN_SIZE = 40;

export function KogitoMenu() {
  const gitHubApi = useGitHubApi();
  const isAuthenticated = !!gitHubApi.token;

  const inputRef = useRef<HTMLInputElement>(null);

  const [isWholeMenuOpen, setWholeMenuOpen] = useState(!isAuthenticated);
  const [isInfoPopOverOpen, setInfoPopOverOpen] = useState(false);
  const [potentialToken, setPotentialToken] = useState("");

  async function updateToken(token?: string) {
    const validToken = await tokenIsValid(token);

    if (validToken) {
      gitHubApi.setToken(token!);
      setPotentialToken("");
    } else {
      gitHubApi.setToken("");
      setWholeMenuOpen(true);
    }

    return validToken;
  }

  useEffect(() => {
    updateToken(gitHubApi.token).then(() => {
      console.debug("Checked GitHub token.");
    });
  }, []);

  const onPaste = useCallback(e => {
    const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
    setPotentialToken(token);
    setTimeout(async () => {
      const wasValid = await updateToken(token);
      if (wasValid) {
        setTimeout(() => setWholeMenuOpen(false), 2000);
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

  const toggleInfoPopOver = useCallback(() => {
    setInfoPopOverOpen(!isInfoPopOverOpen);
  }, [isInfoPopOverOpen]);

  const toggleMenu = useCallback(() => {
    setWholeMenuOpen(!isWholeMenuOpen);
  }, [isWholeMenuOpen]);

  const tokenToDisplay = obfuscate(gitHubApi.token || potentialToken);
  const globals = useGlobals();

  return (
    <>
      {isWholeMenuOpen && (
        <>
          {!isAuthenticated && (
            <>
              <a
                target={"blank"}
                className="Header-link mr-0 mr-lg-3 py-2 py-lg-0"
                href="https://github.com/settings/tokens"
              >
                Create token
              </a>
              <div style={{ position: "relative" }}>
                <a
                  className="info-icon-container Header-link mr-0 mr-lg-3 py-2 py-lg-0"
                  href="#"
                  onClick={toggleInfoPopOver}
                >
                  i
                </a>
                {isInfoPopOverOpen && (
                  <div className={"info-popover"}>
                    <h3>Tokens are only stored locally as cookies.</h3>
                    <p>We never store or share your token with anyone.</p>
                    <hr />
                    <p>
                      We use your GitHub OAuth Token to provide a better experience while using custom editors. The
                      official GitHub API has a throttling mechanism with a fairly low threshold for unauthenticated
                      requests.
                    </p>
                    <p>
                      By authenticating with your OAuth Token we are able to avoid delays when fetching recently updated
                      files and also provide features that need to read from your repositories, like Work Item
                      Definitions on BPMN diagrams.
                    </p>
                    <p>
                      <b>
                        <u>NOTE:</u>
                        &nbsp;
                      </b>
                      <b>For public repositories, no special permissions are required</b>. In fact, you can generate a
                      Token without ticking any checkbox. For private repositories, however, you should provide a Token
                      with the <b>'repo'</b> permission.
                    </p>
                  </div>
                )}
              </div>
            </>
          )}
          <label style={{ position: "relative" }}>
            <input
              className={"kogito-github-token-input form-control input-sm " + (isAuthenticated ? "authenticated" : "")}
              placeholder={"Paste your token here..."}
              maxLength={GITHUB_OAUTH_TOKEN_SIZE}
              autoFocus={true}
              ref={inputRef}
              disabled={isAuthenticated}
              value={tokenToDisplay}
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
        className={`kogito-menu-icon ${isAuthenticated ? "authenticated" : ""}`}
        src={globals.extensionIconUrl}
        onClick={toggleMenu}
      />
    </>
  );
}

function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
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
