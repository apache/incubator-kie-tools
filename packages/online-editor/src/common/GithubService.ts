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
const GIST_RAW_URL = "gist.githubusercontent.com/";

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

interface UpdateGistArgs {
  filename: string;
  content: string;
}

interface CurrentGist {
  id: string;
  filename: string;
}

export enum UpdateGistErrors {
  INVALID_CURRENT_GIST,
  INVALID_GIST_FILENAME
}

export class GithubService {
  private octokit: Octokit;
  private authenticated: boolean;
  private userLogin: string;
  private currentGist: CurrentGist | undefined;

  constructor() {
    this.init(false);
  }

  private init(resetToken: boolean): void {
    this.octokit = new Octokit();
    this.authenticated = false;
    this.userLogin = "";

    if (resetToken) {
      setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, EMPTY_TOKEN);
    }
  }

  private initAuthenticated(token: string): void {
    this.octokit = new Octokit({ auth: token });
    this.authenticated = true;

    setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, token);
  }

  private getAuthenticateUser(token: string): Promise<string | undefined> {
    if (!token) {
      return Promise.resolve(undefined);
    }

    const testOctokit = new Octokit({ auth: token });
    return testOctokit.users
      .getAuthenticated()
      .then(res => (this.hasGistScope(res.headers) ? res.data.login : undefined))
      .catch(() => undefined);
  }

  public reset(): void {
    this.init(true);
  }

  public async authenticate(token: string = EMPTY_TOKEN) {
    token = this.resolveToken(token);

    const userLogin = await this.getAuthenticateUser(token);
    if (!userLogin) {
      this.init(true);
      return false;
    }

    this.userLogin = userLogin;
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

  public getLogin(): string {
    return this.userLogin;
  }

  public getCurrentGist(): CurrentGist | undefined {
    return this.currentGist;
  }

  public isGithub(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?github.com.*$/.test(url);
  }

  public isGithubRaw(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(raw\.)?githubusercontent.com.*$/.test(url);
  }

  public isGist(url: string): boolean {
    return this.isGistDefault(url) || this.isGistRaw(url);
  }

  public isGistDefault(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?gist.github.com.*$/.test(url);
  }

  public isGistRaw(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?gist.githubusercontent.com.*$/.test(url);
  }

  public extractGistId(url: string): string {
    return url.substr(url.lastIndexOf("/") + 1);
  }

  public extractGistIdFromRawUrl(url: string): string {
    return url.split(GIST_RAW_URL)[1].split("/")[1];
  }

  public extractUserLoginFromFileUrl(url: string): string {
    return url.split("githubusercontent.com")[1].split("/")[1];
  }

  public removeCommitHashFromGistRawUrl(rawUrl: string): string {
    const gistRawUrlElements = rawUrl.split(GIST_RAW_URL)[1].split("/");
    gistRawUrlElements.splice(3, 1);
    return `https://${GIST_RAW_URL}${gistRawUrlElements.join("/")}`;
  }

  public extractGistFilename(url: string): string | undefined {
    if (url.lastIndexOf("#") > -1) {
      const filename = url.substr(url.lastIndexOf("#") + 1).replace("file-", "");
      return filename.substr(0, filename.lastIndexOf("-")) + "." + filename.substr(filename.lastIndexOf("-") + 1);
    }
    return;
  }

  public extractGistFilenameFromRawUrl(url: string): string {
    // The gist raw URL is encoded two times.
    const decodedUri = decodeURI(decodeURI(url));
    return decodedUri.substr(decodedUri.lastIndexOf("/") + 1);
  }

  public hasGistScope(headers: any) {
    return headers["x-oauth-scopes"].split(", ").indexOf("gist") > -1;
  }

  public retrieveFileInfo(fileUrl: string): FileInfo {
    const split = new URL(fileUrl).pathname.split("/");

    if (this.isGithub(fileUrl)) {
      return {
        gitRef: split[4],
        repo: split[2],
        org: split[1],
        path: split.slice(5).join("/")
      };
    }

    // GitHub Raw
    return {
      gitRef: split[3],
      repo: split[2],
      org: split[1],
      path: split.slice(4).join("/")
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
    return this.authenticate().then(() =>
      this.octokitGet(fileInfo)
        .then(res => atob((res.data as any).content))
        .catch(e => {
          console.debug(`Error fetching ${fileInfo.path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
          return fetch(
            `https://raw.githubusercontent.com/${fileInfo.org}/${fileInfo.repo}/${fileInfo.gitRef}/${fileInfo.path}`
          ).then(res => {
            return res.ok ? res.text() : Promise.reject("Not able to retrieve file content from GitHub.");
          });
        })
    );
  }

  public fetchGistFile(fileUrl: string): Promise<string> {
    const gistId = this.extractGistIdFromRawUrl(fileUrl);
    const filename = this.extractGistFilenameFromRawUrl(fileUrl);

    return this.octokit.gists.get({ gist_id: gistId }).then(response => {
      this.currentGist = { filename, id: gistId };
      return (response.data as any).files[filename].content;
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
      .then(response => this.removeCommitHashFromGistRawUrl((response.data as any).files[args.filename].raw_url))
      .catch(e => Promise.reject("Not able to create gist on GitHub."));
  }

  public async updateGist(args: UpdateGistArgs) {
    const getResponse = await this.octokit.gists.get({ gist_id: this.currentGist!.id });

    if (!Object.keys((getResponse.data as any).files).includes(this.currentGist!.filename)) {
      return UpdateGistErrors.INVALID_CURRENT_GIST;
    }

    if (
      args.filename !== this.currentGist!.filename &&
      Object.keys((getResponse.data as any).files).includes(args.filename)
    ) {
      return UpdateGistErrors.INVALID_GIST_FILENAME;
    }

    const updateResponse = await this.octokit.gists.update({
      gist_id: this.currentGist!.id,
      files: {
        [this.currentGist!.filename]: {
          content: args.content,
          filename: args.filename
        }
      }
    });

    return this.removeCommitHashFromGistRawUrl((updateResponse.data as any).files[args.filename].raw_url);
  }

  public async getGistRawUrlFromId(gistId: string, gistFilename: string | undefined): Promise<string> {
    const { data } = await this.octokit.gists.get({ gist_id: gistId });
    const filename = gistFilename ? gistFilename : Object.keys(data.files)[0];
    return this.removeCommitHashFromGistRawUrl((data as any).files[filename].raw_url);
  }

  public getGithubRawUrl(fileUrl: string): Promise<string> {
    const fileInfo = this.retrieveFileInfo(fileUrl);

    return this.octokitGet(fileInfo)
      .then(res => (res.data as any).download_url)
      .catch(e => Promise.reject("Not able to get raw URL from GitHub."));
  }
}
