/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as Octokit from "@octokit/rest";
import { getCookie, setCookie } from "./utils";

export const GITHUB_OAUTH_TOKEN_SIZE = 40;
export const GITHUB_TOKENS_URL = "https://github.com/settings/tokens";
export const GITHUB_TOKENS_HOW_TO_URL =
  "https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line";

const GITHUB_AUTH_TOKEN_COOKIE_NAME = "github-oauth-token-kie-editors";
const EMPTY_TOKEN = "";

export interface FileInfo {
  gitRef: string;
  repo: string;
  org: string;
  path: string;
}

export interface CreateGistArgs {
  filename: string;
  content: string;
  description: string;
  isPublic: boolean;
}

export class GithubService {
  private octokit: Octokit;
  private authenticated: boolean;

  constructor() {
    this.init(false);
  }

  private init(resetToken: boolean): void {
    this.octokit = new Octokit();
    this.authenticated = false;

    if (resetToken) {
      setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, EMPTY_TOKEN);
    }
  }

  private initAuthenticated(token: string): void {
    this.octokit = new Octokit({ auth: token });
    this.authenticated = true;

    setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, token);
  }

  private validateToken(token: string): Promise<boolean> {
    if (!token) {
      return Promise.resolve(false);
    }

    const testOctokit = new Octokit({ auth: token });
    return testOctokit.emojis
      .get({})
      .then(() => Promise.resolve(true))
      .catch(() => Promise.resolve(false));
  }

  public reset(): void {
    this.init(true);
  }

  public async authenticate(token: string = EMPTY_TOKEN) {
    token = this.resolveToken(token);

    if (!(await this.validateToken(token))) {
      this.init(true);
      return false;
    }

    this.initAuthenticated(token);
    return true;
  }

  public resolveToken(token: string = EMPTY_TOKEN): string {
    if (!token) {
      return getCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME) ?? EMPTY_TOKEN;
    }

    return token;
  }

  public isAuthenticated(): boolean {
    return this.authenticated;
  }

  public isGithub(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?github.com.*$/.test(url);
  }

  public isGist(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?gist.github.com.*$/.test(url);
  }

  public extractGistId(url: string): string {
    return url.substr(url.lastIndexOf("/") + 1);
  }

  public retrieveFileInfo(fileUrl: string): FileInfo {
    const split = new URL(fileUrl).pathname.split("/");
    return {
      gitRef: split[4],
      repo: split[2],
      org: split[1],
      path: split.slice(5).join("/")
    };
  }

  private octokitGet(fileInfo: FileInfo) {
    return this.octokit.repos.getContents({
      repo: fileInfo.repo,
      owner: fileInfo.org,
      ref: fileInfo.gitRef,
      path: fileInfo.path,
      headers: {
        "If-None-Match": ""
      }
    });
  }

  public fetchGithubFile(fileUrl: string): Promise<string> {
    const fileInfo = this.retrieveFileInfo(fileUrl);

    return this.octokitGet(fileInfo)
      .then(res => atob((res.data as any).content))
      .catch(e => {
        console.debug(`Error fetching ${fileInfo.path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
        return fetch(
          `https://raw.githubusercontent.com/${fileInfo.org}/${fileInfo.repo}/${fileInfo.gitRef}/${fileInfo.path}`
        ).then(res => (res.ok ? res.text() : Promise.reject("Not able to retrieve file content from Github.")));
      });
  }

  public checkGithubFileExistence(fileUrl: string): Promise<boolean> {
    const fileInfo = this.retrieveFileInfo(fileUrl);

    return this.octokitGet(fileInfo)
      .then(res => true)
      .catch(octokitError => {
        return fetch(
          `https://raw.githubusercontent.com/${fileInfo.org}/${fileInfo.repo}/${fileInfo.gitRef}/${fileInfo.path}`
        )
          .then(res => res.ok)
          .catch(fetchError => false);
      });
  }

  public createGist(args: CreateGistArgs): Promise<string> {
    if (!this.isAuthenticated()) {
      return Promise.reject("User not authenticated.");
    }

    const gistContent: any = {
      description: args.description,
      public: args.isPublic,
      files: {
        [args.filename]: {
          content: args.content
        }
      }
    };

    return this.octokit.gists
      .create(gistContent)
      .then(response => response.data.files[args.filename].raw_url)
      .catch(e => Promise.reject("Not able to create gist on Github."));
  }

  public getGistRawUrlFromId(gistId: string): Promise<string> {
    return this.octokit.gists
      .get({ gist_id: gistId })
      .then(response => {
        const filename = Object.keys(response.data.files)[0];
        return response.data.files[filename].raw_url;
      })
      .catch(e => Promise.reject("Not able to get gist from Github."));
  }
}
