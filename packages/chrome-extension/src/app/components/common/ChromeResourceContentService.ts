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

import { ResourceContent, ResourceContentService, ResourcesList } from "@kogito-tooling/core-api";
import { fetchFile } from "../../github/api";
import * as minimatch from "minimatch";
import { RepoInfo } from "./GithubInfo";
import Octokit = require("@octokit/rest");

class OctokitResponse {
  public data: GithubTreeResponse;
}

class GithubAsset {
  public type: string;
  public path: string;
  public mode: string;
  public sha: string;
  public url: string;
}

class GithubTreeResponse {
  public sha: string;
  public url: string;
  public tree: GithubAsset[];
}

class ChromeResourceContentService implements ResourceContentService {
  private readonly repoInfo: RepoInfo;
  private readonly octokit: Octokit;

  constructor(octokit: Octokit, repoInfo: RepoInfo) {
    this.octokit = octokit;
    this.repoInfo = repoInfo;
  }

  public read(uri: string): Promise<ResourceContent | undefined> {
    return fetchFile(this.octokit, this.repoInfo.owner, this.repoInfo.repo, this.repoInfo.gitref, uri)
      .then(assetContent => new ResourceContent(uri, assetContent))
      .catch(e => {
        console.debug(e);
        console.debug(`Error retrieving content from URI ${uri}`);
        return undefined;
      });
  }

  public list(pattern: string): Promise<ResourcesList> {
    return this.octokit.git
      .getTree({
        headers: {
          "cache-control": "no-cache"
        },
        recursive: "1",
        tree_sha: this.repoInfo.gitref,
        ...this.repoInfo
      })
      .then((v: OctokitResponse) => {
        const filteredPaths = v.data.tree.filter(file => file.type === "blob").map(file => file.path);
        const result = minimatch.match(filteredPaths, pattern);
        return new ResourcesList(pattern, result);
      })
      .catch(e => {
        console.debug(`Error retrieving file list for pattern ${pattern}`);
        return new ResourcesList(pattern, []);
      });
  }
}

const resourceContentServiceFactory = {
  create: (octokit: Octokit, repoInfo: RepoInfo) => {
    return new ChromeResourceContentService(octokit, repoInfo);
  }
};

export { resourceContentServiceFactory };
