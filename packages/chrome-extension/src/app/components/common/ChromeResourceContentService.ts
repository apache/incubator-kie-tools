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

import { ResourceContentService, ResourceContent } from "@kogito-tooling/core-api";
import { ResourcesList } from "@kogito-tooling/core-api";
import { fetchFile } from "../../github/api";
import * as minimatch from "minimatch";
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

class RepoInfo {
  public owner: string;
  public repo: string;
  public gitref: string;
}

class ChromeResourceContentService implements ResourceContentService {

  private octokit: Octokit;

  constructor(octokit: Octokit) {
    this.octokit = octokit;
  }

  public read(uri: string): Promise<ResourceContent | undefined> {
    const repoInfo = this.repoInfo();

    return fetchFile(
      this.octokit,
      repoInfo.owner,
      repoInfo.repo,
      repoInfo.gitref,
      uri
    ).then(assetContent => new ResourceContent(uri, assetContent))
      .catch(e => {
        console.debug(e);
        console.debug(`Error retrieving content from URI ${uri}`);
        return undefined;
      }); 
  }

  public list(pattern: string): Promise<ResourcesList> {
    const repoInfo = this.repoInfo();

    return this.octokit.git.getTree({
      headers: {
        "cache-control": "no-cache"
      },
      recursive: "1",
      tree_sha: repoInfo.gitref,
      ...repoInfo
    }).then((v: OctokitResponse) => {
      const filteredPaths = v.data.tree.filter(file => file.type === 'blob').map(file => file.path);
      const result = minimatch.match(filteredPaths, pattern);
      return new ResourcesList(pattern, result);
    }).catch(e => {
      console.debug(`Error retrieving file list for pattern ${pattern}`)
      return new ResourcesList(pattern, []);
    });
  }

  private repoInfo(): RepoInfo {
    const info = window.location.pathname.split('/');
    if (info.length >= 5) {
      return {
        owner: info[1],
        repo: info[2],
        gitref: info[4]
      };
    }
    throw new Error("Not able to retrieve repository information.");
  }

}

const resourceContentServiceFactory = {
  create: (octokit: Octokit) => {
    return new ChromeResourceContentService(octokit);
  }
}

export { resourceContentServiceFactory };