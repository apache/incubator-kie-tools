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

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { getGitHubApiBaseUrl, useGitHubApi } from "./GitHubContext";
import { Octokit } from "@octokit/rest";
import { useGlobals } from "./GlobalContext";
import { useChromeExtensionI18n } from "../../i18n";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";

const GITHUB_OAUTH_TOKEN_SIZE = 40;

export function KogitoMenu() {
  const gitHubApi = useGitHubApi();
  const isAuthenticated = !!gitHubApi.token;
  const { i18n } = useChromeExtensionI18n();
  const inputRef = useRef<HTMLInputElement>(null);

  const [isWholeMenuOpen, setWholeMenuOpen] = useState(false);
  const [isInfoPopOverOpen, setInfoPopOverOpen] = useState(false);
  const [potentialToken, setPotentialToken] = useState("");

  const updateToken = useCallback(
    async (token?: string) => {
      const validToken = await tokenIsValid(token);
      if (validToken) {
        gitHubApi.setToken(token!);
        setPotentialToken("");
      } else {
        gitHubApi.setToken("");
      }
      return validToken;
    },
    [gitHubApi]
  );

  useEffect(() => {
    updateToken(gitHubApi.token).then(() => {
      console.debug("Checked GitHub token.");
    });
  }, [gitHubApi.token, updateToken]);

  const onPaste = useCallback(
    (e) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
      setPotentialToken(token);
      setTimeout(async () => {
        const wasValid = await updateToken(token);
        if (wasValid) {
          setTimeout(() => setWholeMenuOpen(false), 2000);
        }
        inputRef.current!.setSelectionRange(0, 0);
      }, 0);
    },
    [updateToken, setPotentialToken, setWholeMenuOpen]
  );

  const onReset = useCallback(() => {
    gitHubApi.setToken("");
    setPotentialToken("");
    setTimeout(() => {
      inputRef.current!.focus();
    }, 0);
  }, [gitHubApi]);

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
                href={window.location.origin + "/settings/tokens"}
              >
                {i18n.common.menu.createToken}
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
                    <h3>{i18n.common.menu.tokenInfo.title}</h3>
                    <p>{i18n.common.menu.tokenInfo.disclaimer}</p>
                    <hr />
                    <p>{i18n.common.menu.tokenInfo.explanation}</p>
                    <p>{i18n.common.menu.tokenInfo.whichPermissionUserGive}</p>
                    <p>
                      <b>
                        <u>{i18n.note.toUpperCase()}:</u>&nbsp;
                      </b>
                      <I18nHtml>{i18n.common.menu.tokenInfo.permission}</I18nHtml>
                    </p>
                  </div>
                )}
              </div>
            </>
          )}
          <label style={{ position: "relative" }}>
            <input
              className={"kogito-github-token-input form-control input-sm " + (isAuthenticated ? "authenticated" : "")}
              placeholder={i18n.common.menu.placeYourToken}
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
            {i18n.reset}
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

  const testOctokit = new Octokit({ auth: token, baseUrl: getGitHubApiBaseUrl(window.location.origin) });
  return await testOctokit.emojis
    .get({})
    .then(() => true)
    .catch(() => false);
}
