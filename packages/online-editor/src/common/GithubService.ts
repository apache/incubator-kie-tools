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

export interface FileInfo {
  gitRef: string;
  repo: string;
  org: string;
  path: string;
}
export class GithubService {
  private readonly octokit: Octokit;

  constructor() {
    this.octokit = new Octokit();
  }

  public isGithub(url: string): boolean {
    return /^(http:\/\/|https:\/\/)?(www\.)?github.com.*$/.test(url);
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

  public fetchGithubFile(fileUrl: string): Promise<string> {
    const fileInfo = this.retrieveFileInfo(fileUrl);

    return this.octokit.repos
      .getContents({
        repo: fileInfo.repo,
        owner: fileInfo.org,
        ref: fileInfo.gitRef,
        path: fileInfo.path,
        headers: {
          "If-None-Match": ""
        }
      })
      .then(res => atob((res.data as any).content))
      .catch(e => {
        console.debug(`Error fetching ${fileInfo.path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
        return fetch(
          `https://raw.githubusercontent.com/${fileInfo.org}/${fileInfo.repo}/${fileInfo.gitRef}/${fileInfo.path}`
        ).then(res => (res.ok ? res.text() : Promise.reject("Not able to retrieve file content from Github.")));
      });
  }
}
