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

import {
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourceContentService,
  ResourceListOptions,
  ResourcesList,
} from "@kogito-tooling/channel-common-api";
import { fetchFile } from "../../github/api";
import * as minimatch from "minimatch";
import { RepoInfo } from "./RepoInfo";
import { Octokit } from "@octokit/rest";

class ChromeResourceContentService implements ResourceContentService {
  private readonly repoInfo: RepoInfo;
  private readonly octokit: Octokit;

  constructor(octokit: Octokit, repoInfo: RepoInfo) {
    this.octokit = octokit;
    this.repoInfo = repoInfo;
  }

  public get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    opts = opts ?? { type: ContentType.TEXT };
    return fetchFile(this.octokit, this.repoInfo.owner, this.repoInfo.repo, this.repoInfo.gitref, path, opts!.type)
      .then((resourceContent) => new ResourceContent(path, resourceContent, opts!.type))
      .catch((e) => {
        console.debug(e);
        console.debug(`Error retrieving content from URI ${path}`);
        return undefined;
      });
  }

  public list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    return this.octokit.git
      .getTree({
        recursive: "1",
        tree_sha: this.repoInfo.gitref,
        ...this.repoInfo,
      })
      .then((v) => {
        const filteredPaths = v.data.tree.filter((file) => file.type === "blob").map((file) => file.path);
        const result = minimatch.match(filteredPaths, pattern);
        return new ResourcesList(pattern, result);
      })
      .catch((e) => {
        console.debug(`Error retrieving file list for pattern ${pattern}`);
        return new ResourcesList(pattern, []);
      });
  }
}

export class ResourceContentServiceFactory {
  public createNew(octokit: Octokit, repoInfo: RepoInfo) {
    return new ChromeResourceContentService(octokit, repoInfo);
  }
}
